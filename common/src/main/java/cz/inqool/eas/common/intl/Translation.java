package cz.inqool.eas.common.intl;

import cz.inqool.eas.common.dictionary.store.DictionaryObject;
import cz.inqool.eas.common.domain.DomainViews;
import cz.inqool.eas.common.storage.file.File;
import cz.inqool.entityviews.Viewable;
import cz.inqool.entityviews.ViewableMapping;
import cz.inqool.entityviews.ViewableProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static cz.inqool.eas.common.domain.DomainViews.*;
import static cz.inqool.eas.common.domain.DomainViews.UPDATE;

/**
 * Language translation.
 */
@Viewable
@DomainViews
@Getter
@Setter
@BatchSize(size = 100)
@Entity
@Table(name = "eas_translation")
public class Translation extends DictionaryObject<Translation> {
    /**
     * Translation language.
     */
    @Enumerated(EnumType.STRING)
    protected Language language;

    /**
     * File with key-value translations.
     */
    @ViewableProperty(views = {CREATE, UPDATE, DETAIL})
    @ViewableMapping(views = {CREATE, UPDATE}, useRef = true)
    @NotNull
    @Fetch(FetchMode.SELECT)
    @ManyToOne
    protected File content;
}
