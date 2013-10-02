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
