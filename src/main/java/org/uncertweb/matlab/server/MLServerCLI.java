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
import java.io.PrintStream;

import org.uncertweb.matlab.socket.ssl.KeyStoreSSLConfiguration;
import org.uncertweb.matlab.socket.ssl.PemFileSSLConfiguration;
import org.uncertweb.matlab.socket.ssl.SSLConfiguration;
import org.uncertweb.matlab.util.Optionals;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.google.common.base.Optional;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class MLServerCLI {
    private static final int DEFAULT_PORT = 7000;
    private static final int DEFAULT_THREADS = 5;
    private static final String DEFAULT_BASE_DIR = System.getProperty("user.dir");
    private static final PrintStream ERR = System.err;

    public static void main(String[] args) throws IOException {
        MLServerCLIOptions options = new MLServerCLIOptions()
                .setPort(DEFAULT_PORT)
                .setThreads(DEFAULT_THREADS)
                .setPath(DEFAULT_BASE_DIR);
        JCommander cli = new JCommander(options);
        cli.setProgramName("java " + MLServerCLI.class.getName());
        MLServerOptions config = null;
        try {
            cli.parse(args);
            config = createConfig(options);
        } catch (ParameterException e) {
            cli.usage();
            printAndExit(e);
        }
        if (options.isHelp()) {
            cli.usage();
        } else {
            start(config);
        }
    }

    private static void start(MLServerOptions options) {
        try {
            new MLServer(options).start();
        } catch (IOException ex) {
            ERR.printf("Couldn't listen on port %d.\n", options
                    .getPort());
            if (options.isDebug()) {
                ex.printStackTrace(ERR);
            }
            System.exit(1);
        } catch (MLConnectorException ex) {
            ERR.println("Couldn't setup MatLab instance pool.");
            if (options.isDebug()) {
                ex.printStackTrace(ERR);
            }
            System.exit(1);
        }
    }


    private static MLServerOptions createConfig(MLServerCLIOptions options)
            throws ParameterException {
        SSLConfiguration sslConfig;
        try {
            sslConfig = createSSLConfiguration(options);
        } catch(IOException e){
            return printAndExit(e);
        }
        return new MLServerOptions()
                .setPath(options.getPath())
                .setPort(options.getPort())
                .setThreads(options.getThreads())
                .setDebug(options.isDebug())
                .setSSLConfiguration(sslConfig);
    }

    private static <T> T printAndExit(Throwable e) {
        ERR.println(e.getMessage());
        System.exit(1);
        return null;
    }

    @SuppressWarnings("unchecked")
    private static SSLConfiguration createSSLConfiguration(
            MLServerCLIOptions options) throws IOException {
        if (options.getSSLConfigPath().isPresent()) {
            return KeyStoreSSLConfiguration
                    .load(options.getSSLConfigPath().get());
        } else {
            Optional<String> cf = options.getCertFile();
            Optional<String> kf = options.getKeyFile();
            Optional<String> tf = options.getTrustFile();
            Optional<Boolean> ca = options.isClientAuth();
            if (Optionals.any(cf, kf, tf, ca)) {
                if (Optionals.all(cf, kf, tf)) {
                    return new PemFileSSLConfiguration(
                            kf.get(), cf.get(), tf.get(), ca.or(false));
                } else {
                    throw new ParameterException("Not all required SSL options were present!");
                }
            } else {
                return null;
            }
        }
    }
    
}
