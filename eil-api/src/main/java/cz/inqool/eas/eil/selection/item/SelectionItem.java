package cz.inqool.eas.eil.selection.item;

import cz.inqool.eas.common.domain.DomainViews;
import cz.inqool.eas.common.domain.store.DomainObject;
import cz.inqool.eas.eil.record.book.Book;
import cz.inqool.eas.eil.record.illustration.Illustration;
import cz.inqool.eas.eil.selection.Selection;
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
@ViewableClass(views = {DEFAULT, SelectionItem.EXTERNAL_UPDATE}, generateRef = true)
@ViewableMapping(views = {DEFAULT, SelectionItem.EXTERNAL_UPDATE}, mappedTo = DEFAULT)
@ViewableAnnotation(value = {Entity.class, BatchSize.class, Table.class}, views = {DEFAULT, SelectionItem.EXTERNAL_UPDATE})
@Entity
@Getter
@Setter
@Table(name = "eil_selection_item")
public class SelectionItem extends DomainObject<SelectionItem> {
    public static final String EXTERNAL_UPDATE = "external_update";

    @ViewableProperty(views = {DETAIL, EXTERNAL_UPDATE})
    @ViewableMapping(views = {DETAIL}, mappedTo = Illustration.ESSENTIAL)
    @ViewableMapping(views = {EXTERNAL_UPDATE}, useRef = true)
    @Fetch(FetchMode.SELECT)
    @ManyToOne
    protected Illustration illustration;

    @ViewableProperty(views = {DETAIL, EXTERNAL_UPDATE})
    @ViewableMapping(views = {DETAIL}, mappedTo = Book.ESSENTIAL)
    @ViewableMapping(views = {EXTERNAL_UPDATE}, useRef = true)
    @Fetch(FetchMode.SELECT)
    @ManyToOne
    protected Book book;

    @ViewableProperty(views = {DETAIL, EXTERNAL_UPDATE})
    @ViewableMapping(views = {DETAIL, EXTERNAL_UPDATE}, useRef = true)
    @Fetch(FetchMode.SELECT)
    @ManyToOne
    protected Selection selection;

    protected boolean mirador;
}
