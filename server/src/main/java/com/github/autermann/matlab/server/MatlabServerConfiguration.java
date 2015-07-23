/*
 * Copyright (C) 2012-2015 by it's authors.
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


/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class MatlabServerConfiguration {
    private int port;
    private int threads;
    private String path;
    private boolean debug;
    private boolean hidden;

    public int getPort() {
        return this.port;
    }

    public int getThreads() {
        return this.threads;
    }

    public String getPath() {
        return this.path;
    }

    public MatlabServerConfiguration setPort(int port) {
        checkArgument(port > 0);
        this.port = port;
        return this;
    }

    public MatlabServerConfiguration setThreads(int threads) {
        checkArgument(threads > 0);
        this.threads = threads;
        return this;
    }

    public MatlabServerConfiguration setPath(String path) {
        checkArgument(path != null && !path.isEmpty());
        this.path = path;
        return this;
    }

    public boolean isDebug() {
        return debug;
    }

    public MatlabServerConfiguration setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public boolean isHidden() {
        return hidden;
    }

    public MatlabServerConfiguration setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

}
