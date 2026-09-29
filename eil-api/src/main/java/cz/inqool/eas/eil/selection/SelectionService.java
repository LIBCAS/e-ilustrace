package cz.inqool.eas.eil.selection;

import cz.inqool.eas.common.dated.DatedService;
import cz.inqool.eas.common.exception.v2.MissingObject;
import cz.inqool.eas.eil.record.book.BookRef;
import cz.inqool.eas.eil.record.illustration.IllustrationRef;
import cz.inqool.eas.eil.security.UserChecker;
import cz.inqool.eas.eil.selection.dto.SelectionDto;
import cz.inqool.eas.eil.selection.dto.SelectionUpdateDto;
import cz.inqool.eas.eil.selection.item.SelectionItem;
import cz.inqool.eas.eil.selection.item.SelectionItemExternalUpdate;
import cz.inqool.eas.eil.selection.item.SelectionItemRef;
import cz.inqool.eas.eil.selection.item.SelectionItemStore;
import cz.inqool.eas.eil.user.User;
import cz.inqool.eas.eil.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static cz.inqool.eas.common.exception.v2.ExceptionCode.ENTITY_NOT_EXIST;
import static cz.inqool.eas.common.utils.AssertionUtils.notNull;

@Service
public class SelectionService extends DatedService<
        Selection,
        SelectionDetail,
        SelectionList,
        SelectionCreate,
        SelectionUpdate,
        SelectionRepository> {

    private UserRepository userRepository;
    private SelectionItemStore selectionItemStore;

    @Transactional
    public void addItems(SelectionUpdateDto dto) {
        User user = userRepository.find(UserChecker.getUserId());

        SelectionUserFind selectionUserFind = repository.findByUserId(user.getId());
        if (selectionUserFind == null) {
            Selection selection = new Selection();
            selection.setUser(user);
            selection = repository.create(selection);
            selectionUserFind = SelectionUserFind.toView(selection);
        }
        SelectionUserFind finalSelectionUserFind = selectionUserFind;
        Selection selection = SelectionUserFind.toEntity(finalSelectionUserFind);

        dto.getIllustrations().forEach(i -> {
            SelectionItem selectionItem = new SelectionItem();
            selectionItem.setIllustration(IllustrationRef.toEntity(i));
            selectionItem.setSelection(selection);
            selectionItemStore.create(selectionItem);
        });

        dto.getBooks().forEach(b -> {
            SelectionItem selectionItem = new SelectionItem();
            selectionItem.setBook(BookRef.toEntity(b));
            selectionItem.setSelection(selection);
            selectionItemStore.create(selectionItem);
        });

    }

    @Transactional
    public void deleteItems(SelectionDto dto) {
        SelectionUserFind selection = repository.findByUserId(UserChecker.getUserId());
        notNull(selection, () -> new MissingObject(ENTITY_NOT_EXIST)
                .details(details -> details.clazz(Selection.class)));

        dto.getItems().forEach(r -> {
            SelectionItem item = SelectionItemRef.toEntity(r);
            item.setSelection(null);
            item.setBook(null);
            item.setIllustration(null);
            selectionItemStore.update(item);
            selectionItemStore.delete(item.id);
        });
    }

    @Transactional
    public void setMiradorFlags(SelectionUpdate dto) {
        SelectionUserFind selection = repository.findByUserId(UserChecker.getUserId());
        notNull(selection, () -> new MissingObject(ENTITY_NOT_EXIST)
                .details(details -> details.clazz(Selection.class)));

        dto.getItems().forEach(r -> {
            SelectionItem item = SelectionItemExternalUpdate.toEntity(r);
            item.setMirador(!item.isMirador());
            selectionItemStore.update(item);
        });
    }

    public SelectionDetail getMine() {
        return repository.findSelectionDetailByUserId(UserChecker.getUserId());
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setSelectionItemStore(SelectionItemStore selectionItemStore) {
        this.selectionItemStore = selectionItemStore;
    }
}
