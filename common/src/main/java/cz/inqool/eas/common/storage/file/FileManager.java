package cz.inqool.eas.common.storage.file;

import cz.inqool.eas.common.alog.event.EventBuilder;
import cz.inqool.eas.common.alog.event.EventService;
import cz.inqool.eas.common.antivirus.Scanner;
import cz.inqool.eas.common.antivirus.scan.ScanResult;
import cz.inqool.eas.common.authored.user.UserGenerator;
import cz.inqool.eas.common.authored.user.UserReference;
import cz.inqool.eas.common.domain.Domain;
import cz.inqool.eas.common.domain.DomainService;
import cz.inqool.eas.common.exception.GeneralException;
import cz.inqool.eas.common.exception.VirusFoundException;
import cz.inqool.eas.common.exception.v2.*;
import lombok.Builder;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

import static cz.inqool.eas.common.exception.ExceptionUtils.checked;
import static cz.inqool.eas.common.exception.v2.ExceptionCode.*;
import static cz.inqool.eas.common.utils.AssertionUtils.*;
import static java.nio.file.Files.*;

@SuppressWarnings("JavadocReference")
@Builder
@Slf4j
public class FileManager {
    private static final int DIR_NAME_LENGTH = 2;

    private FileStore store;

    @Setter
    private String directoryPath;

    @Setter
    private Long fileSizeLimit;

    /**
     * Resolves upload file size limit based on specific request.
     *
     * Has priority over {@link #fileSizeLimit} property.
     */
    @Setter
    private Function<MultipartFile, Long> fileSizeLimitFunction;

    @Setter
    private int hierarchicalLevel;

    private Scanner scanner;

    private EventService eventService;

    private EventBuilder eventBuilder;

    @Setter
    private String[] allowedExtensions;

    @Setter
    private String[] forbiddenExtensions;

    /**
     * Stores a new file to the file system storage temporarily from provided Multipart.
     *
     * @param multipart Multipart to store
     * @return created file entity - {@link File}
     * @throws InvalidArgument  if the file size exceeds allowed size limit
     * @throws GeneralException if any other I/O exception occurs
     */
    @Transactional(dontRollbackOn = VirusFoundException.class)
    public File upload(@NonNull MultipartFile multipart) {
        Long sizeLimit = (fileSizeLimitFunction != null) ? fileSizeLimitFunction.apply(multipart) : fileSizeLimit;
        lte(multipart.getSize(), sizeLimit, () -> new InvalidArgument(SIZE_TOO_BIG, "File '" + multipart.getName() + "' exceeded max size limit.")
                .details(details -> details.name(multipart.getName()).property("size", multipart.getSize()).property("sizeLimit", sizeLimit))
                .logAll());

        String name = getFileName(multipart).toLowerCase();

        if (allowedExtensions != null) {
            boolean fail = Arrays.stream(allowedExtensions).noneMatch(name::endsWith);

            if (fail) {
                throw new ExtensionNotAllowedException(UNSUPPORTED_FILE_EXTENSION)
                        .details(details -> details.filename(name).allowedExtensions(Set.of(allowedExtensions)));
            }
        }
        if (forbiddenExtensions != null) {
            boolean fail = Arrays.stream(forbiddenExtensions).anyMatch(name::endsWith);

            if (fail) {
                throw new ExtensionNotAllowedException(UNSUPPORTED_FILE_EXTENSION)
                        .details(details -> details.filename(name).forbiddenExtensions(Set.of(forbiddenExtensions)));
            }
        }

        File file = new File();
        file.setName(name);
        file.setContentType(multipart.getContentType());
        file.setSize(multipart.getSize());
        file.setLevel(hierarchicalLevel);
        file.setPermanent(false);

        checked(() -> {
            Path path = getPath(file.getId());
            createDirectories(path.getParent());
            Files.copy(multipart.getInputStream(), path);
        });

        File storedFile = store.create(file);

        if (scanner != null) {
            OpenedFile openedFile = open(storedFile.getId());

            try (InputStream stream = openedFile.getStream()) {
                ScanResult result = scanner.scanFile(storedFile, stream);
                if (result == ScanResult.VIRUS_FOUND) {
                    store.delete(storedFile.getId());

                    if (eventService != null) {
                        UserReference userReference = UserGenerator.generateValue();
                        eventService.create(eventBuilder.foundVirus(storedFile, userReference));
                    }

                    throw new VirusFoundException("Found virus in uploaded file");
                }
            } catch (IOException e) {
                log.error("Error closing stream.", e);
                return storedFile;
            }

        }

        return storedFile;
    }

