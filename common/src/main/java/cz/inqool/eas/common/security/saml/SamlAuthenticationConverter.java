package cz.inqool.eas.common.security.saml;

import cz.inqool.eas.common.exception.v2.MissingObject;
import cz.inqool.eas.common.security.User;
import cz.inqool.eas.common.security.saml.internal.Saml2Authentication;
import org.joda.time.DateTime;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.schema.*;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.Response;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.saml2.provider.service.authentication.OpenSamlAuthenticationProvider;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticationToken;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static cz.inqool.eas.common.exception.v2.ExceptionCode.SAML_USER_NOT_REGISTERED;

public abstract class SamlAuthenticationConverter implements Converter<OpenSamlAuthenticationProvider.ResponseToken, Saml2Authentication> {

    @Transactional
    @Override
    public Saml2Authentication convert(OpenSamlAuthenticationProvider.ResponseToken responseToken) {
        Saml2AuthenticationToken token = responseToken.getToken();
        Response response = responseToken.getResponse();

        Assertion assertion = CollectionUtils.firstElement(response.getAssertions());
        String identifier = assertion.getSubject().getNameID().getValue();

        User user = findUser(identifier);
        if (user == null) {
            if (isCreateMissingAccount()) {
                Map<String, List<Object>> attributes = getAssertionAttributes(assertion);
                user = createUser(identifier, attributes);
            } else {
                throw new MissingObject(SAML_USER_NOT_REGISTERED)
                        .details(details -> details.property("type", User.class.getSimpleName()))
                        .debugInfo(info -> info.clazz(User.class).property("identifier", identifier));
            }
        } else {
            if (isUpdateAccountDetails()) {
                Map<String, List<Object>> attributes = getAssertionAttributes(assertion);
                user = updateUser(identifier, attributes);
            }
        }

        return new Saml2Authentication(user, token.getSaml2Response(), user.getAuthorities());
    }

    public abstract User findUser(String identifier);

    public abstract User createUser(String identifier, Map<String, List<Object>> attributes);

    public abstract User updateUser(String identifier, Map<String, List<Object>> attributes);

    public abstract boolean isCreateMissingAccount();

    public abstract boolean isUpdateAccountDetails();

    /**
     * Converts all SAML assertions to simple Map.
     *
     */
    protected Map<String, List<Object>> getAssertionAttributes(Assertion assertion) {
        Map<String, List<Object>> attributeMap = new LinkedHashMap<>();
        for (AttributeStatement attributeStatement : assertion.getAttributeStatements()) {
            for (Attribute attribute : attributeStatement.getAttributes()) {
                List<Object> attributeValues = new ArrayList<>();
                for (XMLObject xmlObject : attribute.getAttributeValues()) {
                    Object attributeValue = getXmlObjectValue(xmlObject);
                    if (attributeValue != null) {
                        attributeValues.add(attributeValue);
                    }
                }
                attributeMap.put(attribute.getName(), attributeValues);
            }
        }
        return attributeMap;
    }

    protected Object getXmlObjectValue(XMLObject xmlObject) {
        if (xmlObject instanceof XSAny) {
            return ((XSAny) xmlObject).getTextContent();
        }
        if (xmlObject instanceof XSString) {
            return ((XSString) xmlObject).getValue();
        }
        if (xmlObject instanceof XSInteger) {
            return ((XSInteger) xmlObject).getValue();
        }
        if (xmlObject instanceof XSURI) {
            return ((XSURI) xmlObject).getValue();
        }
        if (xmlObject instanceof XSBoolean) {
            XSBooleanValue xsBooleanValue = ((XSBoolean) xmlObject).getValue();
            return (xsBooleanValue != null) ? xsBooleanValue.getValue() : null;
        }
        if (xmlObject instanceof XSDateTime) {
            DateTime dateTime = ((XSDateTime) xmlObject).getValue();
            return (dateTime != null) ? Instant.ofEpochMilli(dateTime.getMillis()) : null;
        }
        return null;
    }

    protected <T> T firstValue(List<Object> list) {
        return (T) list.stream().findFirst().orElse(null);
    }
}
