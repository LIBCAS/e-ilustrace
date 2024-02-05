package cz.inqool.eas.eil.user;

import cz.inqool.eas.common.dated.DatedService;
import cz.inqool.eas.common.exception.v2.ForbiddenObject;
import cz.inqool.eas.common.exception.v2.ForbiddenOperation;
import cz.inqool.eas.common.exception.v2.InvalidAttribute;
import cz.inqool.eas.common.exception.v2.MissingObject;
import cz.inqool.eas.eil.notification.NotificationSender;
import cz.inqool.eas.eil.notification.template.model.TokenNotificationTemplateModel;
import cz.inqool.eas.eil.security.Permission;
import cz.inqool.eas.eil.security.UserChecker;
import cz.inqool.eas.eil.security.token.Token;
import cz.inqool.eas.eil.security.token.TokenService;
import cz.inqool.eas.eil.user.dto.ChangeRoleDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.validation.constraints.NotNull;

import static cz.inqool.eas.common.exception.v2.ExceptionCode.ENTITY_NOT_EXIST;
import static cz.inqool.eas.common.utils.AssertionUtils.*;
import static cz.inqool.eas.eil.config.exception.EilExceptionCode.ENTITY_ALREADY_EXISTS;
import static cz.inqool.eas.eil.config.exception.EilExceptionCode.TOKEN_EXPIRED;
import static cz.inqool.eas.eil.exception.EilExceptionCode.MISSING_PERMISSION_FOR_OPERATION;
import static cz.inqool.eas.eil.notification.event.NotificationEvent.CONFIRM_REGISTRATION;

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
    public UserDetail changePassword(String password) {
        User user = repository.find(UserChecker.getUserId());
        user.setPassword(passwordEncoder.encode(password));
        repository.update(user);
        return repository.find(UserDetail.class, user.getId());
    }

    private void sendConfirmationEmail(String mail, Token token) {
        if (mail != null) {
            log.debug("Sending notification to requester with email '{}'.", mail);
            notificationSender.sendEmailNotification(
                    CONFIRM_REGISTRATION,
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
        User admin = repository.find(UserChecker.getUserId());
        isTrue(admin.getRole() == EilRole.ADMIN || admin.getRole() == EilRole.SUPER_ADMIN,
                () -> new ForbiddenOperation(MISSING_PERMISSION_FOR_OPERATION,
                        "User with id '" + UserChecker.getUserId() + "' and role '" + admin.getRole() + "' does not have permission for this operation."));
        User user = repository.find(dto.getUserId());
        notNull(user, () -> new MissingObject(
                ENTITY_NOT_EXIST,
                "User with id '" + dto.getUserId() + "' does not exist"));
        user.setRole(dto.getRole());
        repository.update(user);
        return true;
    }

    @Override
    protected void preUpdateHook(User object) {
        super.preUpdateHook(object);
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
    }

    @Override
    public void preDeleteHook(@NotNull String id) {
        super.preDeleteHook(id);
        UserChecker.checkUserHasAnyPermission(Permission.ADMIN);
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