    /**
     * Store a new file to the file system storage permanently.
     *
     * @param name        name of the file
     * @param size        size of the file
     * @param contentType content type
     * @return created file entity - {@link File}
     * @throws InvalidArgument  if the file size exceeds allowed size limit
     * @throws GeneralException if any other I/O exception occurs
     */
    @Transactional(dontRollbackOn = GeneralException.class)
    public File store(String name, long size, String contentType, InputStream stream) {
        File file = new File();
        file.setName(name);
        file.setContentType(contentType);
        file.setSize(size);
        file.setLevel(hierarchicalLevel);
        file.setPermanent(true);

        checked(() -> {
            Path path = getPath(file.getId());
            createDirectories(path.getParent());
            Files.copy(stream, path);
        });

        return store.create(file);
    }

    /**
     * Changes file from being stored temporarily to being stored permanently.
     *
     * @param file temporary stored file reference
     * @return file reference of permanently stored file
     * @throws ForbiddenOperation if file is already stored permanently
     */
    @Transactional
    public File storeFromUpload(@NonNull File file) {
        eq(file.permanent, false, () -> new ForbiddenObject(FILE_ALREADY_PERMANENTLY_STORED)
                .details(details -> details.id(file.getId()).property("type", File.class.getSimpleName()))
                .debugInfo(info -> info.clazz(File.class).property("permanent", file.isPermanent())));

        file.setPermanent(true);
        return store.update(file);
    }

    /**
     * Create a physical copy of file with given ID
     */
    @Transactional
    public File copy(@NonNull String fileId) {
        OpenedFile openedFile = open(fileId);
        File openedFileDescriptor = openedFile.getDescriptor();

        return store(
                openedFileDescriptor.getName(),
                openedFileDescriptor.getSize(),
                openedFileDescriptor.getContentType(),
                openedFile.getStream()
        );
    }

    /**
     * Deletes a file.
     *
     * @param id Id of file entity to be deleted
     * @return deleted file entity - {@link File}
     * @throws GeneralException if any other I/O exception occurs
     */
    @Transactional
    public File remove(@NonNull String id) {
        File file = store.delete(id);
        notNull(file, () -> new MissingObject(ENTITY_NOT_EXIST)
                .details(details -> details.id(id).property("type", File.class.getSimpleName()))
                .debugInfo(info -> info.clazz(File.class))
                .logAll());

        deleteFromFileSystem(file);

        return file;
    }

    /**
     * Deletes files.
     * <p>
     * Non-existent entities are silently skipped.
     *
     * @param id Ids of file entities to be deleted
     * @return deleted file entities - {@link File}s
     * @throws GeneralException if any other I/O exception occurs
     */
    @Transactional
    public Collection<File> remove(@NonNull Collection<String> ids) {
        Collection<File> files = store.delete(ids);

        files.forEach(this::deleteFromFileSystem);

        return files;
    }

    /**
     * Discards temporary file from file system.
     *
     * @param id Id of file to be discarded
     * @return deleted file entity - {@link File}
     * @throws ForbiddenOperation if file is stored permanently
     */
    @Transactional
    public File discardUpload(@NonNull String id) {
        File file = store.delete(id);
        notNull(file, () -> new MissingObject(ENTITY_NOT_EXIST)
                .details(details -> details.id(id).property("type", File.class.getSimpleName()))
                .debugInfo(info -> info.clazz(File.class))
                .logAll());
        eq(file.permanent, false, () -> new ForbiddenObject(FILE_ALREADY_PERMANENTLY_STORED)
                .details(details -> details.id(file.getId()).property("type", File.class.getSimpleName()))
                .debugInfo(info -> info.clazz(File.class).property("permanent", file.isPermanent())));

        deleteFromFileSystem(file);

        return file;
    }

