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
package com.github.autermann.matlab.client;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.net.InetSocketAddress;

import com.github.autermann.sockets.ClientSocketFactory;
import com.github.autermann.sockets.ssl.SSLClientSocketFactory;
import com.github.autermann.sockets.ssl.SSLConfiguration;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class MatlabClientConfiguration {
    private final ClientSocketFactory socketFactory;
    private final InetSocketAddress address;
    private final int timeOut;
    private final int attempts;

    private MatlabClientConfiguration(ClientSocketFactory socketFactory,
                                      InetSocketAddress address, int timeOut,
                                      int attempts) {
        this.socketFactory = socketFactory;
        this.address = address;
        this.timeOut = timeOut;
        this.attempts = attempts;
    }

    public ClientSocketFactory getSocketFactory() {
        return socketFactory;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public int getAttempts() {
        return attempts;
    }

    private static class Builder {
        private static final int DEFAULT_TIMEOUT = 10 * 1000;
        public static final int DEFAULT_ATTEMPTS = 3;
        private InetSocketAddress address;
        private ClientSocketFactory socketFactory;
        private int timeout = DEFAULT_TIMEOUT;
        private int attempts = DEFAULT_ATTEMPTS;

        public Builder withAddress(InetSocketAddress address) {
            this.address = checkNotNull(address);
            return this;
        }

        public Builder withAddress(String host, int port) {
            checkNotNull(host);
            checkArgument(port > 0);
            return withAddress(new InetSocketAddress(host, port));
        }

        public Builder withSocketFactory(ClientSocketFactory socketFactory) {
            this.socketFactory = checkNotNull(socketFactory);
            return this;
        }

        public Builder withSSL(SSLConfiguration config) {
            checkNotNull(config);
            return withSocketFactory(new SSLClientSocketFactory(config));
        }

        public Builder withTimeout(int timeout) {
            checkArgument(timeout > 0);
            this.timeout = timeout;
            return this;
        }

        public Builder withAttempts(int attempts) {
            checkArgument(attempts > 0);
            this.attempts = attempts;
            return this;
        }

        public MatlabClientConfiguration build() {
            checkState(address != null);
            if (socketFactory == null) {
                socketFactory = ClientSocketFactory.getDefault();
            }
            return new MatlabClientConfiguration(socketFactory, address,
                                                 timeout, attempts);
        }
    }
}
