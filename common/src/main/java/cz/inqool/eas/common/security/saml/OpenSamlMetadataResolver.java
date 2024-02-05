/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cz.inqool.eas.common.security.saml;

import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import net.shibboleth.utilities.java.support.xml.SerializeSupport;
import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.saml2.metadata.impl.EntityDescriptorMarshaller;
import org.opensaml.security.credential.UsageType;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.X509Certificate;
import org.opensaml.xmlsec.signature.X509Data;
import org.springframework.security.saml2.provider.service.metadata.Saml2MetadataResolver;
import org.w3c.dom.Element;

import org.springframework.security.saml2.Saml2Exception;
import org.springframework.security.saml2.core.OpenSamlInitializationService;
import org.springframework.security.saml2.core.Saml2X509Credential;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.util.Assert;

/**
 * Copy of {@link org.springframework.security.saml2.provider.service.metadata.OpenSamlMetadataResolver}
 * with specified md prefix for EntityDescriptor
 *
 * This hack is necesarry because Azure ADFS can not read valid xml file with prefix specified only on SPSSO and not EntityDescriptor.
 */
public final class OpenSamlMetadataResolver implements Saml2MetadataResolver {
    public static final QName ENTITY_DESCRIPTOR_ELEMENT_NAME = new QName(SAMLConstants.SAML20MD_NS, EntityDescriptor.DEFAULT_ELEMENT_LOCAL_NAME,
            SAMLConstants.SAML20MD_PREFIX);


    static {
        OpenSamlInitializationService.initialize();
    }

    private final EntityDescriptorMarshaller entityDescriptorMarshaller;

    public OpenSamlMetadataResolver() {
        this.entityDescriptorMarshaller = (EntityDescriptorMarshaller) XMLObjectProviderRegistrySupport
                .getMarshallerFactory().getMarshaller(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        Assert.notNull(this.entityDescriptorMarshaller, "entityDescriptorMarshaller cannot be null");
    }

    @Override
    public String resolve(RelyingPartyRegistration relyingPartyRegistration) {
        EntityDescriptor entityDescriptor = build(ENTITY_DESCRIPTOR_ELEMENT_NAME);
        entityDescriptor.setEntityID(relyingPartyRegistration.getEntityId());
        SPSSODescriptor spSsoDescriptor = buildSpSsoDescriptor(relyingPartyRegistration);
        entityDescriptor.getRoleDescriptors(SPSSODescriptor.DEFAULT_ELEMENT_NAME).add(spSsoDescriptor);
        return serialize(entityDescriptor);
    }

    private SPSSODescriptor buildSpSsoDescriptor(RelyingPartyRegistration registration) {
        SPSSODescriptor spSsoDescriptor = build(SPSSODescriptor.DEFAULT_ELEMENT_NAME);
        spSsoDescriptor.addSupportedProtocol(SAMLConstants.SAML20P_NS);
        spSsoDescriptor.setWantAssertionsSigned(true);
        spSsoDescriptor.getKeyDescriptors()
                .addAll(buildKeys(registration.getSigningX509Credentials(), UsageType.SIGNING));
        spSsoDescriptor.getKeyDescriptors()
                .addAll(buildKeys(registration.getDecryptionX509Credentials(), UsageType.ENCRYPTION));
        spSsoDescriptor.getAssertionConsumerServices().add(buildAssertionConsumerService(registration));
        return spSsoDescriptor;
    }

    private List<KeyDescriptor> buildKeys(Collection<Saml2X509Credential> credentials, UsageType usageType) {
        List<KeyDescriptor> list = new ArrayList<>();
        for (Saml2X509Credential credential : credentials) {
            KeyDescriptor keyDescriptor = buildKeyDescriptor(usageType, credential.getCertificate());
            list.add(keyDescriptor);
        }
        return list;
    }

    private KeyDescriptor buildKeyDescriptor(UsageType usageType, java.security.cert.X509Certificate certificate) {
        KeyDescriptor keyDescriptor = build(KeyDescriptor.DEFAULT_ELEMENT_NAME);
        KeyInfo keyInfo = build(KeyInfo.DEFAULT_ELEMENT_NAME);
        X509Certificate x509Certificate = build(X509Certificate.DEFAULT_ELEMENT_NAME);
        X509Data x509Data = build(X509Data.DEFAULT_ELEMENT_NAME);
        try {
            x509Certificate.setValue(new String(Base64.getEncoder().encode(certificate.getEncoded())));
        }
        catch (CertificateEncodingException ex) {
            throw new Saml2Exception("Cannot encode certificate " + certificate.toString());
        }
        x509Data.getX509Certificates().add(x509Certificate);
        keyInfo.getX509Datas().add(x509Data);
        keyDescriptor.setUse(usageType);
        keyDescriptor.setKeyInfo(keyInfo);
        return keyDescriptor;
    }

    private AssertionConsumerService buildAssertionConsumerService(RelyingPartyRegistration registration) {
        AssertionConsumerService assertionConsumerService = build(AssertionConsumerService.DEFAULT_ELEMENT_NAME);
        assertionConsumerService.setLocation(registration.getAssertionConsumerServiceLocation());
        assertionConsumerService.setBinding(registration.getAssertionConsumerServiceBinding().getUrn());
        assertionConsumerService.setIndex(1);
        return assertionConsumerService;
    }

    @SuppressWarnings("unchecked")
    private <T> T build(QName elementName) {
        XMLObjectBuilder<?> builder = XMLObjectProviderRegistrySupport.getBuilderFactory().getBuilder(elementName);
        if (builder == null) {
            throw new Saml2Exception("Unable to resolve Builder for " + elementName);
        }
        return (T) builder.buildObject(elementName);
    }

    private String serialize(EntityDescriptor entityDescriptor) {
        try {
            Element element = this.entityDescriptorMarshaller.marshall(entityDescriptor);
            return SerializeSupport.prettyPrintXML(element);
        }
        catch (Exception ex) {
            throw new Saml2Exception(ex);
        }
    }

}
