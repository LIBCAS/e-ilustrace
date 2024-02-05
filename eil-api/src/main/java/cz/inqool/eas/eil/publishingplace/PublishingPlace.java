package cz.inqool.eas.eil.publishingplace;

import cz.inqool.eas.common.dated.store.DatedObject;
import cz.inqool.eas.common.domain.DomainViews;
import cz.inqool.eas.eil.authority.Authority;
import cz.inqool.entityviews.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;

import static cz.inqool.eas.common.domain.DomainViews.*;

/**
 * Místo vydání
 */
@Viewable
@DomainViews
@ViewableImplement(value = Authority.class, views = {DETAIL})
@ViewableClass(views = {INDEXED}, generateRef = true)
@ViewableMapping(views = {INDEXED}, mappedTo = DEFAULT)
@ViewableAnnotation(value = {Entity.class, BatchSize.class, Table.class}, views = {INDEXED})
@Getter
@Setter
@Entity
@Table(name = "eil_publishing_place")
public class PublishingPlace extends DatedObject<PublishingPlace> implements Authority {

    public static final String INDEXED = "indexed";

    @ViewableProperty(views = {DETAIL, LIST, INDEXED})
    String name;

    @ViewableProperty(views = {DETAIL, LIST, INDEXED})
    String country;

    @ViewableProperty(views = {DETAIL, INDEXED})
    String authorityCode;

    @ViewableProperty(views = {DETAIL, LIST, INDEXED})
    boolean fromBook;

    @ViewableProperty(views = {DETAIL, LIST, INDEXED})
    boolean fromIllustration;
}
