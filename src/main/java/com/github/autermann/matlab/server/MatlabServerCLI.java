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

import java.io.IOException;
import java.io.PrintStream;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.github.autermann.matlab.MatlabException;
import com.github.autermann.utils.StandardSystemProperties;
import com.google.common.base.Strings;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <autermann@uni-muenster.de>
 */
public class MatlabServerCLI {
    private static final int DEFAULT_PORT = 7000;
    private static final int DEFAULT_THREADS = 5;
    private static final PrintStream ERR = System.err;

    public static void main(String[] args) throws IOException {
        MatlabServerCLIOptions options = new MatlabServerCLIOptions()
                .setPort(DEFAULT_PORT)
                .setThreads(DEFAULT_THREADS)
                .setPath(StandardSystemProperties.USER_DIR);
        JCommander cli = new JCommander(options);
        cli.setProgramName("java " + MatlabServerCLI.class.getName());
        MatlabServerConfiguration config = null;
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
            try {
                start(config);
            } catch (Exception ex) {
                printAndExit(ex);
            }
        }
    }

    private static void start(MatlabServerConfiguration options) throws Exception {
        try {
            new MatlabServer(options).start();
        } catch (IOException ex) {
            ERR.printf("Couldn't listen on port %d.\n", options
                    .getPort());
            if (options.isDebug()) {
                ex.printStackTrace(ERR);
            }
            System.exit(1);
        } catch (MatlabException ex) {
            ERR.println("Couldn't setup MatLab instance pool.");
            if (options.isDebug()) {
                ex.printStackTrace(ERR);
            }
            System.exit(1);
        }
    }

    private static MatlabServerConfiguration createConfig(
            MatlabServerCLIOptions options)
            throws ParameterException {
        return new MatlabServerConfiguration()
                .setPath(options.getPath())
                .setPort(options.getPort())
                .setThreads(options.getThreads())
                .setDebug(options.isDebug())
                .setHidden(!options.isShowInstances());
    }

    private static <T> T printAndExit(Throwable e) {
        ERR.println(e.getMessage());
        System.exit(1);
        return null;
    }

    private static class MatlabServerCLIOptions {
        @Parameter(names = { "-p", "--port" },
                   description = "The port to listen on.")
        private int port;
        @Parameter(names = { "-t", "--threads" },
                   description = "The amount of server threads.")
        private int threads;
        @Parameter(names = { "-b", "--base-dir" },
                   description = "The base directory.")
        private String path;
        @Parameter(names = { "--debug" },
                   description = "Show debug output.")
        private Boolean debug = false;
        @Parameter(names = { "-h", "--help" }, help = true,
                   description = "Display this help message.")
        private Boolean help;
        @Parameter(names = { "--no-hidden" },
                   description = "Don't start hidden Matlab instances.")
        private boolean showInstances = false;
        public int getPort() {
            return port;
        }

        public int getThreads() {
            return threads;
        }

        public boolean isDebug() {
            return debug != null && debug;
        }

        public boolean isHelp() {
            return help != null && help;
        }

        public String getPath() {
            return Strings.emptyToNull(path);
        }

        public MatlabServerCLIOptions setPort(int port) {
            checkArgument(port > 0);
            this.port = port;
            return this;
        }

        public MatlabServerCLIOptions setThreads(int threads) {
            checkArgument(threads > 0);
            this.threads = threads;
            return this;
        }

        public MatlabServerCLIOptions setPath(String path) {
            checkArgument(path != null && !path.isEmpty());
            this.path = path;
            return this;
        }

        public MatlabServerCLIOptions setDebug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public MatlabServerCLIOptions setHelp(boolean help) {
            this.help = help;
            return this;
        }

        public boolean isShowInstances() {
            return showInstances;
        }

        public MatlabServerCLIOptions setShowInstances(boolean showInstances) {
            this.showInstances = showInstances;
            return this;
        }

    }
}
