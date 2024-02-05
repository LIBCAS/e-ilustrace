package cz.inqool.eas.common.ws;

import cz.inqool.eas.common.crypto.CertHelper;
import cz.inqool.eas.common.exception.GeneralException;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.core.io.Resource;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.transport.WebServiceMessageSender;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * Helper for setting up SSL communication.
 */
public class SSLHelper {

    /**
     * Applies one-way or two-way SSL configuration to implementation of {@link WebServiceGatewaySupport}.
     * Adds also RemoveSoapHeadersInterceptor, because otherwise there will be Content-Length already present exceptions
     *
     * @param ws             Webservice client implementation
     * @param keyStore       KeyStore
     * @param keyStorePswd   Password for KeyStore
     * @param trustStore     TrustStore
     * @param trustStorePswd Password for TrustStore
     */
    public static void apply(WebServiceGatewaySupport ws, Resource keyStore,
                      String keyStorePswd, Resource trustStore, String trustStorePswd) {
        apply(ws, keyStore, keyStorePswd, trustStore, trustStorePswd, new DefaultHostnameVerifier());
    }

    /**
     * @see SSLHelper#apply
     *
     * @param hostnameVerifier Instance of custom class implements {@link HostnameVerifier} interface
     */
    public static void apply(WebServiceGatewaySupport ws, Resource keyStore,
                      String keyStorePswd, Resource trustStore, String trustStorePswd, HostnameVerifier hostnameVerifier) {
        WebServiceMessageSender messageSender = getMessageSender(keyStore, keyStorePswd, trustStore, trustStorePswd, hostnameVerifier);
        ws.setMessageSender(messageSender);
    }

    private static WebServiceMessageSender getMessageSender(Resource keyStore, String keyStorePswd, Resource trustStore, String trustStorePswd,
                                                     HostnameVerifier hostnameVerifier) {
        try {
            SSLContextBuilder builder = SSLContextBuilder.create();
            if (keyStore != null) {
                KeyStore store = CertHelper.getKeyStore(keyStore, keyStorePswd);
                builder.loadKeyMaterial(store, keyStorePswd != null ? keyStorePswd.toCharArray() : null);
            }

            if (trustStore != null) {
                KeyStore store = CertHelper.getKeyStore(trustStore, trustStorePswd);
                builder.loadTrustMaterial(store, null);
            } else {
                builder.loadTrustMaterial(new TrustAllStrategy());
            }

            SSLContext sslContext = builder.build();

            CloseableHttpClient client = HttpClients.custom()
                    .addInterceptorFirst(new HttpComponentsMessageSender.RemoveSoapHeadersInterceptor())
                    .setSSLContext(sslContext)
                    .setSSLHostnameVerifier(hostnameVerifier)
                    .build();

            return new HttpComponentsMessageSender(client);
        } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException | IOException | UnrecoverableKeyException | KeyManagementException ex) {
            throw new GeneralException(ex);
        }
    }
}