package cz.inqool.eas.common.ws;

import cz.inqool.eas.common.crypto.CertHelper;
import cz.inqool.eas.common.exception.GeneralException;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.core.io.Resource;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * Helper for setting up SSL communication on existing {@link HttpClientBuilder}.
 */
public class SSLHelper2 {
    /**
     * Applies one-way or two-way SSL configuration to implementation of {@link HttpClientBuilder}.
     *
     * @param builder        Custom HttpClientBuilder
     * @param keyStore       KeyStore
     * @param keyStorePswd   Password for KeyStore
     * @param trustStore     TrustStore
     * @param trustStorePswd Password for TrustStore
     */
    public static void apply(HttpClientBuilder builder, Resource keyStore,
                             String keyStorePswd, Resource trustStore, String trustStorePswd) {
        apply(builder, keyStore, keyStorePswd, trustStore, trustStorePswd, new DefaultHostnameVerifier());
    }

    /**
     * @see SSLHelper2#apply
     *
     * @param hostnameVerifier Instance of custom class implements {@link HostnameVerifier} interface
     */
    public static void apply(HttpClientBuilder httpClientBuilder, Resource keyStore,
                      String keyStorePswd, Resource trustStore, String trustStorePswd, HostnameVerifier hostnameVerifier) {
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

            httpClientBuilder
                    .setSSLContext(sslContext)
                    .setSSLHostnameVerifier(hostnameVerifier);

        } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException | IOException | UnrecoverableKeyException | KeyManagementException ex) {
            throw new GeneralException(ex);
        }
    }
}