package cz.inqool.eas.eil.user;

import cz.inqool.eas.common.dated.DatedService;
import cz.inqool.eas.common.exception.v2.ForbiddenObject;
import cz.inqool.eas.common.exception.v2.InvalidAttribute;
import cz.inqool.eas.common.exception.v2.MissingObject;
import cz.inqool.eas.eil.notification.NotificationSender;
import cz.inqool.eas.eil.notification.event.NotificationEvent;
import cz.inqool.eas.eil.notification.template.model.TokenNotificationTemplateModel;
import cz.inqool.eas.eil.security.Permission;
import cz.inqool.eas.eil.security.UserChecker;
import cz.inqool.eas.eil.security.token.Token;
import cz.inqool.eas.eil.security.token.TokenService;
import cz.inqool.eas.eil.user.dto.ChangePasswordDto;
import cz.inqool.eas.eil.user.dto.ChangeRoleDto;
import cz.inqool.eas.eil.user.dto.PasswordResetDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.validation.constraints.NotNull;

import java.util.UUID;

import static cz.inqool.eas.common.exception.v2.ExceptionCode.ENTITY_NOT_EXIST;
import static cz.inqool.eas.common.utils.AssertionUtils.*;
import static cz.inqool.eas.eil.config.exception.EilExceptionCode.ENTITY_ALREADY_EXISTS;
import static cz.inqool.eas.eil.config.exception.EilExceptionCode.TOKEN_EXPIRED;
import static cz.inqool.eas.eil.exception.EilExceptionCode.*;

@Service
@Slf4j
public class UserService extends DatedService<
        User,
        UserDetail,
        UserList,
        UserCreate,
        UserUpdate,
        UserRepository> {

    private PasswordEncoder passwordEncoder;
    private TokenService tokenService;
    private NotificationSender notificationSender;
    private TransactionTemplate transactionTemplate;

    @Override
    public UserDetail create(UserCreate view) {
        return checkAndCreate(view);
    }

    private synchronized UserDetail checkAndCreate(UserCreate view) {
        isNull(repository.findByEmail(view.getEmail()), () -> new InvalidAttribute(ENTITY_ALREADY_EXISTS)
                .details(details -> details.property("email", view.getEmail()).clazz(User.class))
                .debugInfo(info -> info.property("email", view.getEmail()).clazz(User.class)));
        return transactionTemplate.execute(status -> {
            view.setPassword(passwordEncoder.encode(view.getPassword()));
            User user = UserCreate.toEntity(view);
            user.setRole(EilRole.USER);
            UserDetail userDetail = UserDetail.toView(repository.create(user));

            Token token = tokenService.generateNewToken(getRef(user.id), true);
            sendConfirmationEmail(user.getEmail(), token);
            return userDetail;
        });
    }

    @Transactional
    public boolean confirmRegistration(String tokenId) {
        Token token = tokenService.get(tokenId);
        notNull(token, () -> new MissingObject(ENTITY_NOT_EXIST)
                .details(details -> details.id(tokenId).clazz(Token.class))
                .debugInfo(info -> info
                        .id(tokenId)
                        .clazz(Token.class)));
        if (tokenService.useToken(tokenId)) {
            User user = token.getUser();
            user.setValidated(true);
            repository.update(user);
            return true;
        } else {
            throw new ForbiddenObject(TOKEN_EXPIRED)
                    .details(details -> details.id(tokenId).clazz(Token.class))
                    .debugInfo(info -> info.id(tokenId).clazz(Token.class));
        }
    }

    @Transactional
    public UserDetail changePassword(ChangePasswordDto dto) {
        User user = repository.find(UserChecker.getUserId());
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new ForbiddenObject(WRONG_PASSWORD);
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));

        repository.update(user);
        return repository.find(UserDetail.class, user.getId());
    }

    private void sendConfirmationEmail(String mail, Token token) {
        if (mail != null) {
            log.debug("Sending notification to requester with email '{}'.", mail);
            notificationSender.sendEmailNotification(
                    NotificationEvent.CONFIRM_REGISTRATION,
                    new TokenNotificationTemplateModel(token.getUser(), token.getId()),
                    mail
            );
            log.info("Registration created, token " + token);
        } else {
            log.debug("User '{}' does not have email. No notification will be sent.", token.getUser());
        }
    }

    @Transactional
    public boolean changeRole(ChangeRoleDto dto) {
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
        User user = repository.find(dto.getUserId());
        notNull(user, () -> new MissingObject(
                ENTITY_NOT_EXIST,
                "User with id '" + dto.getUserId() + "' does not exist"));
        user.setRole(dto.getRole());
        repository.update(user);
        return true;
    }

    @Transactional
    public void requestPasswordResetEmail(String email) {
        User user = repository.findByEmail(email);
        if (user == null) {
            throw new MissingObject(ENTITY_NOT_EXIST);
        }
        if (!user.isValidated()) {
            throw new ForbiddenObject(OBJECT_NOT_IN_STATE_FOR_THIS_OPERATION);
        }
        user.setEmailConfirmationKey(UUID.randomUUID().toString());
        repository.update(user);
        sendPasswordResetEmail(user);
    }

    private void sendPasswordResetEmail(User user) {
        if (user.getEmail() != null) {
            log.debug("Sending notification to user with email '{}'.", user.getEmail());
            notificationSender.sendEmailNotification(
                    NotificationEvent.PASSWORD_RESET,
                    new TokenNotificationTemplateModel(user, user.getEmailConfirmationKey()),
                    user.getEmail()
            );
            log.info("Code for password reset set, emailConfirmationKey " + user.getEmailConfirmationKey());
        } else {
            log.debug("User '{}' does not have email. No notification will be sent.", user);
        }
    }

    @Transactional
    public void resetPassword(PasswordResetDto passwordResetDto) {
        User user = repository.findByEmailConfirmationKey(passwordResetDto.getKey());
        if (user == null) {
            throw new MissingObject(ENTITY_NOT_EXIST);
        }
        if (!user.isValidated()) {
            throw new ForbiddenObject(OBJECT_NOT_IN_STATE_FOR_THIS_OPERATION);
        }
        user.setPassword(passwordEncoder.encode(passwordResetDto.getNewPassword()));
        user.setEmailConfirmationKey(null);
        repository.update(user);
    }


    @Override
    protected void preUpdateHook(User object) {
        super.preUpdateHook(object);
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
    }

    @Override
    public void preDeleteHook(@NotNull String id) {
        super.preDeleteHook(id);
        User user = repository.find(id);
        if (user != null) {
            if (user.getRole() == EilRole.USER) {
                UserChecker.checkUserHasAnyPermission(Permission.SUPER_ADMIN, Permission.ADMIN);
            } else {
                UserChecker.checkUserHasAnyPermission(Permission.SUPER_ADMIN);
            }
        }
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setTokenService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Autowired
    public void setNotificationSender(NotificationSender notificationSender) {
        this.notificationSender = notificationSender;
    }

    @Autowired
    public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }
}
