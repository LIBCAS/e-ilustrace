package cz.inqool.eas.eil.record.owner;

import cz.inqool.eas.common.domain.store.DomainObject;
import cz.inqool.entityviews.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.Entity;
import javax.persistence.Table;

import static cz.inqool.eas.common.domain.DomainViews.DEFAULT;

@Viewable
@ViewableClass(views = {DEFAULT})
@ViewableMapping(views = {DEFAULT}, mappedTo = DEFAULT)
@ViewableAnnotation(value = {Entity.class, BatchSize.class, Table.class}, views = {DEFAULT})
@Getter
@Setter
@Entity
@Table(name = "eil_owner")
public class Owner extends DomainObject<Owner> {

    @ViewableProperty(views = {DEFAULT})
    String name;

    @ViewableProperty(views = {DEFAULT})
    String signature;
}
