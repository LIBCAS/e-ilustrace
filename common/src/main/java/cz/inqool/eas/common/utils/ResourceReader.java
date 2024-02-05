package cz.inqool.eas.common.utils;

import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Resource loader.
 */
public class ResourceReader {
    /**
     * Loads resource data as String.
     *
     * @param resource Spring resource
     * @return string with resource's data
     */
    public static String asString(Resource resource) {
        return asString(resource, UTF_8);
    }

    /**
     * Loads resource data as String.
     *
     * @param resource Spring resource
     *
     */
    public static String asString(Resource resource, Charset charset) {
        try (Reader reader = new InputStreamReader(resource.getInputStream(), charset)) {
            return FileCopyUtils.copyToString(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}