package cz.inqool.eas.eil.iconclass;

import cz.inqool.eas.common.dated.store.DatedObject;
import cz.inqool.entityviews.Viewable;
import cz.inqool.entityviews.ViewableAnnotation;
import cz.inqool.entityviews.ViewableClass;
import cz.inqool.entityviews.ViewableMapping;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import static cz.inqool.eas.common.domain.DomainViews.CREATE;
import static cz.inqool.eas.common.domain.DomainViews.DEFAULT;

@Viewable
@ViewableClass(views = {CREATE, DEFAULT}, generateRef = true)
@ViewableMapping(views = {CREATE}, mappedTo = CREATE)
@ViewableMapping(views = {DEFAULT}, mappedTo = DEFAULT)
@ViewableAnnotation(value = {Entity.class, BatchSize.class, Table.class}, views = {DEFAULT})
@Getter
@Setter
@Entity
@Table(name = "eil_iconclass_category")
public class IconclassCategory extends DatedObject<IconclassCategory> {

    @NotNull
    String code;

    String name;

    String url;
}
