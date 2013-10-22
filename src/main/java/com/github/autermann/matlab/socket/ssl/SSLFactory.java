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
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

import com.google.common.base.Preconditions;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class SSLFactory {
    private final SSLConfiguration options;
    private X509TrustManager trustManager;
    private X509KeyManager keyManager;
    private SSLContext context;

    public SSLFactory(SSLConfiguration options) {
        this.options = Preconditions.checkNotNull(options);
    }

    public SSLServerSocket createServerSocket(int port)
            throws GeneralSecurityException, IOException {
        SSLServerSocket socket = (SSLServerSocket) getContext()
                .getServerSocketFactory().createServerSocket(port);
        if (getOptions().isRequireClientAuth()) {
            socket.setNeedClientAuth(true);
        }
        socket.setEnabledProtocols(new String[] {
            SSLConstants.PROTOCOL_TLS_V1 });
        return socket;
    }

    public SSLSocket createSocket(InetSocketAddress address, int timeout)
            throws GeneralSecurityException, IOException {
        SSLSocket socket = (SSLSocket) getContext()
                    .getSocketFactory().createSocket();
        socket.connect(address, timeout);
        return socket;
    }

    protected SSLConfiguration getOptions() {
        return this.options;
    }

    public X509TrustManager getTrustManager()
            throws GeneralSecurityException, IOException {
        if (this.trustManager == null) {
            this.trustManager = createTrustManager();
        }
        return this.trustManager;
    }

    public X509KeyManager getKeyManager()
            throws GeneralSecurityException, IOException {
        if (this.keyManager == null) {
            this.keyManager = createKeyManager();
        }
        return this.keyManager;
    }

    public SSLContext getContext()
            throws GeneralSecurityException, IOException {
        if (this.context == null) {
            this.context = createSSLContext();
        }
        return this.context;
    }

    private X509KeyManager createKeyManager()
            throws GeneralSecurityException, IOException {
        KeyManagerFactory keyManagerFactory = KeyManagerFactory
                .getInstance(SSLConstants.KEY_MANAGER_ALGORITHM_SUN_X509,
                             SSLConstants.KEY_MANAGER_PROVIDER_SUN_JSSE);
        keyManagerFactory.init(getOptions().getKeyStore(),
                               getOptions().getKeyStorePass());
        for (KeyManager km : keyManagerFactory.getKeyManagers()) {
            if (km instanceof X509KeyManager) {
                return (X509KeyManager) km;
            }
        }
        throw new NoSuchAlgorithmException("No X509KeyManager in KeyManagerFactory");
    }

    private X509TrustManager createTrustManager()
            throws GeneralSecurityException, IOException {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory
                .getInstance(SSLConstants.TRUST_MANAGER_ALGORITHM_PKIX);
        trustManagerFactory.init(getOptions().getTrustStore());
        for (TrustManager tm : trustManagerFactory.getTrustManagers()) {
            if (tm instanceof X509TrustManager) {
                return (X509TrustManager) tm;
            }
        }
        throw new NoSuchAlgorithmException("No X509TrustManager in TrustManagerFactory");
    }

    private SSLContext createSSLContext()
            throws GeneralSecurityException, IOException {
        SSLContext sslContext = SSLContext
                .getInstance(SSLConstants.PROTOCOL_TLS_V1);
        sslContext.init(new KeyManager[] { getKeyManager() },
                        new TrustManager[] { getTrustManager() }, null);
        return sslContext;
    }
}
