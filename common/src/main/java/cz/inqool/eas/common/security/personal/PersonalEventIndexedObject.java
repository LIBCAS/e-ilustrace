package cz.inqool.eas.common.security.personal;

import cz.inqool.eas.common.authored.user.UserReferenceIndexed;
import cz.inqool.eas.common.dated.index.DatedIndexedObject;
import cz.inqool.eas.common.domain.index.reference.LabeledReference;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.*;

@Getter
@Setter
@Document(indexName = "eas_personal_event")
public class PersonalEventIndexedObject extends DatedIndexedObject<PersonalEvent, PersonalEvent> {

    @Field(type = FieldType.Object, fielddata = true)
    protected LabeledReference type;

    @Field(type = FieldType.Object)
    protected UserReferenceIndexed user;

    @Override
    public void toIndexedObject(PersonalEvent obj) {
        super.toIndexedObject(obj);

        this.type = LabeledReference.of(obj.getType());
        this.user = UserReferenceIndexed.of(obj.getUser());
    }
}
