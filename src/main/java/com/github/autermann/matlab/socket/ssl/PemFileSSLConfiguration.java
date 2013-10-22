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
package com.github.autermann.matlab.socket.ssl;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class PemFileSSLConfiguration extends SSLConfiguration {
    private static final Logger log = LoggerFactory.getLogger(PemFileSSLConfiguration.class);
    private static final String PASSWORD = "password";
    private final String trusted;
    private final String key;
    private final String certificate;

    public PemFileSSLConfiguration(String keyFile,
                                   String certificateFile,
                                   String trustedFile,
                                   boolean requireClientAuth) {
        super(requireClientAuth);
        this.key = Preconditions.checkNotNull(keyFile);
        this.certificate = Preconditions.checkNotNull(certificateFile);
        this.trusted = Preconditions.checkNotNull(trustedFile);
    }

    @Override
    public char[] getKeyStorePass() {
        return PASSWORD.toCharArray();
    }

    @Override
    public char[] getTrustStorePass() {
        return PASSWORD.toCharArray();
    }

    @Override
    protected KeyStore createTrustStore()
            throws IOException, GeneralSecurityException {
        KeyStore store = SSLUtils.createEmptyKeyStore();
        log.debug("Creating Trust Store");
        for (X509Certificate cert : SSLUtils.readCertificates(this.trusted)) {
            store.setCertificateEntry(SSLUtils.randomAlias(), cert);
        }
        return store;
    }

    @Override
    protected KeyStore createKeyStore()
            throws IOException, GeneralSecurityException {
        KeyStore store = SSLUtils.createEmptyKeyStore();
        log.debug("Creating Key Store");
        store.setKeyEntry(SSLUtils.randomAlias(),
                          SSLUtils.readKey(this.key),
                          getKeyStorePass(),
                          SSLUtils.readChain(this.certificate));
        return store;
    }
}