package cz.inqool.eas.common.signing.request;

import cz.inqool.eas.common.authored.index.AuthoredIndexedObject;
import cz.inqool.eas.common.authored.user.UserReferenceIndexed;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import static cz.inqool.eas.common.domain.index.field.ES.Analyzer.TEXT_SHORT_KEYWORD;

@Getter
@Setter
@Document(indexName = "eas_sign_request")
@FieldNameConstants
public class SignRequestIndexedObject extends AuthoredIndexedObject<SignRequest, SignRequest> {
    @Field(type = FieldType.Text, analyzer = TEXT_SHORT_KEYWORD, fielddata = true)
    protected String name;

    @Field(type = FieldType.Object, fielddata = true)
    protected UserReferenceIndexed user;

    @Override
    public void toIndexedObject(SignRequest obj) {
        super.toIndexedObject(obj);

        this.name = obj.getName();
        this.user = UserReferenceIndexed.of(obj.getUser());
    }
}
