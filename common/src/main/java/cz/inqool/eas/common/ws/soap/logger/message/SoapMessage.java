package cz.inqool.eas.common.ws.soap.logger.message;

import cz.inqool.eas.common.authored.store.AuthoredObject;
import cz.inqool.eas.common.domain.DomainViews;
import cz.inqool.entityviews.Viewable;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Soap message is used for logging request/response of web service
 */
@Viewable
@DomainViews
@Getter
@Setter
@Entity
@Table(name = "eas_soap_message")
@BatchSize(size = 100)
public class SoapMessage extends AuthoredObject<SoapMessage> {
    /**
     * Web service's name.
     */
    protected String service;

    /**
     * Operation name.
     */
    protected String localPart;

    /**
     * Operation execution duration
     */
    protected Long duration;

    /**
     * Formatted request message.
     */
    protected String request;

    /**
     * Formatted response message.
     */
    protected String response;

    /**
     * Response HTTP status
     */
    protected Integer httpStatus;

    /**
     * Exception thrown during request execution (if any was thrown)
     */
    protected String error;
}

