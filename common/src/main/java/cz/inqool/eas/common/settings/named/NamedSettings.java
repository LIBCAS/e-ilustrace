package cz.inqool.eas.common.settings.named;

import cz.inqool.eas.common.authored.store.AuthoredObject;
import cz.inqool.eas.common.authored.user.UserReference;
import cz.inqool.eas.common.domain.DomainViews;
import cz.inqool.entityviews.ViewableProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

import static cz.inqool.eas.common.domain.DomainViews.DEFAULT;

/**
 * todo: add sharing based on permission.
 */
@DomainViews
@Setter
@Getter
@Entity
@Table(name = "eas_named_settings")
public class NamedSettings extends AuthoredObject<NamedSettings> {
    /**
     * JSON settings
     */
    protected String settings;

    /**
     * Name of the settings.
     */
    protected String name;

    /**
     * Location in app where to use.
     */
    protected String tag;

    /**
     * The settings is shared.
     */
    protected boolean shared;

    @ViewableProperty(views = DEFAULT)
    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "user_id"))
    @AttributeOverride(name = "name", column = @Column(name = "user_name"))
    protected UserReference user;
}
