package cz.inqool.eas.common.action;

import cz.inqool.eas.common.dictionary.store.DictionaryObject;
import cz.inqool.eas.common.domain.DomainViews;
import cz.inqool.eas.common.script.ScriptType;
import cz.inqool.entityviews.Viewable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;

/**
 * Servrová akce.
 */
@Viewable
@DomainViews
@Getter
@Setter
@BatchSize(size = 100)
@Entity
@Table(name = "eas_action")

public class Action extends DictionaryObject<Action> {
    /**
     * Language of the script used
     */
    @Enumerated(EnumType.STRING)
    protected ScriptType scriptType;

    protected String script;

    protected boolean useTransaction;
}
