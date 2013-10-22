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

import com.beust.jcommander.Parameter;
import com.google.common.base.Optional;
import com.google.common.base.Strings;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class MLServerCLIOptions {
    @Parameter(names = { "-p", "--port" },
               description = "The port to listen on.")
    private int port;
    @Parameter(names = { "-t", "--threads" },
               description = "The amount of server threads.")
    private int threads;
    @Parameter(names = { "-b", "--base-dir" },
               description = "The base directory.")
    private String path;
    @Parameter(names = { "--keystore-config" },
               description = "Path to the SSL config file.")
    private String sslConfigPath;
    @Parameter(names = { "--trust-file" },
               description
            = "Path to a PEM file containing all trusted (client) certificates.")
    private String trustFile;
    @Parameter(names = { "--key-file" },
               description = "Path to SSL server key in PEM format.")
    private String keyFile;
    @Parameter(names = { "--cert-file" },
               description
            = "Path to SSL server certificate (incl. chain) in PEM format.")
    private String certFile;
    @Parameter(names = { "--clientauth" },
               description
            = "Path to a PEM file containing all trusted (client) certificates.")
    private Boolean clientAuth;
    @Parameter(names = { "--debug" },
               description = "Show debug output.")
    private Boolean debug = false;
    @Parameter(names = { "-h", "--help" }, help = true,
               description = "Display this help message.")
    private Boolean help;

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

    public Optional<String> getSSLConfigPath() {
        return Optional.fromNullable(Strings.emptyToNull(sslConfigPath));
    }

    public Optional<String> getTrustFile() {
        return Optional.fromNullable(Strings.emptyToNull(trustFile));
    }

    public Optional<String> getKeyFile() {
        return Optional.fromNullable(Strings.emptyToNull(keyFile));
    }

    public Optional<String> getCertFile() {
        return Optional.fromNullable(Strings.emptyToNull(certFile));
    }

    public Optional<Boolean> isClientAuth() {
        return Optional.fromNullable(this.clientAuth);
    }

    public MLServerCLIOptions setPort(int port) {
        checkArgument(port > 0);
        this.port = port;
        return this;
    }

    public MLServerCLIOptions setThreads(int threads) {
        checkArgument(threads > 0);
        this.threads = threads;
        return this;
    }

    public MLServerCLIOptions setPath(String path) {
        checkArgument(path != null && !path.isEmpty());
        this.path = path;
        return this;
    }

    public MLServerCLIOptions setSslConfigPath(String sslConfigPath) {
        checkArgument(sslConfigPath != null && !sslConfigPath.isEmpty());
        this.sslConfigPath = sslConfigPath;
        return this;
    }

    public MLServerCLIOptions setTrustFile(String trustFile) {
        checkArgument(trustFile != null && !trustFile.isEmpty());
        this.trustFile = trustFile;
        return this;
    }

    public MLServerCLIOptions setKeyFile(String keyFile) {
        checkArgument(keyFile != null && !keyFile.isEmpty());
        this.keyFile = keyFile;
        return this;
    }

    public MLServerCLIOptions setCertFile(String certFile) {
        checkArgument(certFile != null && !certFile.isEmpty());
        this.certFile = certFile;
        return this;
    }

    public MLServerCLIOptions setClientAuth(boolean clientAuth) {
        this.clientAuth = clientAuth;
        return this;
    }

    public MLServerCLIOptions setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public MLServerCLIOptions setHelp(boolean help) {
        this.help = help;
        return this;
    }

}
