package cz.inqool.eas.common.signing.request;

import cz.inqool.eas.common.domain.DomainViews;
import cz.inqool.eas.common.domain.store.DomainObject;
import cz.inqool.eas.common.storage.file.File;
import cz.inqool.entityviews.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import static cz.inqool.eas.common.domain.DomainViews.*;

/**
 * File to sign.
 */
@Viewable
@DomainViews
@ViewableClass(views = SignContent.CASCADED)
@ViewableMapping(views = SignContent.CASCADED, mappedTo = DEFAULT)
@ViewableAnnotation(value = {Entity.class, BatchSize.class, Table.class}, views = SignContent.CASCADED)
@Getter
@Setter
@Entity
@Table(name = "eas_sign_content")
public class SignContent extends DomainObject<SignContent> {
    public static final String CASCADED = "cascaded";

    @ViewableProperty(views = {CREATE, UPDATE, DETAIL, LIST})
    @ViewableMapping(views = {CREATE, UPDATE}, useRef = true)
    @NotNull
    @Fetch(FetchMode.SELECT)
    @ManyToOne
    protected File toSign;

    /**
     * Signed file.
     */
    @ViewableProperty(views = {DETAIL, LIST})
    @Fetch(FetchMode.SELECT)
    @ManyToOne
    protected File signed;

    @Column(name = "list_order")
    protected Integer order;
}
