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
package org.uncertweb.matlab.server;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class MLServerShutdownHook extends Thread {
    private static final Logger log = LoggerFactory.getLogger(MLServer.class);
    private final MLServer server;

    public MLServerShutdownHook(MLServer server) {
        this.server = server;
    }

    public MLServer getServer() {
        return server;
    }

    @Override
    public void run() {
        closeSocket();
        closePool();
    }

    private void closeSocket() {
        log.info("Closing server socket...");
        try {
            getServer().getServerSocket().close();
        } catch (IOException e) {
            log.warn("Couldn't close server socket on port {}.",
                     getServer().getOptions().getPort());
        }
    }

    private void closePool() {
        log.info("Destroying MATLAB instance pool...");
        getServer().getPool().destroy();
        log.info("Shutdown complete.");
    }
}
