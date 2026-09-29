package cz.inqool.eas.eil.record.export;

import com.fasterxml.jackson.databind.SequenceWriter;
import cz.inqool.eas.common.domain.index.dto.Result;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import cz.inqool.eas.common.utils.JsonUtils;
import cz.inqool.eas.eil.record.RecordExport;
import cz.inqool.eas.eil.record.RecordRepository;
import cz.inqool.eas.eil.security.Permission;
import cz.inqool.eas.eil.security.UserChecker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static cz.inqool.eas.common.utils.AssertionUtils.coalesce;

@Service
@Slf4j
public class RecordExportService {
    public static final int SIZE = 30;
    public static final String ROOT_PATH = "/export";
    public static final String FILE_PREFIX = "records_export_";

    @Autowired
    private RecordRepository recordRepository;

    public String export(Params params) {
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
        params = coalesce(params, Params::new);
        params.setSize(SIZE);
        String now = Instant.now().toString();
        String fullFilePath = StringUtils.join(new String[]{ROOT_PATH, "/", FILE_PREFIX, now, ".json"}, "");

        try {
            Files.createDirectories(Paths.get(ROOT_PATH));
            File file = new File(fullFilePath);

            try (FileOutputStream fos = new FileOutputStream(file);
                 SequenceWriter jsonWriter = JsonUtils.newObjectMapper().writer()
                         .writeValuesAsArray(fos)) {

                Result<RecordExport> result = recordRepository.listByParams(RecordExport.class, params);
                while (result != null && result.getItems() != null && !result.getItems().isEmpty()) {
                    for (RecordExport record : result.getItems()) {
                        jsonWriter.write(record);
                    }

                    params.setSearchAfter(result.getSearchAfter());
                    result = recordRepository.listByParams(RecordExport.class, params);
                }

                return fullFilePath;
                // SequenceWriter closes the array and flushes everything
            } catch (Exception ex) {
                log.debug("Error while writing Records to JSON file", ex);
            }
        } catch (IOException e) {
            log.error("Failed creating export path.", e);
        }

        return null;
    }

    //Runs every hour
    @Scheduled(cron = "0 0 * * * *")
    public void deleteExportFiles() {
        try {
            Files.createDirectories(Paths.get(ROOT_PATH));
            File directory = new File(ROOT_PATH);
            List<String> files = directory.list() == null ? Collections.emptyList() : Arrays.asList(Objects.requireNonNull(directory.list()));
            for (String file : files) {
                if (file.startsWith(FILE_PREFIX)) {
                    Path filePath = Paths.get(StringUtils.join(new String[]{ROOT_PATH, "/", file}, ""));
                    try {
                        Files.delete(filePath);
                    } catch (IOException e) {
                        log.error("Deleting export file '{}' failed.", file);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Deleting export files failed.", e);
        }
    }
}
