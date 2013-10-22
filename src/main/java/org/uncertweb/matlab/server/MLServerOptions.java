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

import static com.google.common.base.Preconditions.checkArgument;

import org.uncertweb.matlab.socket.ssl.SSLConfiguration;

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
