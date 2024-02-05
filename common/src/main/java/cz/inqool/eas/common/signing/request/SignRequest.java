package cz.inqool.eas.common.signing.request;

import cz.inqool.eas.common.authored.store.AuthoredObject;
import cz.inqool.eas.common.authored.user.UserReference;
import cz.inqool.eas.common.domain.DomainViews;
import cz.inqool.entityviews.Viewable;
import cz.inqool.entityviews.ViewableMapping;
import cz.inqool.entityviews.ViewableProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;

import java.util.Set;

import static cz.inqool.eas.common.domain.DomainViews.*;

/**
 * Request to sign document.
 */
@Viewable
@DomainViews
@Getter
@Setter
@Entity
@Table(name = "eas_sign_request")
public class SignRequest extends AuthoredObject<SignRequest> {
    /**
     * Name.
     */
    protected String name;

    /**
     * Files to sign.
     */
    @ViewableProperty(views = {CREATE, UPDATE, DETAIL })
    @ViewableMapping(views = {CREATE, UPDATE }, mappedTo = SignContent.CASCADED)
    @ViewableMapping(views = DETAIL, mappedTo = DETAIL)
    @BatchSize(size = 100)
    @Fetch(FetchMode.SELECT)
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "request_id")
    @OrderBy("list_order")
    protected Set<SignContent> contents;

    /**
     * User which should sign the file.
     */
    @ViewableProperty(views = {CREATE, UPDATE, DETAIL, LIST})
    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "user_id"))
    @AttributeOverride(name = "name", column = @Column(name = "user_name"))
    protected UserReference user;

    /**
     * State.
     */
    @ViewableProperty(views = {DETAIL, LIST})
    @Enumerated(EnumType.STRING)
    protected SignRequestState state;

    /**
     * Description of error/reason.
     */
    @ViewableProperty(views = {DETAIL, LIST})
    protected String error;

    /**
     * Identifier from application.
     */
    protected String identifier;
}
