package cz.inqool.entityviews;

import com.google.testing.compile.JavaFileObjects;

import javax.tools.JavaFileObject;
import java.util.Arrays;

public abstract class TestBase {
    protected JavaFileObject[] loadJavaFiles(String... names) {
        return Arrays.
                stream(names).
                map(JavaFileObjects::forResource)
                .toArray(JavaFileObject[]::new);
    }

    protected JavaFileObject[] tail(JavaFileObject[] input) {
        return Arrays.asList(input).subList(1, input.length).toArray(JavaFileObject[]::new);
    }
}
