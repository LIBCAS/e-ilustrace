package cz.inqool.eas.eil.mirador;

import cz.inqool.eas.common.storage.file.File;
import cz.inqool.eas.common.storage.file.FileDetail;
import cz.inqool.eas.common.storage.file.FileManager;
import cz.inqool.eas.common.storage.file.OpenedFile;
import cz.inqool.eas.eil.mirador.dto.*;
import cz.inqool.eas.eil.record.RecordEssential;
import cz.inqool.eas.eil.record.RecordRepository;
import cz.inqool.eas.eil.record.book.Book;
import cz.inqool.eas.eil.record.illustration.Illustration;
import cz.inqool.eas.eil.record.illustration.IllustrationEssential;
import cz.inqool.eas.eil.vise.ViseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MiradorService {
    public static final String RECORD_PATH = "api/eil/record/";
    public static final String CANTALOUPE = "Cantaloupe";

    private RecordRepository recordRepository;
    private FileManager fileManager;
    private String cantaloupeImagesDir;
    private String cantaloupeFilelist;
    @Value("${eil.baseUrl}")
    private String baseUrl;
    private TransactionTemplate transactionTemplate;
    private final RestTemplate restTemplate = new RestTemplate();

    //http://ronallo.com/iiif-workshop-new/presentation-api.html
    //https://github.com/dbmdz/iiif-apis
    public Manifest createManifest(Illustration illustration) {
        List<Canvas> canvases = new ArrayList<>();
        String order = "1";
        String label = buildLabel(illustration);
        if (illustration.getCantaloupeIllScanId() != null && illustration.getIllustrationScan() != null) {
            canvases.add(createCanvas(illustration.getId(), illustration.getIllustrationScan().getName(), order, label));
            order = "2";
        }
        if (illustration.getCantaloupePageScanId() != null && illustration.getPageScan() != null) {
            canvases.add(createCanvas(illustration.getId(), illustration.getPageScan().getName(), order, label));
        }
        String illPath = baseUrl + RECORD_PATH + illustration.getId();

        Sequence sequence = new Sequence(illPath, canvases);
        List<Sequence> sequences = new ArrayList<>();
        sequences.add(sequence);

        return new Manifest(illPath,
                illustration.getTitle(),
                illustration.getPhysicalDescription(),
                sequences);
    }

    public Canvas createCanvas(String id, String filename, String order, String label) {
        InfoJsonDto infoJsonDto = getInfoJson(filename);

        cz.inqool.eas.eil.mirador.dto.Service service = new cz.inqool.eas.eil.mirador.dto.Service(infoJsonDto.getId());

        Resource resource = new Resource(infoJsonDto.getId(), infoJsonDto.getHeight(), infoJsonDto.getHeight(), service);

        String on = baseUrl + RECORD_PATH + id + "/canvas/" + order;
        Image image = new Image(infoJsonDto.getId(), on, resource);
        List<Image> images = new ArrayList<>();
        images.add(image);

        String illPath = baseUrl + RECORD_PATH + id;
        return new Canvas(illPath,
                images,
                infoJsonDto.getHeight(),
                infoJsonDto.getWidth(),
                label,
                order);
    }

    public Manifest createManifest(Book book) {
        List<Canvas> canvases = new ArrayList<>();
        List<Illustration> illustrations = book.getIllustrations().stream().filter(ill -> ill.getCantaloupePageScanId() != null && ill.getPageScan() != null)
                .sorted(Comparator.comparing(Illustration::getIdentifier))
                .collect(Collectors.toList());
        if (!illustrations.isEmpty()) {
            for (int i = 0 ; i < illustrations.size(); i++) {
                Illustration illustration = illustrations.get(i);
                String label = buildLabel(illustration);
                canvases.add(createCanvas(illustration.getId(), illustration.getPageScan().getName(), Integer.toString(i), label));
            }
        }

        String bookPath = baseUrl + RECORD_PATH + book.getId();
        Sequence sequence = new Sequence(bookPath, canvases);
        List<Sequence> sequences = new ArrayList<>();
        sequences.add(sequence);

        return new Manifest(bookPath,
                book.getTitle(),
                book.getPhysicalDescription(),
                sequences);
    }

    public InfoJsonDto getInfoJson(String fileName) {
        return restTemplate.getForObject(baseUrl + "/iiif/2/" + fileName + "/info.json", InfoJsonDto.class);
    }

    public String buildLabel(Illustration illustration) {
        StringBuilder sb = new StringBuilder();
        if (illustration.getTitle() != null) {
            sb.append(illustration.getTitle());
            sb.append(", ");
        }
        if (illustration.getIdentifier() != null) {
            sb.append(illustration.getIdentifier());
            sb.append(", ");
        }
        if (illustration.getMainAuthor() != null) {
            sb.append(illustration.getMainAuthor().getAuthor().getFullName());
        }
        return sb.toString();
    }

    public void resetMiradorImages() {
        ViseUtils viseUtils = new ViseUtils(baseUrl);
        //delete vise project
        viseUtils.deleteViseProject(CANTALOUPE);
        //remove ids and flags
        deleteFromCantaloupe();
        //create vise project again
        viseUtils.createViseProject(CANTALOUPE);
        //copy files
        moveIllsImagesToCantaloupe();
        //run index
        viseUtils.startIndexingVise(CANTALOUPE);
        if (viseUtils.fetchIndexStatusLoop(CANTALOUPE)) {
            setCantaloupeIds();
        }
    }

    public void deleteFromCantaloupe() {
        log.debug("STARTED deleting Cantaloupe IDs and flags from Illustrations");
        List<RecordEssential> ills = recordRepository.findAllEssential();
        List<RecordEssential> updateBatch = new ArrayList<>();
        int count = 0;
        int size = ills.size();
        for (RecordEssential ill : ills) {
            ((IllustrationEssential) ill).setCantaloupePageScanCopied(null);
            ((IllustrationEssential) ill).setCantaloupeIllScanId(null);
            ((IllustrationEssential) ill).setCantaloupeIllScanCopied(null);
            ((IllustrationEssential) ill).setCantaloupePageScanId(null);
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
        log.debug("FINISHED deleting Cantaloupe IDs and flags from Illustrations");
    }

    public void setCantaloupeFlags(RecordEssential ill, boolean isPage, Instant now) {
        String fileId = isPage ? ((IllustrationEssential) ill).getPageScan().getId() : ((IllustrationEssential) ill).getIllustrationScan().getId();
        OpenedFile openedFile = fileManager.open(fileId);
        File file = openedFile.getDescriptor();
        InputStream stream = openedFile.getStream();
        String name = file.getName().replace(",", "").replace("\"", "").replace("'", "");

        java.io.File targetFile = new java.io.File(String.join(java.io.File.separator, cantaloupeImagesDir, name));
        try {
            FileUtils.copyInputStreamToFile(stream, targetFile);
            if (isPage) {
                ((IllustrationEssential) ill).setCantaloupePageScanCopied(now);
            } else {
                ((IllustrationEssential) ill).setCantaloupeIllScanCopied(now);
            }
        } catch (IOException ioe) {
            log.error("Error moving file '{}' to Cantaloupe directory", name);
        }
    }

    public void moveIllsImagesToCantaloupe() {
        log.debug("STARTED moving Illustration images to Cantaloupe dir");
        List<RecordEssential> ills = recordRepository.findIllustrationScans();
        Set<RecordEssential> updateBatch = new HashSet<>();
        Instant now = Instant.now();
        int count = 0;
        int size = ills.size();
        for (RecordEssential ill : ills) {
            if (((IllustrationEssential) ill).getCantaloupeIllScanCopied() == null
                    && ((IllustrationEssential) ill).getIllustrationScan() != null) {
                setCantaloupeFlags(ill,false, now);
            }
            if (((IllustrationEssential) ill).getCantaloupePageScanCopied() == null
                    && ((IllustrationEssential) ill).getPageScan() != null) {
                setCantaloupeFlags(ill, true, now);
            }
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
        log.debug("FINISHED moving Illustration images to Cantaloupe dir");
    }

    public void setCantaloupeIds() {
        log.debug("STARTED setting Illustration Cantaloupe IDs");
        List<RecordEssential> illustrations = recordRepository.findNullCantaloupeFileIds();
        Set<RecordEssential> updateBatch = new HashSet<>();
        List<String> lines;
        int count = 0;
        int size = illustrations.size();
        try {
            Path filelistPath = Paths.get(cantaloupeFilelist);
            lines = Files.readAllLines(filelistPath, StandardCharsets.UTF_8)
                    .stream()
                    .map(line -> line.strip()
                            .replace("\n", "")
                            .replace("\r", ""))
                    .collect(Collectors.toList());
        } catch (IOException ioe) {
            log.error("Error reading Cantaloupe filelist.txt");
            return;
        }
        for (RecordEssential illustration : illustrations) {
            if (((IllustrationEssential) illustration).getCantaloupeIllScanId() == null
                    && ((IllustrationEssential) illustration).getIllustrationScan() != null) {
                setCantaloupeIds(lines, illustration, false);
            }
            if (((IllustrationEssential) illustration).getCantaloupePageScanId() == null
                    && ((IllustrationEssential) illustration).getPageScan() != null) {
                setCantaloupeIds(lines, illustration, true);
            }
            updateBatch.add(illustration);
            if (updateBatch.size() >= 100) {
                transactionTemplate.executeWithoutResult(status -> recordRepository.update(updateBatch));
                count += updateBatch.size();
                updateBatch.clear();
                log.debug("Processed {}/{} Illustrations", count, size);
            }
        }
        transactionTemplate.executeWithoutResult(status -> recordRepository.update(updateBatch));
        updateBatch.clear();
        log.debug("FINISHED setting Illustration Cantaloupe IDs");
    }

    public void setCantaloupeIds(List<String> lines, RecordEssential illustration, boolean isPage) {
        FileDetail file = isPage ?
                ((IllustrationEssential) illustration).getPageScan() : ((IllustrationEssential) illustration).getIllustrationScan();
        String fileName = file.getName().replace(",", "").replace("\"", "").replace("'", "");
        int index = lines.indexOf(fileName);
        if (index > -1) {
            if (isPage) {
                ((IllustrationEssential) illustration).setCantaloupePageScanId(Integer.toString(index));
            } else {
                ((IllustrationEssential) illustration).setCantaloupeIllScanId(Integer.toString(index));
            }
        } else {
            log.info("Setting Cantaloupe Illustration file id FAILED to Illustration '{}' with value '{}'", illustration.getId(), fileName);
        }
    }

    @Autowired
    public void setRecordRepository(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    @Autowired
    public void setManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Autowired
    public void setCantaloupeImagesDir(@Value("${eil.file-storage-cantaloupe.directory}") String cantaloupeImagesDir) {
        this.cantaloupeImagesDir = cantaloupeImagesDir;
    }

    @Autowired
    public void setCantaloupeFilelist(@Value("${eil.file-storage-cantaloupe.filelist}") String cantaloupeFilelist) {
        this.cantaloupeFilelist = cantaloupeFilelist;
    }

    @Autowired
    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }
}
