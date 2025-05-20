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
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import static cz.inqool.eas.common.domain.DomainViews.DEFAULT;
import static cz.inqool.eas.common.domain.DomainViews.IDENTIFIED;

@Viewable
@ViewableClass(views = {DEFAULT})
@ViewableMapping(views = {DEFAULT}, mappedTo = DEFAULT)
@ViewableAnnotation(value = {Entity.class, BatchSize.class, Table.class}, views = {DEFAULT})
@Getter
@Setter
@Entity
@Table(name = "eil_link")
public class Link extends DomainObject<Link> {

    @ViewableProperty(views = {DEFAULT})
    String url;

    @ViewableProperty(views = {DEFAULT})
    String description;

    @ViewableProperty(views = {DEFAULT})
    @ViewableMapping(views = {DEFAULT}, mappedTo = IDENTIFIED)
    @Fetch(FetchMode.SELECT)
    @ManyToOne
    Record record;
}
