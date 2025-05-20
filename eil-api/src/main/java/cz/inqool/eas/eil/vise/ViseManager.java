package cz.inqool.eas.eil.vise;

import cz.inqool.eas.common.storage.file.FileDetail;
import cz.inqool.eas.common.storage.file.FileManager;
import cz.inqool.eas.common.storage.file.OpenedFile;
import cz.inqool.eas.eil.author.record.RecordAuthorDetail;
import cz.inqool.eas.eil.record.RecordEssential;
import cz.inqool.eas.eil.record.RecordRepository;
import cz.inqool.eas.eil.record.illustration.IllustrationEssential;
import cz.inqool.eas.eil.record.illustration.IllustrationVise;
import cz.inqool.eas.eil.role.MarcRole;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ViseManager {
    public static final String ILLUSTRATIONS = "Illustrations";

    private String database;
    private RecordRepository recordRepository;
    private TransactionTemplate transactionTemplate;
    private FileManager fileManager;
    private String viseImagesDir;
    private String viseFilelist;
    @Value("${eil.baseUrl}")
    private String baseUrl;

    public void populateAndAlternateDb() {
        alterTable();
        List<IllustrationVise> ills = recordRepository.findIllustrationsWithViseId();
        ills.sort(Comparator.comparingInt(i -> Integer.parseInt(i.getViseFileId())));
        insertBasicData(ills);
        insertIllustrationData(ills);
//        insertMetadataVtable(ills);
        insertMetadataVtableContent(ills);
    }

    public void alterTable() {
        String alterSql = "ALTER TABLE file_metadata ADD COLUMN ";
        String text = " TEXT";
        List<String> columns = List.of("identifier", "printer", "year_from", "year_to", "artist", "publish_place");
        try (Connection conn = this.connect()) {
            for (String column : columns) {
                try {
                    PreparedStatement pstmt = conn.prepareStatement(alterSql + column + text);
                    pstmt.execute();
                } catch (SQLException e) {
                    log.debug("Error during creating column '{}'", column);
                }

            }
        } catch (SQLException e) {
            log.debug("Error during altering SQLite database for vise metadata");
        }
    }

    public void insertIllustrationData(List<IllustrationVise> illustrations) {
        String sql = "UPDATE file_metadata SET identifier = ? , "
                + "printer = ? , "
                + "year_from = ? , "
                + "year_to = ? , "
                + "artist = ? , "
                + "publish_place = ? "
                + "WHERE file_id = ?";

        log.debug("Start setting VISE metadata");
        int size = illustrations.size();
        int count = 0;
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (IllustrationVise illustration : illustrations) {
                pstmt.setString(1, illustration.getIdentifier());
                Optional<RecordAuthorDetail> recordAuthor = illustration.getCoauthors().stream().filter(ca -> ca.getRoles().contains(MarcRole.PRINTER)).findFirst();
                if (recordAuthor.isPresent()) {
                    pstmt.setString(2, recordAuthor.get().getAuthor().getFullName());
                }
                pstmt.setString(3, String.valueOf(illustration.getYearFrom()));
                pstmt.setString(4, String.valueOf(illustration.getYearTo()));
                if (illustration.getMainAuthor() != null) {
                    pstmt.setString(5, illustration.getMainAuthor().getAuthor().getFullName());
                }
                if (!illustration.getPublishingPlaces().isEmpty()) {
                    pstmt.setString(6, illustration.getPublishingPlaces().stream().findFirst().get().getName());
                }
                pstmt.setString(7, illustration.getViseFileId());
                pstmt.executeUpdate();
                count += 1;
                if (count % 100 == 0) {
                    log.info("Setting VISE metadata to {}/{} illustrations", count, size);
                }
            }
        } catch (SQLException e) {
            log.debug("Error during inserting data into SQLite database for vise metadata");
        }
        log.debug("Finished setting VISE metadata");
    }

    private void insertBasicData(List<IllustrationVise> illustrations) {
        String sql = "INSERT INTO file_metadata(file_id, filename) VALUES(?, ?)";

        log.debug("Start setting VISE BASIC metadata");
        int size = illustrations.size();
        int count = 0;
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (IllustrationVise illustration : illustrations) {
                pstmt.setString(1, illustration.getViseFileId());
                pstmt.setString(2, String.valueOf(illustration.getIllustrationScan().getName()));
                pstmt.executeUpdate();
                count += 1;
                if (count % 100 == 0) {
                    log.info("Setting VISE BASIC metadata to {}/{} illustrations", count, size);
                }
            }
        } catch (SQLException e) {
            log.debug("Error during inserting data into SQLite database for vise BASIC metadata");
        }
        log.debug("Finished setting VISE BASIC metadata");
    }

    private void insertMetadataVtable(List<IllustrationVise> illustrations) {
        String sql = "INSERT INTO metadata_vtable(rowid, concatenated_metadata) VALUES(?, ?)";

        log.debug("Start setting VISE vtable metadata");
        int size = illustrations.size();
        int count = 0;
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (IllustrationVise illustration : illustrations) {
                String concatenatedMetadata = concatMetadata(illustration);

                pstmt.setString(1, illustration.getViseFileId());
                pstmt.setString(2, concatenatedMetadata);

                pstmt.executeUpdate();
                count += 1;
                if (count % 100 == 0) {
                    log.info("Setting VISE vtable metadata to {}/{} illustrations", count, size);
                }
            }
        } catch (SQLException e) {
            log.debug("Error during inserting data into SQLite database for vise vtable metadata");
        }
        log.debug("Finished setting VISE vtable metadata");
    }

    private void insertMetadataVtableContent(List<IllustrationVise> illustrations) {
        String sql = "INSERT INTO metadata_vtable_content(docid, c0rowid, c1concatenated_metadata) VALUES(?, ?, ?)";

        log.debug("Start setting VISE vtable content metadata");
        int size = illustrations.size();
        int count = 0;
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (IllustrationVise illustration : illustrations) {
                String concatenatedMetadata = concatMetadata(illustration);

                pstmt.setString(1, String.valueOf(Integer.parseInt(illustration.getViseFileId()) + 1));
                pstmt.setString(2, illustration.getViseFileId());
                pstmt.setString(3, concatenatedMetadata);

                pstmt.executeUpdate();
                count += 1;
                if (count % 100 == 0) {
                    log.info("Setting VISE vtable metadata content to {}/{} illustrations", count, size);
                }
            }
        } catch (SQLException e) {
            log.debug("Error during inserting data into SQLite database for vise vtable content metadata");
        }
        log.debug("Finished setting VISE vtable content metadata");
    }

    private String concatMetadata(IllustrationVise illustration) {
        String concatenatedMetadata = "";
        concatenatedMetadata = concatenatedMetadata.concat(illustration.getIllustrationScan().getName());

        concatenatedMetadata = concatenatedMetadata.concat(" ");
        Optional<RecordAuthorDetail> recordAuthor = illustration.getCoauthors().stream().filter(ca -> ca.getRoles().contains(MarcRole.PRINTER)).findFirst();
        if (recordAuthor.isPresent()) {
            concatenatedMetadata = concatenatedMetadata.concat(recordAuthor.get().getAuthor().getFullName());
        } else {
            concatenatedMetadata = concatenatedMetadata.concat(" ");
        }

        concatenatedMetadata = concatenatedMetadata.concat(" ");
        concatenatedMetadata = concatenatedMetadata.concat(String.valueOf(illustration.getYearFrom()));

        concatenatedMetadata = concatenatedMetadata.concat(" ");
        concatenatedMetadata = concatenatedMetadata.concat(String.valueOf(illustration.getYearTo()));

        concatenatedMetadata = concatenatedMetadata.concat(" ");
        if (illustration.getMainAuthor() != null) {
            concatenatedMetadata = concatenatedMetadata.concat(illustration.getMainAuthor().getAuthor().getFullName());
        } else {
            concatenatedMetadata = concatenatedMetadata.concat(" ");
        }

        concatenatedMetadata = concatenatedMetadata.concat(" ");
        if (!illustration.getPublishingPlaces().isEmpty()) {
            concatenatedMetadata = concatenatedMetadata.concat(illustration.getPublishingPlaces().stream().findFirst().get().getName());
        } else {
            concatenatedMetadata = concatenatedMetadata.concat(" ");
        }

        return concatenatedMetadata;
    }

    public Connection connect() {
        // SQLite connection string
        String path = new File(database).getAbsolutePath();
        String url = String.format("jdbc:sqlite:%s", path);
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            log.debug("Error during connecting to SQLite database for VISE metadata");
        }
        return conn;
    }

    public void deleteViseIds() {
        log.debug("STARTED deleting VISE IDs and flags from Illustrations");
        List<RecordEssential> ills = recordRepository.findAllEssential();
        List<RecordEssential> updateBatch = new ArrayList<>();
        int count = 0;
        int size = ills.size();
        for (RecordEssential ill : ills) {
            ((IllustrationEssential) ill).setViseFileId(null);
            ((IllustrationEssential) ill).setViseIllScanCopied(null);
            updateBatch.add(ill);
            if (updateBatch.size() >= 100) {
                transactionTemplate.executeWithoutResult(status -> recordRepository.update(updateBatch));
                count += updateBatch.size();
                updateBatch.clear();
                log.debug("Processed {}/{} Illustrations", count, size);
            }
        }
        transactionTemplate.executeWithoutResult(status -> recordRepository.update(updateBatch));
        updateBatch.clear();
        log.debug("FINISHED deleting VISE IDs and flags from Illustrations");
    }

    public void moveFilesToVise() {
        log.debug("Moving Illustration images to VISE dir STARTED");
        List<RecordEssential> ills = recordRepository.findIllustrationScansVise();
        List<RecordEssential> updateBatch = new ArrayList<>();
        Instant now = Instant.now();
        int count = 0;
        int size = ills.size();
        for (RecordEssential ill : ills) {
            OpenedFile openedFile = fileManager.open(((IllustrationEssential) ill).getIllustrationScan().getId());
            cz.inqool.eas.common.storage.file.File file = openedFile.getDescriptor();
            InputStream stream = openedFile.getStream();
            String name = file.getName().replace(",", "").replace("\"", "").replace("'", "");

            File targetFile = new File(String.join(File.separator, viseImagesDir, name));
            try {
                FileUtils.copyInputStreamToFile(stream, targetFile);
                ((IllustrationEssential) ill).setViseIllScanCopied(now);
                updateBatch.add(ill);
            } catch (IOException ioe) {
                log.error("Error moving file '{}' to VISE directory", name);
            }
            if (updateBatch.size() >= 100) {
                transactionTemplate.executeWithoutResult(status -> recordRepository.update(updateBatch));
                count += updateBatch.size();
                updateBatch.clear();
                log.debug("Processed {}/{} Illustrations", count, size);
            }
        }
        transactionTemplate.executeWithoutResult(status -> recordRepository.update(updateBatch));
        updateBatch.clear();
        log.debug("Moving Illustration images to VISE dir FINISHED");
    }

    public void setViseFileIds() {
        log.debug("Setting VISE file ids to Illustrations started");
        List<RecordEssential> illustrations = recordRepository.findNullViseFileIdWithIllustrationScan();

        Map<RecordEssential, String> fileNames = new HashMap<>();
        illustrations.forEach(ill -> {
            FileDetail file = ((IllustrationEssential) ill).getIllustrationScan();
            String name = file.getName().replace(",", "").replace("\"", "").replace("'", "");
            fileNames.put(ill, name);
        });
        List<RecordEssential> updateBatch = new ArrayList<>();
        AtomicInteger count = new AtomicInteger();
        int size = illustrations.size();
        try {
            Path filelistPath = Paths.get(viseFilelist);
            List<String> lines = Files.readAllLines(filelistPath, StandardCharsets.UTF_8)
                    .stream()
                    .map(line -> line.strip()
                            .replace("\n", "")
                            .replace("\r", ""))
                    .collect(Collectors.toList());
            fileNames.forEach((illustration, value) -> {
                int index = lines.indexOf(value);
                if (index > -1) {
                    ((IllustrationEssential) illustration).setViseFileId(Integer.toString(index));
                    updateBatch.add(illustration);
                } else {
                    log.info("Setting VISE file id FAILED to Illustration '{}' with value '{}'", illustration.getId(), value);
                }
                if (updateBatch.size() >= 100) {
                    transactionTemplate.executeWithoutResult(status -> recordRepository.update(updateBatch));
                    count.addAndGet(updateBatch.size());
                    updateBatch.clear();
                    log.debug("Processed {}/{} Illustrations", count.get(), size);
                }
            });
        } catch (IOException ioe) {
            log.error("Error reading VISE filelist.txt");
        }
        transactionTemplate.executeWithoutResult(status -> recordRepository.update(updateBatch));
        updateBatch.clear();
        log.debug("Setting VISE file ids to Illustrations finished");
    }

    public void resetViseImages() {
        ViseUtils viseUtils = new ViseUtils(baseUrl);
        //delete vise project
        viseUtils.deleteViseProject(ILLUSTRATIONS);
        //remove ids and flags
        deleteViseIds();
        //create vise project again
        viseUtils.createViseProject(ILLUSTRATIONS);
        //copy files
        moveFilesToVise();
        //run index
        viseUtils.startIndexingVise(ILLUSTRATIONS);
        if (viseUtils.fetchIndexStatusLoop(ILLUSTRATIONS)) {
            setViseFileIds();
        }
    }

    @Autowired
    public void setDatabase(@Value("${eil.file-storage-vise.database}") String database) {
        this.database = database;
    }

    @Autowired
    public void setRecordRepository(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    @Autowired
    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    @Autowired
    public void setManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Autowired
    public void setViseImages(@Value("${eil.file-storage-vise.directory}") String viseImagesDir) {
        this.viseImagesDir = viseImagesDir;
    }

    @Autowired
    public void setViseFilelist(@Value("${eil.file-storage-vise.filelist}") String viseFilelist) {
        this.viseFilelist = viseFilelist;
    }
}
