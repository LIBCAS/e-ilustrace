package cz.inqool.eas.common.utils;

import com.github.underscore.lodash.U;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility functions for working with RAW objects (Maps of Base types and arrays/lists).
 */
public class RawObjectUtils {
    /**
     * Sets value to specified path in dot notation, possibly creating missing keys.
     *
     * Does not support array notation.
     *
     * @param object Raw object
     * @param path Path in dot notation
     * @param value Value to set
     */
    public static void setToPath(Map<String, Object> object, String path, Object value) {
        String[] segments = path.split("\\.", 2);
        if (segments.length == 1) {
            object.put(segments[0], value);
        } else {
            Map<String, Object> nestedObject = (Map<String, Object>) object.computeIfAbsent(segments[0], (key) -> new HashMap<String, Object>());
            setToPath(nestedObject, segments[1], value);
        }
    }

    /**
     * Gets value for specified path in dot notation, possibly returning null if the path does not exist.
     *
     * @param object Raw object
     * @param path Path in dot notation
     * @param <T> Type of data to return
     * @return Value of null if not set or path does not exist
     */
    public static <T> T getFromPath(Map<String, Object> object, String path) {
        return U.get(object, path);
    }
}
