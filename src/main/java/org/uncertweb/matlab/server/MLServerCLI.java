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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class MLServerCLI {
    public static final int DEFAULT_PORT = 7000;
    public static final int DEFAULT_THREADS = 5;
    public static final String DEFAULT_BASE_DIR = System.getProperty("user.dir");

    public static void main(String[] args) {
        final MLServerOptions options = new MLServerOptions(
                DEFAULT_PORT, DEFAULT_THREADS, DEFAULT_BASE_DIR);
        final JCommander cli = new JCommander(options);
        cli.setProgramName("java " + MLServer.class.getName());
        try {
            cli.parse(args);
        } catch (ParameterException e) {
            cli.usage();
            System.err.println(e.getMessage());
            System.exit(1);
        }
        if (options.isHelp()) {
            cli.usage();
        } else {
            try {
                new MLServer(options).start();
            } catch (IOException ex) {
                System.err.printf("Couldn't listen on port %d.\n", options.getPort());
                if (options.isDebug()) {
                    ex.printStackTrace(System.err);
                }
                System.exit(1);
            } catch (MLConnectorException ex) {
                System.err.println("Couldn't setup MatLab instance pool.");
                if (options.isDebug()) {
                    ex.printStackTrace(System.err);
                }
                System.exit(1);
            }
        }
    }
}
