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

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.io.Closeables;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class KeyStoreOptions {
    private final String pass;
    private final String type;
    private final String path;

    public KeyStoreOptions(String path, String pass, String type) {
        this.path = Preconditions.checkNotNull(path);
        this.pass = Preconditions.checkNotNull(pass);
        this.type = Objects.firstNonNull(Strings.emptyToNull(type), 
                                         SSLConstants.KEYSTORE_TYPE_JKS);
    }

    public String getPass() {
        return pass;
    }

    public String getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public KeyStore read() throws IOException,
                                  KeyStoreException,
                                  NoSuchAlgorithmException,
                                  CertificateException {
        KeyStore ks = KeyStore.getInstance(getType());
        FileInputStream in = null;
        try {
            in = new FileInputStream(getPath());
            ks.load(in, getPass().toCharArray());
            return ks;
        } finally {
            Closeables.close(in, true);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getPass(), getType(), getPath());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof KeyStoreOptions) {
            KeyStoreOptions other = (KeyStoreOptions) obj;
            return Objects.equal(getPass(), other.getPass()) &&
                   Objects.equal(getPath(), other.getPath()) &&
                   Objects.equal(getType(), other.getType());
        }
        return false;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).omitNullValues()
                .add("pass", getPass())
                .add("path", getPath())
                .add("type", getType())
                .toString();
    }
}
