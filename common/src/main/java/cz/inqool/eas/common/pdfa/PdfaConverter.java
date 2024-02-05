package cz.inqool.eas.common.pdfa;

import cz.inqool.eas.common.storage.file.File;
import cz.inqool.eas.common.storage.file.FileManager;
import cz.inqool.eas.common.storage.file.OpenedFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.io.*;

@Slf4j
public abstract class PdfaConverter {

    private FileManager fileManager;

    /**
     * Converts a file with a PDFA converter and saves the result as a separate converted file.
     *
     * @param inputFile file that shall be converted into PDFA format
     * @return file reference of converted file.
     */
    @Transactional
    public File convert(File inputFile) {
        OpenedFile openedFile = fileManager.open(inputFile.getId());
        File descriptor = openedFile.getDescriptor();

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (InputStream input = openedFile.getStream()) {
            convertHook(descriptor, input, output);
        } catch (IOException e) {
            throw new PdfaConvertException("Failed to convert to PDFA.", e);
        }

        String fileName = getFileNameWithExtension(descriptor);
        String contentType = getContentType(descriptor);

        return fileManager.store(
                fileName,
                output.size(),
                contentType,
                new ByteArrayInputStream(output.toByteArray())
        );
    }

    /**
     * Constructs full file name with extension for converted file.
     *
     * @param fileDescriptor file descriptor
     * @return File name with new extension
     */
    protected String getFileNameWithExtension(File fileDescriptor) {
        String fileName = fileDescriptor.getName();
        return changeExtensionToPdf(fileName);
    }

    /**
     * Constructs content type for converted file.
     *
     * @return simple constant of "application/pdf", overridable in subclasses (e.g. for mocking purposes)
     */
    protected String getContentType(File fileDescriptor) {
        return "application/pdf";
    }

    /**
     * Renames file so that filename contains an extension '.pdf' (either adds it or replaces old one)
     *
     * @param fileName filename with or without extension
     * @return filename with .pdf extension
     */
    private String changeExtensionToPdf(String fileName) {
        String newExtension = ".pdf";
        String oldExtensionWithoutDot = FilenameUtils.getExtension(fileName); // without '.'
        if (oldExtensionWithoutDot.isBlank()) {
            return fileName + newExtension;
        } else {
            return fileName.replace("." + oldExtensionWithoutDot, newExtension);
        }
    }

    /**
     * Hook for actual conversion of data from input stream.
     *
     * @param descriptor file reference from that data are streamed
     * @param input      input stream data
     * @param output     output stream that converted data should be streamed into
     * @throws IOException if there is a problem with streams
     */
    public abstract void convertHook(File descriptor, InputStream input, OutputStream output) throws IOException;

    @Autowired
    public void setFileManager(FileManager fileManager) {
        this.fileManager = fileManager;
    }

}
