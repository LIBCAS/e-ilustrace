package cz.inqool.eas.eil.exhibition;

import cz.inqool.eas.common.dated.store.DatedObject;
import cz.inqool.eas.common.domain.DomainViews;
import cz.inqool.eas.eil.exhibition.item.ExhibitionItem;
import cz.inqool.eas.eil.user.User;
import cz.inqool.entityviews.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

import static cz.inqool.eas.common.domain.DomainViews.*;
import static cz.inqool.eas.eil.exhibition.item.ExhibitionItem.EXTERNAL_UPDATE;

@Viewable
@DomainViews
@ViewableClass(views = {DEFAULT, Exhibition.ESSENTIAL, INDEXED}, generateRef = true)
@ViewableMapping(views = {DEFAULT, Exhibition.ESSENTIAL, INDEXED}, mappedTo = DEFAULT)
@ViewableAnnotation(value = {Entity.class, BatchSize.class, Table.class}, views = {DEFAULT, Exhibition.ESSENTIAL, INDEXED})
@Entity
@Getter
@Setter
@Table(name = "eil_exhibition")
public class Exhibition extends DatedObject<Exhibition> {
    public static final String ESSENTIAL = "ESSENTIAL";

    protected String name;

    protected String description;

    @ViewableProperty(views = {DETAIL, LIST, INDEXED})
    protected Instant published;

    @ViewableProperty(views = {DETAIL, CREATE, UPDATE, LIST, ESSENTIAL})
    @ViewableMapping(views = {DETAIL, LIST}, mappedTo = DETAIL)
    @ViewableMapping(views = {CREATE}, mappedTo = CREATE)
    @ViewableMapping(views = {UPDATE}, mappedTo = EXTERNAL_UPDATE)
    @ViewableMapping(views = {ESSENTIAL}, mappedTo = EXTERNAL_UPDATE)
    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "exhibition_id")
    @OrderBy("list_order")
    protected List<ExhibitionItem> items = new LinkedList<>();

    @ViewableProperty(views = {DETAIL, LIST, CREATE, ESSENTIAL, INDEXED})
    @ViewableMapping(views = {DETAIL, CREATE, ESSENTIAL}, useRef = true)
    @ViewableMapping(views = {DETAIL, LIST}, mappedTo = ESSENTIAL)
    @Fetch(FetchMode.SELECT)
    @ManyToOne
    protected User user;

    @Enumerated(EnumType.STRING)
    protected Radio radio;

    @ViewableProperty(views = {DETAIL, LIST})
    public int getItemsCount() {
        return items.size();
    }
}
