package cz.inqool.entityviews;

import com.google.testing.compile.JavaSourcesSubjectFactory;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;
import java.util.Arrays;

import static com.google.common.truth.Truth.assert_;

public class MethodTest extends TestBase {
    @Test
    public void run() {
        JavaFileObject[] sourceFiles = loadJavaFiles(
                "cz/inqool/entityviews/method/Person.java"
        );

        JavaFileObject[] generatedFiles = loadJavaFiles(
                "cz/inqool/entityviews/method/PersonList.java",
                "cz/inqool/entityviews/method/PersonDetail.java",
                "cz/inqool/entityviews/method/PersonComplex.java"
        );

        assert_().
                about(JavaSourcesSubjectFactory.javaSources()).
                that(Arrays.asList(sourceFiles)).
                processedWith(new EntityViewsProcessor()).
                compilesWithoutError().
                and().generatesSources(generatedFiles[0], tail(generatedFiles));
    }
}
