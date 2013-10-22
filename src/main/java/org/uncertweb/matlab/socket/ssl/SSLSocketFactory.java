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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.security.GeneralSecurityException;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;

import org.uncertweb.matlab.socket.ServerSocketFactory;
import org.uncertweb.matlab.socket.SocketFactory;

import com.google.common.base.Preconditions;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class SSLSocketFactory implements SocketFactory, ServerSocketFactory {
    private final SSLFactory delegate;
    private final SSLConfiguration options;

    public SSLSocketFactory(SSLConfiguration options) {
        this.options = Preconditions.checkNotNull(options);
        this.delegate = new SSLFactory(options);
    }

    @Override
    public SSLServerSocket createServerSocket(int port)
            throws IOException, SocketException {
        try {
            SSLServerSocket socket = delegate.createServerSocket(port);
            return socket;
        } catch (GeneralSecurityException ex) {
            throw new SSLSocketCreationException(ex);
        }
    }

    @Override
    public SSLSocket createSocket(InetSocketAddress address, int timeout)
            throws IOException, SocketException {
        try {
            return delegate.createSocket(address, timeout);
        } catch (GeneralSecurityException ex) {
            throw new SSLSocketCreationException(ex);
        }
    }

    @Override
    public SSLSocket createSocket(String host, int port, int timeout)
            throws IOException, SocketException {
        return createSocket(new InetSocketAddress(host, port), timeout);
    }
}
