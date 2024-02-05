package cz.inqool.eas.eil.user;

import cz.inqool.eas.common.dated.DatedRepository;
import cz.inqool.eas.common.dated.index.DatedIndex;
import cz.inqool.eas.common.dated.store.DatedStore;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository extends DatedRepository<
        User,
        User,
        UserIndexedObject,
        DatedStore<User, User, QUser>,
        DatedIndex<User, User, UserIndexedObject>> {

    public User findByEmail(String email) {
        QUser model = QUser.user;

        User user = query()
                .select(model)
                .from(model)
                .where(model.deleted.isNull())
                .where(model.email.equalsIgnoreCase(email))
                .fetchFirst();

        detachAll();
        return user;
    }
}
