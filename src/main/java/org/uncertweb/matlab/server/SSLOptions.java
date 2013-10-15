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
package org.uncertweb.matlab.server;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.google.common.io.Closeables;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class SSLOptions {
    public static final String KEY_STORE_PASS = "keyStorePass";
    public static final String TRUST_STORE_PASS = "trustStorePass";
    public static final String KEY_STORE_PATH = "keyStorePath";
    public static final String TRUST_STORE_PATH = "trustStorePath";
    private final String trustStorePath;
    private final String keyStorePath;
    private final char[] keyStorePass;
    private final char[] trustStorePass;

    private SSLOptions(String trustStorePath, String trustStorePass,
                             String keyStorePath, String keyStorePass) {
        this.trustStorePath = checkNotNull(trustStorePath, KEY_STORE_PATH);
        this.trustStorePass = checkNotNull(trustStorePass, KEY_STORE_PASS).toCharArray();
        this.keyStorePath = checkNotNull(keyStorePath, KEY_STORE_PATH);
        this.keyStorePass = checkNotNull(keyStorePass, KEY_STORE_PASS).toCharArray();
    }

    public String getTrustStorePath() {
        return trustStorePath;
    }

    public String getKeyStorePath() {
        return keyStorePath;
    }

    public char[] getKeyStorePass() {
        return keyStorePass;
    }

    public char[] getTrustStorePass() {
        return trustStorePass;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String trustStorePath;
        private String trustStorePass;
        private String keyStorePath;
        private String keyStorePass;

        public Builder() {
        }

        public Builder load(String path) throws IOException {
            final File file = new File(checkNotNull(path));
            if (file.exists() && file.isFile() && file.canRead()) {
                return load(file);
            } else {
                throw new IOException("Can not read " + file);
            }
        }

        public Builder load(File file) throws IOException {
            return load(new FileInputStream(checkNotNull(file)));
        }

        public Builder load(InputStream in) throws IOException {
            try {
                Properties p = new Properties();
                p.load(checkNotNull(in));
                return load(p);
            } finally {
                Closeables.close(in, true);
            }

        }

        public Builder load(Properties p) {
            checkNotNull(p);
            if (p.contains(KEY_STORE_PATH)) {
                setKeyStorePath(p.getProperty(KEY_STORE_PATH));
            }
            if (p.contains(KEY_STORE_PASS)) {
                setKeyStorePass(p.getProperty(KEY_STORE_PASS));
            }
            if (p.contains(TRUST_STORE_PATH)) {
                setTrustStorePath(p.getProperty(TRUST_STORE_PATH));
            }
            if (p.contains(TRUST_STORE_PASS)) {
                setTrustStorePass(p.getProperty(TRUST_STORE_PASS));
            }
            return this;
        }

        public Builder setTrustStorePath(String trustStorePath) {
            this.trustStorePath = checkNotNull(trustStorePath, KEY_STORE_PATH);
            return this;
        }

        public Builder setTrustStorePass(String trustStorePass) {
            this.trustStorePass = checkNotNull(trustStorePass, TRUST_STORE_PASS);
            return this;
        }

        public Builder setKeyStorePath(String keyStorePath) {
            this.keyStorePath = checkNotNull(keyStorePath, KEY_STORE_PATH);
            return this;
        }

        public Builder setKeyStorePass(String keyStorePass) {
            this.keyStorePass = checkNotNull(keyStorePass, KEY_STORE_PASS);
            return this;
        }

        public SSLOptions build() {
            return new SSLOptions(trustStorePath, trustStorePass,
                                  keyStorePath, keyStorePass);
        }
    }
}
