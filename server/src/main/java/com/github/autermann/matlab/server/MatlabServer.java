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

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;

import com.github.autermann.matlab.MatlabException;
import com.github.autermann.sockets.server.SocketServerBuilder;
import com.github.autermann.sockets.server.StreamingSocketServer;

public class MatlabServer {
    private StreamingSocketServer server;
    private final MatlabServerConfiguration options;

    public MatlabServer(MatlabServerConfiguration options) {
        this.options = options;
    }

    public MatlabServerConfiguration getOptions() {
        return this.options;
    }

    public void start() throws IOException, MatlabException {
        setup().start(true);
    }

    private StreamingSocketServer setup() throws IOException, MatlabException {
        synchronized (this) {
            checkState(server == null, "Server already started.");
        }
        final MatlabInstancePool pool = createInstancePool();
        final MatlabInstancePoolDestroyer destroyer = new MatlabInstancePoolDestroyer(pool);
        final MatlabServerResponseHandler handler = new MatlabServerResponseHandler(pool);
        return this.server = SocketServerBuilder.create()
                .withShutdownHook(destroyer)
                .atPort(getOptions().getPort())
                .withSSL(getOptions().getSSLConfiguration().orNull())
                .build(handler);
    }

    private MatlabInstancePool createInstancePool() throws
            MatlabException {
        // create out matlab instance instancePool
        final MatlabInstanceConfiguration instanceConfig
                = MatlabInstanceConfiguration.builder()
                .withBaseDir(getOptions().getPath())
                .build();
        final MatlabInstancePoolConfiguration poolConfig
                = MatlabInstancePoolConfiguration.builder()
                .withMaximalNumInstances(getOptions().getThreads())
                .withInstanceConfig(instanceConfig)
                .build();
        final MatlabInstancePool pool = new MatlabInstancePool(poolConfig);
        return pool;
    }
}
