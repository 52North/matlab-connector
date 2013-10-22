/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.uncertweb.matlab.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public interface SocketFactory {
    Socket createSocket(InetSocketAddress address, int timeout)
            throws IOException, SocketException;

    Socket createSocket(String host, int port, int timeout)
            throws IOException, SocketException;
}
