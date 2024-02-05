package cz.inqool.eas.common.admin.console.stream;

import java.io.IOException;
import java.io.OutputStream;

/**
 * OutputStream wrapper, allowing to write to multiple streams at once
 */
public class MultiOutputStream extends OutputStream {

    private final OutputStream[] outputStreams;


    public MultiOutputStream(OutputStream... outputStreams) {
        this.outputStreams = outputStreams;
    }


    @Override
    public void write(int b) throws IOException {
        for (OutputStream out : outputStreams) {
            out.write(b);
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        for (OutputStream out : outputStreams) {
            out.write(b);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        for (OutputStream out : outputStreams) {
            out.write(b, off, len);
        }
    }

    @Override
    public void flush() throws IOException {
        for (OutputStream out : outputStreams) {
            out.flush();
        }
    }

    @Override
    public void close() throws IOException {
        for (OutputStream out : outputStreams) {
            out.close();
        }
    }
}