    /**
     * Open specified file in READ mode.
     *
     * @param id Id of file to be opened
     * @return initialized input stream ready to be read from with file descriptor
     */
    @Transactional
    public OpenedFile open(String id) {
        File file = store.find(id);
        notNull(file, () -> new MissingObject(ENTITY_NOT_EXIST)
                .details(details -> details.id(id).property("type", File.class.getSimpleName()))
                .debugInfo(info -> info.clazz(File.class))
                .logAll());

        Path path = getPath(file.getId());

        InputStream stream = checked(() -> newInputStream(path, StandardOpenOption.READ));

        return new OpenedFile(file, stream);
    }

    /**
     * Open specified file as Spring resource.
     *
     * @return initialized input stream ready to be read from with file descriptor
     */
    @Transactional
    public FileResource openAsResource(String id) {
        File file = store.find(id);
        notNull(file, () -> new MissingObject(ENTITY_NOT_EXIST)
                .details(details -> details.id(id).property("type", File.class.getSimpleName()))
                .debugInfo(info -> info.clazz(File.class))
                .logAll());

        Path path = getPath(file.getId());

        InputStream stream = checked(() -> newInputStream(path, StandardOpenOption.READ));

        return new FileResource(file, stream);
    }

    /**
     * Retrieve file descriptor.
     *
     * @param id Id of file to be retrieved
     * @return file descriptor
     */
    @Transactional
    public File get(String id) {
        File file = store.find(id);
        notNull(file, () -> new MissingObject(ENTITY_NOT_EXIST)
                .details(details -> details.id(id).property("type", File.class.getSimpleName()))
                .debugInfo(info -> info.clazz(File.class))
                .logAll());

        return file;
    }

    /**
     * Compute the path to actual file with given directory hierarchy.
     *
     * @param id id of the file
     * @return computed path
     */
    public Path getPath(String id) {
        String[] path = new String[hierarchicalLevel + 1];
        path[hierarchicalLevel] = id;

        String uuid = id.replaceAll("-", "");
        for (int i = 0; i < hierarchicalLevel; i++) {
            path[i] = uuid.substring(i * DIR_NAME_LENGTH, i * DIR_NAME_LENGTH + DIR_NAME_LENGTH);
        }

        return Paths.get(directoryPath, path);
    }

    /**
     * Returns the actual filename of given multipart file.
     *
     * @return actual file name with extension
     * @throws MissingAttribute if file name is not available
     */
    private String getFileName(MultipartFile file) {
        String filename = file.getOriginalFilename();
        notEmpty(filename, () -> new MissingAttribute(ATTRIBUTE_VALUE_IS_NULL)
                .details(details -> details.property("filename").property("type", MultipartFile.class.getSimpleName()))
                .debugInfo(info -> info.clazz(MultipartFile.class).property("name", file.getName())));

        return FilenameUtils.getName(filename);
    }

    /**
     * Permanently saves uploaded file for object.
     *
     * Should be called in {@link DomainService#preCreateHook(Domain)}
     *
     * @param object   Object
     * @param mapper   Mapper for File attribute
     * @param <ENTITY> Entity Type
     */
    public <ENTITY> void preCreateHook(ENTITY object, Function<ENTITY, File> mapper) {
        File file = mapper.apply(object);

        if (file != null) {
            File content = this.get(file.getId());
            notNull(content, () -> new MissingObject(ENTITY_NOT_EXIST)
                    .details(details -> details.id(file.getId()).property("type", File.class.getSimpleName()))
                    .debugInfo(info -> info.clazz(File.class))
                    .logAll());

            if (!content.isPermanent()) {
                log.debug("Storing '{}'.", file);
                this.storeFromUpload(content);
            }
        }
    }

    /**
     * Permanently saves uploaded files for object.
     *
     * Should be called in {@link DomainService#preCreateHook(Domain)}
     *
     * @param object   Object
     * @param mapper   Mapper for File attribute
     * @param <ENTITY> Entity Type
     */
    public <ENTITY> void preCreateHookCollection(ENTITY object, Function<ENTITY, Collection<File>> mapper) {
        Collection<File> files = mapper.apply(object);

        if (files != null) {
            files.forEach(file -> {
                File content = this.get(file.getId());
                notNull(content, () -> new MissingObject(ENTITY_NOT_EXIST)
                        .details(details -> details.id(file.getId()).property("type", File.class.getSimpleName()))
                        .debugInfo(info -> info.clazz(File.class))
                        .logAll());

                if (!content.isPermanent()) {
                    log.debug("Storing '{}'.", file);
                    this.storeFromUpload(content);
                }
            });
        }
    }

