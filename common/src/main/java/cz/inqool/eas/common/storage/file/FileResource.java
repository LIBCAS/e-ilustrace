package cz.inqool.eas.common.storage.file;

import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

@AllArgsConstructor
public class FileResource implements Resource, Closeable {
    private final File file;
    private final InputStream stream;

    /**
     * Gets file descriptor.
     */
    public File getDescriptor() {
        return file;
    }

    @Override
    public boolean exists() {
        return file != null;
    }

    @Override
    public URL getURL() throws IOException {
        throw new IOException("Unsupported");
    }

    @Override
    public URI getURI() throws IOException {
        throw new IOException("Unsupported");
    }

    @Override
    public java.io.File getFile() throws IOException {
        throw new IOException("Unsupported");
    }

    @Override
    public long contentLength() throws IOException {
        return file.getSize();
    }

    @Override
    public long lastModified() throws IOException {
        return file.getUpdated().getEpochSecond();
    }

    @Override
    public Resource createRelative(String relativePath) throws IOException {
        throw new IOException("Unsupported");
    }

    @Override
    public String getFilename() {
        return file.getName();
    }

    @Override
    public String getDescription() {
        return file.getName();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return stream;
    }

    @Override
    public String toString() {
        return "FileResource{" +
                "id=" + file.getId() +
                ", name=" + file.getName() +
                ", size=" + file.getSize() +
                '}';
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }
}
