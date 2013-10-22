/*
 * Copyright (C) 2012-2013 by it's authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
    private final boolean requireClientAuth;

    public SSLConfiguration(boolean requireClientAuth) {
        this.requireClientAuth = requireClientAuth;
    }

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

    public boolean isRequireClientAuth() {
        return requireClientAuth;
    }
}
