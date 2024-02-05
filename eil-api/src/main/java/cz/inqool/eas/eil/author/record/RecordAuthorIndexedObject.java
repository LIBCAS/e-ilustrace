package cz.inqool.eas.eil.author.record;

import cz.inqool.eas.common.domain.index.DomainIndexedObject;
import cz.inqool.eas.eil.author.AuthorDetail;
import cz.inqool.eas.eil.author.AuthorIndexedObject;
import cz.inqool.eas.eil.role.MarcRole;
import cz.inqool.entityviews.ViewableProperty;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import java.util.Set;
import java.util.stream.Collectors;

import static cz.inqool.eas.common.domain.DomainViews.DETAIL;
import static cz.inqool.eas.common.domain.DomainViews.LIST;

@Getter
@org.springframework.data.elasticsearch.annotations.Document(indexName = "record_author")
@FieldNameConstants(innerTypeName = "IxFields")
public class RecordAuthorIndexedObject extends DomainIndexedObject<RecordAuthor, RecordAuthorIndexed> {

    @Field(type = FieldType.Object)
    private AuthorIndexedObject author;

    @Field(type = FieldType.Text)
    private Set<String> roles;

    @Field(type = FieldType.Boolean)
    boolean fromBook;

    @Field(type = FieldType.Boolean)
    boolean fromIllustration;

    @Override
    public void toIndexedObject(RecordAuthorIndexed obj) {
        super.toIndexedObject(obj);

        this.author = AuthorIndexedObject.of(AuthorDetail.toEntity(obj.getAuthor()));
        this.roles = obj.getRoles().stream().map(MarcRole::getId).collect(Collectors.toSet());
        this.fromBook = obj.isFromBook();
        this.fromIllustration = obj.isFromIllustration();
    }

    public static RecordAuthorIndexedObject of(RecordAuthorIndexed obj) {
        if (obj == null) {
            return null;
        }
        RecordAuthorIndexedObject indexed = new RecordAuthorIndexedObject();
        indexed.toIndexedObject(obj);
        return indexed;
    }

    public static RecordAuthorIndexedObject of(RecordAuthor obj) {
        return of(RecordAuthorIndexed.toView(obj));
    }
}
