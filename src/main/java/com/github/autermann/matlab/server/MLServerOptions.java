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
package com.github.autermann.matlab.server;

import static com.google.common.base.Preconditions.checkArgument;

import com.github.autermann.matlab.socket.ssl.SSLConfiguration;
import com.google.common.base.Optional;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class MLServerOptions {
    private int port;
    private int threads;
    private String path;
    private SSLConfiguration sslConfiguration;
    private boolean debug;

    public int getPort() {
        return this.port;
    }

    public int getThreads() {
        return this.threads;
    }

    public String getPath() {
        return this.path;
    }

    public MLServerOptions setPort(int port) {
        checkArgument(port > 0);
        this.port = port;
        return this;
    }

    public MLServerOptions setThreads(int threads) {
        checkArgument(threads > 0);
        this.threads = threads;
        return this;
    }

    public MLServerOptions setPath(String path) {
        checkArgument(path != null && !path.isEmpty());
        this.path = path;
        return this;
    }

    public Optional<SSLConfiguration> getSSLConfiguration() {
        return Optional.fromNullable(this.sslConfiguration);
    }

    public MLServerOptions setSSLConfiguration(SSLConfiguration c) {
        this.sslConfiguration = sslConfiguration;
        return this;
    }

    public boolean isDebug() {
        return debug;
    }

    public MLServerOptions setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

}
