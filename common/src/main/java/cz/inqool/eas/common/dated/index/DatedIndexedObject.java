package cz.inqool.eas.common.dated.index;

import cz.inqool.eas.common.dated.Dated;
import cz.inqool.eas.common.dated.DatedIndexed;
import cz.inqool.eas.common.domain.index.DomainIndexedObject;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;

/**
 * Building block for Index entities, which want to track creation, update and deletion.
 * <p>
 * Provides attributes {@link DatedIndexedObject#created}, {@link DatedIndexedObject#updated} and {@link
 * DatedIndexedObject#deleted}.
 * <p>
 *
 * @param <ROOT>      Root of the projection type system
 * @param <PROJECTED> Index projection type
 */
@Getter
@Setter
@FieldNameConstants
abstract public class DatedIndexedObject<ROOT extends Dated<ROOT>, PROJECTED extends Dated<ROOT>> extends DomainIndexedObject<ROOT, PROJECTED> implements DatedIndexed<ROOT, PROJECTED> {

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    protected Instant created;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    protected Instant updated;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    protected Instant deleted;

    @Override
    public void toIndexedObject(PROJECTED obj) {
        super.toIndexedObject(obj);

        this.created = obj.getCreated();
        this.updated = obj.getUpdated();
        this.deleted = obj.getDeleted();
    }
}
