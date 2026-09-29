package cz.inqool.eas.common.security.personal;

import cz.inqool.eas.common.authored.user.UserGenerator;
import cz.inqool.eas.common.authored.user.UserReference;
import cz.inqool.eas.common.dated.DatedService;
import cz.inqool.eas.common.domain.index.dto.Result;
import cz.inqool.eas.common.domain.index.dto.filter.EqFilter;
import cz.inqool.eas.common.domain.index.dto.filter.IdsFilter;
import cz.inqool.eas.common.domain.index.dto.params.Params;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import javax.transaction.Transactional;

import static cz.inqool.eas.common.authored.AuthoredUtils.userFilter;
import static cz.inqool.eas.common.utils.AssertionUtils.coalesce;
import static java.util.Collections.emptySet;

@Slf4j
public class PersonalEventService extends DatedService<
        PersonalEvent,
        PersonalEventDetail,
        PersonalEventList,
        PersonalEventCreate,
        PersonalEventUpdate,
        PersonalEventRepository
        > {

    /**
     * Retrieves list view of objects that respect the selected {@link Params} and was created by current user.
     *
     * @param params Parameters to comply with
     * @return Sorted list of objects with total number
     */
    @Transactional
    public Result<PersonalEventList> listMine(@Nullable Params params) {
        params = coalesce(params, Params::new);

        UserReference user = UserGenerator.generateValue();

        params.addFilter(new EqFilter("user.id", user.getId()));
        return super.list(params);
    }

    /**
     * Todo: save IP address
     * @param user
     */
    @Transactional
    public void saveLoginSuccessEvent(UserReference user) {
        PersonalEvent event = new PersonalEvent();
        event.setType(PersonalEventType.LOGIN_SUCCESSFUL);
        event.setUser(user);

        repository.create(event);
    }

    /**
     * Todo: save IP address
     * @param user
     */
    @Transactional
    public void saveLoginFailedEvent(UserReference user) {
        PersonalEvent event = new PersonalEvent();
        event.setType(PersonalEventType.LOGIN_FAILED);
        event.setUser(user);

        repository.create(event);
    }

    /**
     * Todo: save IP address
     * @param user
     */
    @Transactional
    public void saveLogoutEvent(UserReference user) {
        PersonalEvent event = new PersonalEvent();
        event.setType(PersonalEventType.LOGOUT);
        event.setUser(user);

        repository.create(event);
    }

    /**
     * @param user
     */
    @Transactional
    public void saveLogoutAutomaticEvent(UserReference user) {
        PersonalEvent event = new PersonalEvent();
        event.setType(PersonalEventType.LOGOUT_AUTOMATIC);
        event.setUser(user);

        repository.create(event);
    }
}
