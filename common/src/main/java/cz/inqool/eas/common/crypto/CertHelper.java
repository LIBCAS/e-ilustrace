package cz.inqool.eas.common.crypto;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class CertHelper {
    /**
     * Loads jks/pfx keystore based on filename extension
     *
     * @param resource Keystore to load from
     * @param password Password, can be null
     * @return Loaded keystore
     * @throws IOException              if there is problem with loading KeyStore from given resource
     * @throws KeyStoreException        if there is no KeyStore for given type (PKCS12/JKS)
     * @throws CertificateException     if no certificate in the key store could be loaded
     * @throws NoSuchAlgorithmException if the algorithm used to check the integrity of the keystore cannot be found
     */
    public static KeyStore getKeyStore(Resource resource, String password) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {

        try (InputStream stream = resource.getInputStream()) {
            KeyStore keyStore;

            String filename = resource.getFilename().toLowerCase();
            if (filename.endsWith("pfx") || filename.endsWith("p12")) {
                keyStore = KeyStore.getInstance("PKCS12");
            } else {
                keyStore = KeyStore.getInstance("JKS");
            }

            char[] passwordChars = password != null ? password.toCharArray() : null;
            keyStore.load(stream, passwordChars);

            return keyStore;
        }
    }
}
