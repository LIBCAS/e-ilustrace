package cz.inqool.eas.common.domain.index;

import cz.inqool.eas.common.domain.Domain;
import cz.inqool.eas.common.domain.DomainIndexed;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

/**
 * Basic building block for every Indexed entity.
 *
 * @param <ROOT> Root of the projection type system
 * @param <PROJECTED> Index projection type
 */
@Getter
@Setter
@Setting(settingPath = "/es_settings.json")
@FieldNameConstants
abstract public class DomainIndexedObject<ROOT extends Domain<ROOT>, PROJECTED extends Domain<ROOT>> implements DomainIndexed<ROOT, PROJECTED> {

    @Id
    @Field(type = FieldType.Keyword)
    protected String id;

    /**
     * {@inheritDoc}
     */
    @Override
    public void toIndexedObject(PROJECTED obj) {
        this.id = obj.getId();
    }
}
