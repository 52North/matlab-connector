/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uncertweb.matlab.socket.ssl;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public abstract class SSLConfiguration {
    private KeyStore keyStore;
    private KeyStore trustStore;

    public KeyStore getKeyStore()
            throws IOException, GeneralSecurityException {
        if (this.keyStore == null) {
            this.keyStore = createKeyStore();
        }
        return this.keyStore;
    }

    public KeyStore getTrustStore()
            throws IOException, GeneralSecurityException {
        if (this.trustStore == null) {
            this.trustStore = createTrustStore();
        }
        return this.trustStore;
    }

    protected abstract KeyStore createTrustStore()
            throws IOException, GeneralSecurityException;

    protected abstract KeyStore createKeyStore()
            throws IOException, GeneralSecurityException;

    public abstract char[] getKeyStorePass();

    public abstract char[] getTrustStorePass();
}
