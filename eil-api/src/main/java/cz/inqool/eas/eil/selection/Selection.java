package cz.inqool.eas.eil.selection;

import cz.inqool.eas.common.dated.store.DatedObject;
import cz.inqool.eas.common.domain.DomainViews;
import cz.inqool.eas.eil.selection.item.SelectionItem;
import cz.inqool.eas.eil.user.User;
import cz.inqool.entityviews.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

import static cz.inqool.eas.common.domain.DomainViews.*;
import static cz.inqool.eas.eil.selection.item.SelectionItem.EXTERNAL_UPDATE;

@Viewable
@DomainViews
@ViewableClass(views = {DEFAULT, Selection.USER_FIND}, generateRef = true)
@ViewableMapping(views = {DEFAULT, Selection.USER_FIND}, mappedTo = DEFAULT)
@ViewableAnnotation(value = {Entity.class, BatchSize.class, Table.class}, views = {DEFAULT, Selection.USER_FIND})
@Entity
@Getter
@Setter
@Table(name = "eil_selection")
public class Selection extends DatedObject<Selection> {
    public static final String USER_FIND = "user_find";

    @ViewableProperty(views = {DETAIL, UPDATE, USER_FIND})
    @ViewableMapping(views = {DETAIL}, mappedTo = DETAIL)
    @ViewableMapping(views = {UPDATE, USER_FIND}, mappedTo = EXTERNAL_UPDATE)
    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @OneToMany(mappedBy = "selection", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    protected List<SelectionItem> items = new ArrayList<>();

    @NotNull
    @ViewableProperty(views = {DETAIL, USER_FIND})
    @ViewableMapping(views = {DETAIL, USER_FIND}, useRef = true)
    @Fetch(FetchMode.SELECT)
    @OneToOne
    protected User user;

}
