package cz.inqool.eas.eil.keyword;

import cz.inqool.eas.common.domain.DomainViews;
import cz.inqool.eas.common.domain.store.DomainObject;
import cz.inqool.entityviews.Viewable;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;

@Viewable
@DomainViews
@Getter
@Setter
@Entity
@Table(name = "eil_keyword")
public class Keyword extends DomainObject<Keyword> {
    String label;
}

