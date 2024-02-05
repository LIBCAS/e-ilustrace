package cz.inqool.eas.eil.theme;

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

/**
 * Custom EIL themes that admins annotate illustrations and books with (tab "Temata" on FE)
 */
@Viewable
@ViewableClass(views = {DEFAULT, CREATE}, generateRef = true)
@ViewableMapping(views = {DEFAULT}, mappedTo = DEFAULT)
@ViewableMapping(views = {CREATE}, mappedTo = CREATE)
@ViewableAnnotation(value = {Entity.class, BatchSize.class, Table.class}, views = {DEFAULT})
@Getter
@Setter
@Entity
@Table(name = "eil_theme")
public class Theme extends DatedObject<Theme> {

    @NotNull
    String name;
}
