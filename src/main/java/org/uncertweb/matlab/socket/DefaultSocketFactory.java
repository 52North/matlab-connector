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
package org.uncertweb.matlab.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class DefaultSocketFactory implements SocketFactory, ServerSocketFactory {
    @Override
    public ServerSocket createServerSocket(int port)
            throws IOException, SocketException {
        return new ServerSocket(port);
    }

    @Override
    public Socket createSocket(InetSocketAddress address, int timeout)
            throws IOException, SocketException {
        Socket socket = new Socket();
        socket.connect(address, timeout);
        return socket;
    }

    @Override
    public Socket createSocket(String host, int port, int timeout)
            throws IOException, SocketException {
        return createSocket(new InetSocketAddress(host, port), timeout);
    }
}
