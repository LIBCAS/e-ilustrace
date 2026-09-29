package cz.inqool.eas.common.exception.dto;

import lombok.AllArgsConstructor;

import org.springframework.lang.Nullable;
import java.util.Objects;

/**
 * Exception dto with obfuscated details.
 */
@AllArgsConstructor
public class ObfuscatedException {

    /**
     * Name of original exception class.
     */
    private String clazz;

    /**
     * Message.
     */
    private String message;

    /**
     * Path.
     */
    private String path;


    public ObfuscatedException(Class<? extends Exception> clazz, @Nullable String message, @Nullable String path) {
        this(clazz.getName(), (message != null) ? message.trim() : null, path);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObfuscatedException that = (ObfuscatedException) o;
        return clazz.equals(that.clazz) &&
                ((message == null || that.message == null) || Objects.equals(message, that.message)) &&
                ((path == null || that.path == null) || Objects.equals(path, that.path));
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz);
    }
}
