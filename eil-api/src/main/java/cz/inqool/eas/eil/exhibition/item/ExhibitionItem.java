package cz.inqool.eas.eil.exhibition.item;

import cz.inqool.eas.common.dated.store.DatedObject;
import cz.inqool.eas.common.domain.DomainViews;
import cz.inqool.eas.eil.exhibition.Exhibition;
import cz.inqool.eas.eil.record.illustration.Illustration;
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

import static cz.inqool.eas.common.domain.DomainViews.*;

@Viewable
@DomainViews
@ViewableClass(views = {DEFAULT, ExhibitionItem.EXTERNAL_UPDATE}, generateRef = true)
@ViewableMapping(views = {DEFAULT, ExhibitionItem.EXTERNAL_UPDATE}, mappedTo = DEFAULT)
@ViewableAnnotation(value = {Entity.class, BatchSize.class, Table.class}, views = {DEFAULT, ExhibitionItem.EXTERNAL_UPDATE})
@Entity
@Getter
@Setter
@Table(name = "eil_exhibition_item")
public class ExhibitionItem extends DatedObject<ExhibitionItem> {

    public static final String EXTERNAL_UPDATE = "external_update";
    public static final String EXHIBITION = "EXHIBITION";

    protected String description;

    @ViewableProperty(views = {DETAIL, CREATE, EXTERNAL_UPDATE})
    @ViewableMapping(views = {DETAIL}, mappedTo = EXHIBITION)
    @ViewableMapping(views = {CREATE, EXTERNAL_UPDATE}, useRef = true)
    @ManyToOne(fetch = FetchType.EAGER)
    protected Illustration illustration;

    @ViewableProperty(views = {DETAIL, CREATE, EXTERNAL_UPDATE})
    @ViewableMapping(views = {DETAIL, CREATE, EXTERNAL_UPDATE}, useRef = true)
    @Fetch(FetchMode.SELECT)
    @ManyToOne
    protected Exhibition exhibition;

    protected String name;

    protected String year;

    protected boolean preface;
}
