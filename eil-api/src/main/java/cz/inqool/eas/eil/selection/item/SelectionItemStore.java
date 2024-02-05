package cz.inqool.eas.eil.selection.item;

import cz.inqool.eas.common.domain.store.DomainStore;
import org.springframework.stereotype.Repository;

@Repository
public class SelectionItemStore extends DomainStore<SelectionItem, SelectionItem, QSelectionItem> {

    public SelectionItemStore() {
        super(SelectionItem.class);
    }
}


