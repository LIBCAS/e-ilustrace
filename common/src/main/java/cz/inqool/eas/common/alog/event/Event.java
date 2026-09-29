package cz.inqool.eas.common.alog.event;

import cz.inqool.eas.common.authored.store.AuthoredObject;
import cz.inqool.eas.common.authored.user.UserReference;
import cz.inqool.eas.common.domain.DomainViews;
import cz.inqool.eas.common.module.ModuleReference;
import cz.inqool.entityviews.Viewable;
import cz.inqool.entityviews.ViewableProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static cz.inqool.eas.common.domain.DomainViews.*;

/**
 * Audit Log event.
 */
@Viewable
@DomainViews
@Getter
@Setter
@Entity
@Table(name = "eas_alog_event")
public class Event extends AuthoredObject<Event> {
    /**
     * Source type.
     */
    @Enumerated(EnumType.STRING)
    protected EventSourceType sourceType;

    /**
     * Source of event.
     */
    @NotNull
    protected String source;

    /**
     * Module of source of event.
     */
    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "module_id"))
    @AttributeOverride(name = "name", column = @Column(name = "module_name"))
    protected ModuleReference module;

    /**
     * IpAddress of the caller.
     */
    @Nullable
    protected String ipAddress;

    /**
     * Severity of the event.
     */
    @Schema(description = "Severity of the event")
    @Enumerated(EnumType.STRING)
    protected EventSeverity severity;

    /**
     * Message of the event.
     */
    @Nationalized
    protected String message;

    /**
     * JSON formated detail of the event.
     */
    @ViewableProperty(views = {CREATE, UPDATE, DETAIL})
    @Nullable
    @Nationalized
    protected String detail;

    /**
     * User that generated the event.
     */
    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "user_id"))
    @AttributeOverride(name = "name", column = @Column(name = "user_name"))
    protected UserReference user;

    /**
     * Should be send to syslog.
     */
    protected boolean syslog;
}
