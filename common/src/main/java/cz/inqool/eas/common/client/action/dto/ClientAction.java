package cz.inqool.eas.common.client.action.dto;

import cz.inqool.eas.common.dictionary.store.DictionaryObject;
import cz.inqool.eas.common.domain.DomainViews;
import cz.inqool.eas.common.script.ScriptType;
import cz.inqool.entityviews.Viewable;
import lombok.Getter;
import lombok.Setter;

@Viewable
@DomainViews
@Getter
@Setter
public class ClientAction extends DictionaryObject<ClientAction> {
    /**
     * Language of the script used.
     */
    protected ScriptType scriptType;

    protected String script;

    protected boolean useTransaction;
}
