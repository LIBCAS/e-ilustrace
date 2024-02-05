package cz.inqool.eas.eil.config.storage;

import cz.inqool.eas.common.storage.StorageConfiguration;
import cz.inqool.eas.common.storage.file.FileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class EilStorageConfiguration extends StorageConfiguration {
    @Autowired
    @Value("${eil.file-storage.directory}")
    private String directoryPath;

    @Autowired
    @Value("${eil.file.file-size-limit:10485760}")
    private Long fileSizeLimit;

    @Autowired
    @Value("${eil.file.hierarchical-level:4}")
    private int hierarchicalLevel;

    @Override
    protected void configure(FileManager.FileManagerBuilder builder) {
        builder.
                directoryPath(directoryPath).
                fileSizeLimit(fileSizeLimit).
                allowedExtensions(allowedExtensions()).
                hierarchicalLevel(hierarchicalLevel);
        new File(directoryPath).mkdir();
    }

    @Override
    protected String fileUrl() {
        return "/files";
    }

    private String[] allowedExtensions() {
        return new String[]{".docx", ".doc", ".xlsx", ".xls", ".rtf", ".odt", ".ods", ".pdf", ".png", ".jpg", ".jpeg", ".json", "jrxml"};
    }
}
