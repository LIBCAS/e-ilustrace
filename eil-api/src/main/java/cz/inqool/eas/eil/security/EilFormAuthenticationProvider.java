package cz.inqool.eas.eil.security;

import cz.inqool.eas.common.exception.v2.ForbiddenOperation;
import cz.inqool.eas.common.exception.v2.MissingObject;
import cz.inqool.eas.common.security.form.FormAuthenticationProvider;
import cz.inqool.eas.eil.user.User;
import cz.inqool.eas.eil.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static cz.inqool.eas.common.exception.v2.ExceptionCode.ENTITY_NOT_EXIST;
import static cz.inqool.eas.common.utils.AssertionUtils.notNull;
import static cz.inqool.eas.eil.config.exception.EilExceptionCode.ACCOUNT_NOT_VALIDATED;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "eil.security.form", name = "enabled", havingValue = "true")
public class EilFormAuthenticationProvider extends FormAuthenticationProvider {

    private UserRepository userRepository;
    private UserToUserConverter userToUserConverter;

    @Override
    public void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        super.additionalAuthenticationChecks(userDetails, authentication);

        User user = getUserForLogin((String) authentication.getPrincipal());
        if (!user.isValidated()) {
            throw new ForbiddenOperation(ACCOUNT_NOT_VALIDATED, "User account must be validated through activation link before signing in")
                    .details(details -> details.property("validated", user.isValidated()).clazz(User.class))
                    .debugInfo(info -> info.property("validated", user.isValidated()).clazz(User.class))
                    .logAll();
        }
    }

    @Override
    public cz.inqool.eas.common.security.User findUser(String email) {
        User user = getUserForLogin(email);
        return userToUserConverter.convertToUser(user, false);
    }

    @Override
    public cz.inqool.eas.common.security.User findUserWithAuthorities(String email) {
        User user = getUserForLogin(email);
        return userToUserConverter.convertToUser(user, true);
    }

    private User getUserForLogin(String email) {
        User user = userRepository.findByEmail(email);
        notNull(user, () -> new MissingObject(ENTITY_NOT_EXIST)
                .details(details -> details.property("email", email).clazz(User.class))
                .debugInfo(info -> info.property("email", email).clazz(User.class)));
        return user;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setUserToUserConverter(UserToUserConverter userToUserConverter) {
        this.userToUserConverter = userToUserConverter;
    }
}
