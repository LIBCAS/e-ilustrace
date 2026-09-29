package cz.inqool.eas.common.security.personal;

import cz.inqool.eas.common.authored.user.UserReference;
import cz.inqool.eas.common.dated.store.DatedObject;
import cz.inqool.eas.common.domain.DomainViews;
import cz.inqool.entityviews.Viewable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;

/**
 * Událost související s přihlášením nebo odhlášením.
 */
@Viewable
@DomainViews
@Getter
@Setter
@BatchSize(size = 100)
@Entity
@Table(name = "eas_personal_event")
public class PersonalEvent extends DatedObject<PersonalEvent> {

    @Enumerated(EnumType.STRING)
    protected PersonalEventType type;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "user_id"))
    @AttributeOverride(name = "name", column = @Column(name = "user_name"))
    protected UserReference user;

    /**
     * JSON Data: IP, others.
     */
    protected String data;
}
