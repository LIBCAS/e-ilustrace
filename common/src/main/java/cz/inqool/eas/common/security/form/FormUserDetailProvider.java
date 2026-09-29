package cz.inqool.eas.common.security.form;

import cz.inqool.eas.common.security.User;

/**
 * Marks support for retrieving user based on username without authorisation.
 */
public interface FormUserDetailProvider {
    /**
     * Should find user by given name WITHOUT authorities
     */
    User findUser(String username);
}
