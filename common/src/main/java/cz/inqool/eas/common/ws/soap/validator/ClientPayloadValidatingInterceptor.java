package cz.inqool.eas.common.ws.soap.validator;

import cz.inqool.eas.common.authored.user.UserReference;
import cz.inqool.eas.common.ws.soap.SoapExceptionUtils;
import cz.inqool.eas.common.ws.soap.logger.SoapLoggerUtils;
import org.springframework.ws.client.support.interceptor.PayloadValidatingInterceptor;
import org.springframework.ws.client.support.interceptor.WebServiceValidationException;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapBody;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.SoapFaultDetailElement;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.ws.soap.soap12.Soap12Fault;
import org.xml.sax.SAXParseException;

import java.util.Locale;

import static cz.inqool.eas.common.ws.soap.validator.PayloadValidationFaultDetailElements.*;

public class ClientPayloadValidatingInterceptor extends PayloadValidatingInterceptor {

    @Override
    protected boolean handleRequestValidationErrors(MessageContext messageContext, SAXParseException[] errors) {
        for (SAXParseException error : errors) {
            logger.error("XML validation error on request: " + error.getMessage());
        }

        if (logger.isDebugEnabled()) {
            String soapBody = SoapLoggerUtils.extractSoapBody(messageContext.getRequest());
            logger.debug("Invalid client request body: \n" + soapBody + "\n");
        }

        addClientSoapFault(messageContext, errors);

        throw new WebServiceValidationException(errors);
    }

    @Override
    protected boolean handleResponseValidationErrors(MessageContext messageContext, SAXParseException[] errors) throws WebServiceValidationException {
        for (SAXParseException error : errors) {
            logger.warn("XML validation error on response: " + error.getMessage());
        }

        if (logger.isDebugEnabled()) {
            String soapBody = SoapLoggerUtils.extractSoapBody(messageContext.getResponse());
            logger.debug("Invalid client response body: \n" + soapBody + "\n");
        }

        addServerSoapFault(messageContext, errors);

        return false;
    }

    private void addClientSoapFault(MessageContext messageContext, SAXParseException[] errors) {
        if (messageContext.getResponse() instanceof SaajSoapMessage) {
            SaajSoapMessage soapMessage = (SaajSoapMessage) messageContext.getResponse();
            SoapBody soapBody = soapMessage.getSoapBody();
            SoapFault soapFault = soapBody.addClientOrSenderFault("Invalid request", Locale.ENGLISH);
            addSoapFault(soapFault, errors);
        }
    }

    private void addServerSoapFault(MessageContext messageContext, SAXParseException[] errors) {
        if (messageContext.getResponse() instanceof SaajSoapMessage) {
            SaajSoapMessage soapMessage = (SaajSoapMessage) messageContext.getResponse();
            SoapBody soapBody = soapMessage.getSoapBody();
            SoapFault soapFault = soapBody.addServerOrReceiverFault("Invalid response", Locale.ENGLISH);

            addSoapFault(soapFault, errors);
        }
    }

    private void addSoapFault(SoapFault soapFault, SAXParseException[] errors) {
        UserReference user = SoapExceptionUtils.getUserReference();
        if (user != null) {
            soapFault.setFaultActorOrRole(user.getName());
        }

        SoapFaultDetail soapDetail = soapFault.addFaultDetail();

        soapDetail.addFaultDetailElement(Q_TIMESTAMP).addText(SoapExceptionUtils.getTimestamp());
        if (soapFault instanceof Soap12Fault) {
            Soap12Fault soap12Fault = (Soap12Fault) soapFault;
            soap12Fault.addFaultSubcode(VALIDATION_ERROR);
        } else {
            soapDetail.addFaultDetailElement(Q_CODE).addText(VALIDATION_ERROR.getLocalPart());
        }

        if (errors.length > 0) {
            for (SAXParseException error : errors) {
                SoapFaultDetailElement soapFaultDetail = soapDetail.addFaultDetailElement(Q_ERROR);
                if (error.getLineNumber() != -1) {
                    soapFaultDetail.addAttribute(Q_LINE_NUMBER, String.valueOf(error.getLineNumber()));
                }
                if (error.getColumnNumber() != -1) {
                    soapFaultDetail.addAttribute(Q_COLUMN_NUMBER, String.valueOf(error.getColumnNumber()));
                }
                soapFaultDetail.addText(error.getMessage());
            }
        }
    }
}
