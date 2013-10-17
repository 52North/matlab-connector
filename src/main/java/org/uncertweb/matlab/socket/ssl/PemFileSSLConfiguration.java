/**
 * Copyright (C) 2013
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */
package org.uncertweb.matlab.socket.ssl;

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

    public PemFileSSLConfiguration(String key, String certificate,
                                   String trusted) {
        this.key = Preconditions.checkNotNull(key);
        this.certificate = Preconditions.checkNotNull(certificate);
        this.trusted = Preconditions.checkNotNull(trusted);
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