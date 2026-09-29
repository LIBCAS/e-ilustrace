package cz.inqool.entityviews;

import com.google.testing.compile.JavaSourcesSubjectFactory;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;
import java.util.Arrays;

import static com.google.common.truth.Truth.assert_;

public class ReferenceTest extends TestBase {
    @Test
    public void run() {
        JavaFileObject[] sourceFiles = loadJavaFiles(
                "cz/inqool/entityviews/ref/Address.java",
                "cz/inqool/entityviews/ref/Person.java",
                "cz/inqool/entityviews/ref/Passport.java",
                "cz/inqool/entityviews/ref/Name.java"
        );

        JavaFileObject[] generatedFiles = loadJavaFiles(
                "cz/inqool/entityviews/ref/AddressRef.java",
                "cz/inqool/entityviews/ref/PersonList.java",
                "cz/inqool/entityviews/ref/PassportRef.java",
                "cz/inqool/entityviews/ref/NameRef.java"
        );

        assert_().
                about(JavaSourcesSubjectFactory.javaSources()).
                that(Arrays.asList(sourceFiles)).
                processedWith(new EntityViewsProcessor()).
                compilesWithoutError().
                and().generatesSources(generatedFiles[0], tail(generatedFiles));
    }
}
