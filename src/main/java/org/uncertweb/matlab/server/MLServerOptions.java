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

import com.beust.jcommander.Parameter;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class MLServerOptions {
    @Parameter(names = { "-p", "--port" },
               description = "The port to listen on.")
    private int port;
    @Parameter(names = { "-t", "--threads" },
               description = "The amount of server threads.")
    private int threads;
    @Parameter(names = { "-b", "--base-dir" },
               description = "The base directory.")
    private String path;
    @Parameter(names = { "-h", "--help" }, help = true,
               description = "Display this help message.")
    private Boolean help;

    public MLServerOptions(int port, int threads, String path) {
        this.port = port;
        this.threads = threads;
        this.path = path;
    }

    public int getPort() {
        return port;
    }

    public int getThreads() {
        return threads;
    }

    public String getPath() {
        return path;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isHelp() {
        return help;
    }
}
