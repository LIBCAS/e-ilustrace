package cz.inqool.eas.common.utils;

public class IndexUtils {
    public static String indexField(String... names) {
        return String.join(".", names);
    }
}
