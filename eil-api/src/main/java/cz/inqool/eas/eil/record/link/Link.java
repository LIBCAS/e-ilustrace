package cz.inqool.eas.eil.record.link;

import cz.inqool.eas.common.domain.store.DomainObject;
import cz.inqool.eas.eil.record.Record;
import cz.inqool.entityviews.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import static cz.inqool.eas.common.domain.DomainViews.DEFAULT;
import static cz.inqool.eas.common.domain.DomainViews.IDENTIFIED;
import static cz.inqool.eas.eil.record.link.Link.EXPORT;

@Viewable
@ViewableClass(views = {DEFAULT, EXPORT})
@ViewableMapping(views = {DEFAULT, EXPORT}, mappedTo = DEFAULT)
@ViewableAnnotation(value = {Entity.class, BatchSize.class, Table.class}, views = {DEFAULT, EXPORT})
@Getter
@Setter
@Entity
@Table(name = "eil_link")
public class Link extends DomainObject<Link> {
    public static final String EXPORT = "EXPORT";

    @ViewableProperty(views = {DEFAULT, EXPORT})
    String url;

    @ViewableProperty(views = {DEFAULT, EXPORT})
    String description;

    @ViewableProperty(views = {DEFAULT, EXPORT})
    @ViewableMapping(views = {DEFAULT, EXPORT}, mappedTo = IDENTIFIED)
    @Fetch(FetchMode.SELECT)
    @ManyToOne
    Record record;
}