    /**
     * Permanently saves uploaded file for object and removed unused.
     *
     * Should be called in {@link DomainService#preUpdateHook(Domain)}
     *
     * @param object   Object
     * @param old      Old instance
     * @param mapper   Mapper for File attribute
     * @param <ENTITY> Entity Type
     */
    public <ENTITY> void preUpdateHook(ENTITY object, ENTITY old, Function<ENTITY, File> mapper) {
        File file = mapper.apply(object);

        if (file != null) {
            File content = this.get(file.getId());
            notNull(content, () -> new MissingObject(ENTITY_NOT_EXIST)
                    .details(details -> details.id(file.getId()).property("type", File.class.getSimpleName()))
                    .debugInfo(info -> info.clazz(File.class))
                    .logAll());

            if (!content.isPermanent()) {
                log.debug("Storing '{}'.", file);
                this.storeFromUpload(content);
            }
        }

        File oldFile = mapper.apply(old);
        if (oldFile != null) {
            if (!oldFile.equals(file)) {
                log.debug("Removing unused '{}'.", oldFile);
                this.remove(oldFile.getId());
            }
        }
    }

    /**
     * Permanently saves uploaded file for object and removes unused.
     *
     * Should be called in {@link DomainService#preUpdateHook(Domain)}
     *
     * @param object   Object
     * @param old      Old instance
     * @param mapper   Mapper for File attribute
     * @param <ENTITY> Entity Type
     */
    public <ENTITY> void preUpdateHookCollection(ENTITY object, ENTITY old, Function<ENTITY, Collection<File>> mapper) {
        Collection<File> files = mapper.apply(object);

        if (files != null) {
            files.forEach(file -> {
                File content = this.get(file.getId());
                notNull(content, () -> new MissingObject(ENTITY_NOT_EXIST)
                        .details(details -> details.id(file.getId()).property("type", File.class.getSimpleName()))
                        .debugInfo(info -> info.clazz(File.class))
                        .logAll());

                if (!content.isPermanent()) {
                    log.debug("Storing '{}'.", file);
                    this.storeFromUpload(content);
                }
            });
        }

        Collection<File> oldFiles = mapper.apply(old);
        if (oldFiles != null) {
            oldFiles.forEach(oldFile -> {
                if (files != null && !files.contains(oldFile)) {
                    log.debug("Removing unused '{}'.", oldFile);
                    this.remove(oldFile.getId());
                }
            });
        }
    }

    /**
     * Deletes saved file for object.
     *
     * Should be called in {@link DomainService#preDeleteHook(String)}}
     *
     * @param object   Object
     * @param mapper   Mapper for File attribute
     * @param <ENTITY> Entity Type
     */
    public <ENTITY> void preDeleteHook(ENTITY object, Function<ENTITY, File> mapper) {
        File file = mapper.apply(object);

        if (file != null) {
            log.debug("Removing unused '{}'.", file);
            this.remove(file.getId());
        }
    }

    /**
     * Deletes saved file for object.
     *
     * Should be called in {@link DomainService#preDeleteHook(String)}}
     *
     * @param object   Object
     * @param mapper   Mapper for File attribute
     * @param <ENTITY> Entity Type
     */
    public <ENTITY> void preDeleteHookCollection(ENTITY object, Function<ENTITY, Collection<File>> mapper) {
        Collection<File> files = mapper.apply(object);

        if (files != null) {
            files.forEach(file -> {
                log.debug("Removing unused '{}'.", file);
                this.remove(file.getId());
            });
        }
    }

    /**
     * Deletes physical file of provided file entity from the filesystem.
     *
     * @param file file entity for which to remove physical file from the filesystem.
     */
    private void deleteFromFileSystem(@NonNull File file) {
        Path path = getPath(file.getId());

        if (exists(path)) {
            checked(() -> Files.delete(path));
        } else {
            log.warn("File {} not found.", path);
        }
    }

    @Autowired
    public void setStore(FileStore store) {
        this.store = store;
    }

    @Autowired(required = false)
    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }

    @Autowired(required = false)
    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }

    @Autowired(required = false)
    public void setEventBuilder(EventBuilder eventBuilder) {
        this.eventBuilder = eventBuilder;
    }
}
