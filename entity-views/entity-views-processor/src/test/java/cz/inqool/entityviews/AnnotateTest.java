package cz.inqool.entityviews;

import com.google.testing.compile.JavaSourcesSubjectFactory;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;
import java.util.Arrays;

import static com.google.common.truth.Truth.assert_;

public class AnnotateTest extends TestBase {
    @Test
    public void run() {
        JavaFileObject[] sourceFiles = loadJavaFiles(
                "cz/inqool/entityviews/annotate/Person.java",
                "cz/inqool/entityviews/annotate/Address.java"
        );

        JavaFileObject[] generatedFiles = loadJavaFiles(
                "cz/inqool/entityviews/annotate/PersonList.java",
                "cz/inqool/entityviews/annotate/PersonDetail.java",
                "cz/inqool/entityviews/annotate/PersonCreate.java",
                "cz/inqool/entityviews/annotate/AddressPersonList.java",
                "cz/inqool/entityviews/annotate/AddressPersonDetail.java",
                "cz/inqool/entityviews/annotate/AddressPersonCreate.java"
        );

        assert_().
                about(JavaSourcesSubjectFactory.javaSources()).
                that(Arrays.asList(sourceFiles)).
                processedWith(new EntityViewsProcessor()).
                compilesWithoutError().
                and().generatesSources(generatedFiles[0], tail(generatedFiles));
    }
}
