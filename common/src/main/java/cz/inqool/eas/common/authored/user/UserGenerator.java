package cz.inqool.eas.common.authored.user;

import cz.inqool.eas.common.authored.store.AuthoredObject;
import cz.inqool.eas.common.security.User;
import org.hibernate.Session;
import org.hibernate.tuple.ValueGenerator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Class for hibernate value generating used in {@link AuthoredObject}
 * get {@link UserReference} from ApplicationContext, so it's always currently logged in User
 */
public class UserGenerator implements ValueGenerator<UserReference> {
    @Override
    public UserReference generateValue(Session session, Object o) {
        return generateValue();
    }

    /**
     * Constructs UserReference of logged-in user.
     *
     * @return UserReference of logged-in user or null if no authenticated principal is in the context.
     */
    public static UserReference generateValue() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof User) {
                User user = (User) principal;
                return new UserReference(user.getId(), user.getName());
            }
        }

        return null;
    }

    /**
     * Obtains currently logged-in user.
     */
    public static User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof User) {
                return (User) principal;
            }
        }

        return null;
    }
}
