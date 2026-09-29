package cz.inqool.eas.eil.user;

import cz.inqool.eas.common.dated.index.DatedIndexedObject;
import lombok.Getter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.elasticsearch.annotations.*;

import static cz.inqool.eas.common.domain.index.field.ES.Analyzer.*;
import static cz.inqool.eas.common.domain.index.field.ES.Suffix.*;

@Getter
@Document(indexName = "user")
@FieldNameConstants(innerTypeName = "IxFields")
public class UserIndexedObject extends DatedIndexedObject<User, User> {

    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = TEXT_SHORT_KEYWORD),
            otherFields = {
                    @InnerField(suffix = FOLD, type = FieldType.Text, analyzer = FOLDING),
                    @InnerField(suffix = SEARCH, type = FieldType.Text, analyzer = FOLDING_AND_TOKENIZING),
                    @InnerField(suffix = SORT, type = FieldType.Text, analyzer = SORTING, fielddata = true)
            }
    )
    private String fullName;

    @Field(type = FieldType.Keyword)
    private EilRole role;

    @Field(type = FieldType.Keyword)
    private String email;

    @Override
    public void toIndexedObject(User obj) {
        super.toIndexedObject(obj);
        this.fullName = obj.getFullName();
        this.role = obj.getRole();
        this.email = obj.getEmail();
    }

    public static UserIndexedObject of(User obj) {
        if (obj == null) {
            return null;
        }
        UserIndexedObject indexed = new UserIndexedObject();
        indexed.toIndexedObject(obj);
        return indexed;
    }
}
