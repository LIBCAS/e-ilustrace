package cz.inqool.entityviews;

import com.google.testing.compile.JavaSourcesSubjectFactory;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;
import java.util.Arrays;

import static com.google.common.truth.Truth.assert_;

public class InheritanceTest extends TestBase {
    @Test
    public void run() {
        JavaFileObject[] sourceFiles = loadJavaFiles(
                "cz/inqool/entityviews/inheritance/Person.java",
                "cz/inqool/entityviews/inheritance/Man.java"
        );

        JavaFileObject[] generatedFiles = loadJavaFiles(
                "cz/inqool/entityviews/inheritance/PersonDetail.java",
                "cz/inqool/entityviews/inheritance/ManDetail.java",
                "cz/inqool/entityviews/inheritance/ManSimple.java"
        );

        assert_().
                about(JavaSourcesSubjectFactory.javaSources()).
                that(Arrays.asList(sourceFiles)).
                processedWith(new EntityViewsProcessor()).
                compilesWithoutError().
                and().generatesSources(generatedFiles[0], tail(generatedFiles));
    }

    @Test
    public void emptyExtends() {
        JavaFileObject[] sourceFiles = loadJavaFiles(
                "cz/inqool/entityviews/inheritance/Person.java",
                "cz/inqool/entityviews/inheritance/Cyborg.java"
        );

        JavaFileObject[] generatedFiles = loadJavaFiles(
                "cz/inqool/entityviews/inheritance/PersonDetail.java",
                "cz/inqool/entityviews/inheritance/CyborgSimple.java"
        );

        assert_().
                about(JavaSourcesSubjectFactory.javaSources()).
                that(Arrays.asList(sourceFiles)).
                processedWith(new EntityViewsProcessor()).
                compilesWithoutError().
                and().generatesSources(generatedFiles[0], tail(generatedFiles));
    }
}
