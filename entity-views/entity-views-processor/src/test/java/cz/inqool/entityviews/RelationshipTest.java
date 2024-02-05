package cz.inqool.entityviews;

import com.google.testing.compile.JavaSourcesSubjectFactory;
import org.junit.jupiter.api.Test;

import javax.tools.JavaFileObject;
import java.util.Arrays;

import static com.google.common.truth.Truth.assert_;

public class RelationshipTest extends TestBase {
    @Test
    public void run() {
        JavaFileObject[] sourceFiles = loadJavaFiles(
                "cz/inqool/entityviews/relationship/Person.java",
                "cz/inqool/entityviews/relationship/Address.java"
        );

        JavaFileObject[] generatedFiles = loadJavaFiles(
                "cz/inqool/entityviews/relationship/PersonList.java",
                "cz/inqool/entityviews/relationship/PersonDetail.java",
                "cz/inqool/entityviews/relationship/AddressList.java",
                "cz/inqool/entityviews/relationship/AddressDetail.java",
                "cz/inqool/entityviews/relationship/AddressPersonList.java"
        );

        assert_().
                about(JavaSourcesSubjectFactory.javaSources()).
                that(Arrays.asList(sourceFiles)).
                processedWith(new EntityViewsProcessor()).
                compilesWithoutError().
                and().generatesSources(generatedFiles[0], tail(generatedFiles));
    }
}
