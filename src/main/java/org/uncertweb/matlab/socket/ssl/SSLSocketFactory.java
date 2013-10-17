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

    public SSLSocketFactory(SSLConfiguration options) {
        this.delegate = new SSLFactory(Preconditions.checkNotNull(options));
    }

    @Override
    public SSLServerSocket createServerSocket(int port)
            throws IOException, SocketException {
        try {
            return delegate.createServerSocket(port);
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
