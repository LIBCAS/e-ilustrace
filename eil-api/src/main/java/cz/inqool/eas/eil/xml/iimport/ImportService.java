package cz.inqool.eas.eil.xml.iimport;

import cz.inqool.eas.eil.record.RecordRepository;
import cz.inqool.eas.eil.xml.MarcCreateProcessor;
import cz.inqool.eas.eil.xml.MarcProcessor;
import cz.inqool.eas.eil.xml.MarcUpdateProcessor;
import cz.inqool.eas.eil.xml.mapper.Utils;
import gov.loc.marc21.slim.CollectionType;
import gov.loc.marc21.slim.RecordType;
import liquibase.repackaged.org.apache.commons.lang3.ArrayUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXB;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ImportService {
    public static final String FULL = "full";

    private MarcCreateProcessor marcCreateProcessor;

    private MarcUpdateProcessor marcUpdateProcessor;

    private RecordRepository recordRepository;

    @Value("${eil.import.directory}")
    private String importDir;
    @Value("${eil.import.archive.book}")
    private String bookPrefix;
    @Value("${eil.import.archive.illustration}")
    private String illPrefix;
    @Value("${eil.import.archive.extension}")
    private String extension;

    @Value("${eil.import.output.illustration}")
    private String illSubdir;
    @Value("${eil.import.output.book}")
    private String bookSubdir;

    @Scheduled(cron = "${eil.cron.import}")
    public void extractAndImportRecords() {
        String outputDir = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault()).format(LocalDate.now());
        List<File> archives = extractArchivesInto(outputDir);
        importBooks(outputDir);
        importIllustrations(outputDir);
        deleteArchives(archives);
    }

    public List<File> extractArchivesInto(String outputDir) {
        List<File> archives = new ArrayList<>(FileUtils.listFiles(new File(importDir), ArrayUtils.toArray(extension), false));
        archives.forEach(archive -> {
            String archiveName = String.join(File.separator, importDir, outputDir);
            // unzip only archives that are not meant as full reindex.
            //todo Rework after further information from Hejzl
            if (!archiveName.toLowerCase().contains(FULL)) {
                // output directory is created once every day (only if new archives are added)
                new File(String.join(File.separator, importDir, outputDir)).mkdirs();
                try {
                    if (archive.getName().startsWith(illPrefix)) {
                        extract(archive, outputDir, illSubdir);
                    } else if (archive.getName().startsWith(bookPrefix)) {
                        extract(archive, outputDir, bookSubdir);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        return archives;
    }

    public void importIllustrations(String outputDir) {
        Iterator<File> it = null;
        try {
            it = FileUtils.iterateFiles(
                    new File(String.join(File.separator, importDir, outputDir, illSubdir)), ArrayUtils.toArray("xml"), false);
        } catch (Exception e) {
            log.error("Importing illustrations failed from {}", outputDir);
        }

        if (it != null) {
            while (it.hasNext()) {
                File file = it.next();
                String fileName = file.getName();
                try {
                    if (recordRepository.findIdByIdentifier(fileName.substring(0, fileName.lastIndexOf("."))) == null) {
                        unmarshallAndParseRecord(file, marcCreateProcessor);
                    } else {
                        unmarshallAndParseRecord(file, marcUpdateProcessor);
                    }
                } catch (Exception e) {
                    log.error("Error while importing Illustration '{}'", fileName);
                }
            }
        }
    }

    public void importBooks(String outputDir) {
        Iterator<File> it = null;
        try {
            it = FileUtils.iterateFiles(
                    new File(String.join(File.separator, importDir, outputDir, bookSubdir)), ArrayUtils.toArray("xml"), false);
        } catch (Exception e) {
            log.error("Importing books failed from {}", outputDir);
        }

        if (it != null) {
            while (it.hasNext()) {
                File file = it.next();
                String fileName = file.getName();
                String identifier = fileName.substring(0, fileName.lastIndexOf("."));
                String identifierUnderscore = identifier + "_";
                if (recordRepository.findIllByBookIdentifier(identifierUnderscore) != null) {
                    try {
                        if (recordRepository.findIdByIdentifier(identifier) == null) {
                            unmarshallAndParseRecord(file, marcCreateProcessor);
                        } else {
                            unmarshallAndParseRecord(file, marcUpdateProcessor);
                        }
                    } catch (Exception e) {
                        log.error("Error while importing Book '{}'", fileName);
                    }
                }
            }
        }
    }

    public String unmarshallAndParseRecord(File file) {
        return unmarshallAndParseRecord(file, marcCreateProcessor);
    }

    public String unmarshallAndParseRecord(File file, MarcProcessor processor) {
        log.info("Parsing source file " + file.getName());
        CollectionType root = JAXB.unmarshal(file, CollectionType.class);
        RecordType record = root.getRecord().get(0);
        return processor.parseRecord(record);
    }

    public Path findBookSourceFile(String identifier) {
        String outputDir = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault()).format(LocalDate.now());
        Path subdirPath = Path.of(String.join(File.separator, importDir, outputDir, bookSubdir));

        if (Files.exists(subdirPath)) {
            try {
                return Files.list(subdirPath)
                        .filter(f -> f.getFileName().toString().equals(identifier + ".xml"))
                        .findFirst().orElse(null);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            log.info("File path " + subdirPath + " does not exist, book record " + identifier + " could not be found there");
        }
        return null;
    }

    private void extract(File archive, String outputDir, String subdir) throws IOException {
        ArchiveInputStream in = Utils.initZipArchiveInputStream(archive);
        ArchiveEntry entry;
        while ((entry = in.getNextEntry()) != null) {
            // in yyyy-MM-dd output directory, 'illustration' and 'book' subdirs are initiated
            File extracted = new File(String.join(File.separator, importDir, outputDir, subdir, entry.getName()));
            File parent = extracted.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            IOUtils.copy(in, new FileOutputStream(extracted));
            log.info("File " + entry.getName() + " extracted successfully");
        }
        in.close();
    }

    private void deleteArchives(List<File> archives) {
        archives.forEach(a -> {
            try {
                FileUtils.delete(a);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public List<File> extractArchivesInto(String outputDir, String reg) {
        List<File> archives = new ArrayList<>(FileUtils.listFiles(new File(importDir), ArrayUtils.toArray(extension), false));
        archives.forEach(archive -> {
            // output directory is created once every day (only if new archives are added)
            new File(String.join(File.separator, importDir, outputDir)).mkdirs();
            try {
                if (archive.getName().contains(reg)) {
                    if (archive.getName().startsWith(illPrefix)) {
                        extract(archive, outputDir, illSubdir);
                    } else if (archive.getName().startsWith(bookPrefix)) {
                        extract(archive, outputDir, bookSubdir);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return archives;
    }

    public int importIllustrations(String outputDir, int start, int end) {
        List<File> files = FileUtils.listFiles(
                new File(String.join(File.separator, importDir, outputDir, illSubdir)), ArrayUtils.toArray("xml"), false).stream().sorted().collect(Collectors.toList());
        for (int i = start; i < end; i++){
            File file = files.get(i);
            String fileName = file.getName();
            if (recordRepository.findIdByIdentifier(fileName.substring(0, fileName.lastIndexOf("."))) == null) {
                unmarshallAndParseRecord(file, marcCreateProcessor);
            } else {
                unmarshallAndParseRecord(file, marcUpdateProcessor);
            }
        }
        return files.size();
    }

    @Autowired
    public void setMarcCreateProcessor(MarcCreateProcessor marcCreateProcessor) {
        this.marcCreateProcessor = marcCreateProcessor;
    }

    @Autowired
    public void setMarcUpdateProcessor(MarcUpdateProcessor marcUpdateProcessor) {
        this.marcUpdateProcessor = marcUpdateProcessor;
    }
    @Autowired
    public void setRecordRepository(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }
}

