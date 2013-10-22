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
package org.uncertweb.matlab.socket.ssl;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.Properties;

import com.google.common.base.Preconditions;
import com.google.common.io.Closeables;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class KeyStoreSSLConfiguration extends SSLConfiguration {
    public static final String KEY_STORE_PATH = "keyStore.path";
    public static final String KEY_STORE_PASS = "keyStore.pass";
    public static final String KEY_STORE_TYPE = "keyStore.type";
    public static final String TRUST_STORE_PATH = "trustStore.path";
    public static final String TRUST_STORE_PASS = "trustStore.pass";
    public static final String TRUST_STORE_TYPE = "trustStore.type";
    public static final String CLIENT_AUTH = "clientAuth";
    
    private final KeyStoreOptions trustStoreOptions;
    private final KeyStoreOptions keyStoreOptions;

    public KeyStoreSSLConfiguration(KeyStoreOptions trustStoreOptions,
                                    KeyStoreOptions keyStoreOptions,
                                    boolean requireClientAuth) {
        super(requireClientAuth);
        this.trustStoreOptions = Preconditions.checkNotNull(trustStoreOptions);
        this.keyStoreOptions = Preconditions.checkNotNull(keyStoreOptions);
    }

    @Override
    protected KeyStore createKeyStore() throws IOException, GeneralSecurityException {
        return this.keyStoreOptions.read();
    }

    @Override
    public char[] getKeyStorePass() {
        return this.keyStoreOptions.getPass().toCharArray();
    }

    @Override
    protected KeyStore createTrustStore() throws IOException, GeneralSecurityException {
        return this.trustStoreOptions.read();
    }

    @Override
    public char[] getTrustStorePass() {
        return this.trustStoreOptions.getPass().toCharArray();
    }

     public static SSLConfiguration load(Properties p) {
        checkNotNull(p);
        String keyStorePath = emptyToNull(p.getProperty(KEY_STORE_PATH, null));
        String keyStorePass = emptyToNull(p.getProperty(KEY_STORE_PASS, null));
        String keyStoreType = emptyToNull(p.getProperty(KEY_STORE_TYPE, null));
        String trustStorePath = emptyToNull(p.getProperty(TRUST_STORE_PATH, null));
        String trustStorePass = emptyToNull(p.getProperty(TRUST_STORE_PASS, null));
        String trustStoreType = emptyToNull(p.getProperty(TRUST_STORE_TYPE, null));
        String clientAuth = emptyToNull(p.getProperty(CLIENT_AUTH, "true"));

        return new KeyStoreSSLConfiguration(
                new KeyStoreOptions(checkNotNull(trustStorePath, TRUST_STORE_PATH),
                                    checkNotNull(trustStorePass, KEY_STORE_PASS),
                                    trustStoreType),
                new KeyStoreOptions(checkNotNull(keyStorePath, KEY_STORE_PATH),
                                    checkNotNull(keyStorePass, keyStorePass),
                                    keyStoreType),
                Boolean.parseBoolean(clientAuth));
    }

    public static SSLConfiguration load(String path) throws IOException {
        final File file = new File(checkNotNull(path));
        if (file.exists() && file.isFile() && file.canRead()) {
            return load(file);
        } else {
            throw new IOException("Can not read " + file);
        }
    }

    public static SSLConfiguration load(File file) throws IOException {
        return load(new FileInputStream(checkNotNull(file)));
    }

    public static SSLConfiguration load(InputStream in) throws IOException {
        try {
            Properties p = new Properties();
            p.load(checkNotNull(in));
            return load(p);
        } finally {
            Closeables.close(in, true);
        }
    }
}
