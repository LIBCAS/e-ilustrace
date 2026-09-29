package cz.inqool.eas.common.pdfa.mock;

import com.google.common.io.ByteStreams;
import cz.inqool.eas.common.pdfa.PdfaConverter;
import cz.inqool.eas.common.storage.file.File;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MockPdfaConverter extends PdfaConverter {

    private String fileNamePrefix;

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void convertHook(File descriptor, InputStream input, OutputStream output) throws IOException {
        ByteStreams.copy(input, output);
    }

    @Override
    protected String getFileNameWithExtension(File fileDescriptor) {
        if (fileNamePrefix != null) {
            return this.fileNamePrefix + fileDescriptor.getName();
        }
        return fileDescriptor.getName();
    }

    @Override
    protected String getContentType(File fileDescriptor) {
        String fileName = fileDescriptor.getName();
        // Mock Converter only copies file (does not convert to pdf) so return appropriate content type
        if (fileName.contains(".docx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        }
        if (fileName.contains(".xlsx")) {
            return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        }
        return super.getContentType(fileDescriptor); // use default behaviour otherwise
    }

    public void setFileNamePrefix(String fileNamePrefix) {
        this.fileNamePrefix = fileNamePrefix;
    }
}
