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
