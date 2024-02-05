package cz.inqool.eas.common.utils;

/**
 * Utility methods for working with strings
 *
 * @author Lukas Jane (inQool) 02.02.2022.
 */
public class StringUtils {

    /**
     * Trims white space from ends of the string.
     * Converts empty strings to null.
     * Null safe.
     */
    public static String safeStripNullify(String input) {
        if (input == null) {
            return input;
        }
        input = input.strip();
        if (input.isEmpty()) {
            return null;
        }
        return input;
    }

    /**
     * For those cases when you append possibly null texts to possibly null string and want it to behave intelligently
     *
     * example:
     * note = accumulateToString(note, ",\n", "Additional note: ", secondNote, ".")
     */
    public static String accumulateToString(String original, String separator, String pre, String added, String post) {
        if (added == null) {
            return original;
        }
        String result;
        if (original == null || original.isEmpty()) {
            result = "";
        }
        else {
            result = original;
            if (separator != null) {
                result += separator;
            }
        }
        if (pre != null) {
            result += pre;
        }
        result += added;
        if (post != null) {
            result += post;
        }
        return result;
    }
}
