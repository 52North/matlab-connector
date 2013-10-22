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
