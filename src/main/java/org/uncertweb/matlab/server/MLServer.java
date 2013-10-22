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

import org.uncertweb.matlab.socket.DefaultSocketFactory;
import org.uncertweb.matlab.socket.ServerSocketFactory;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uncertweb.matlab.socket.ssl.SSLSocketCreationException;
import org.uncertweb.matlab.socket.ssl.SSLSocketFactory;
import org.uncertweb.matlab.util.NamedAndGroupedThreadFactory;

public class MLServer {
    private static final Logger log = LoggerFactory.getLogger(MLServer.class);
    private MLInstancePool instancePool;
    private final ExecutorService threadPool = Executors
            .newCachedThreadPool(NamedAndGroupedThreadFactory.builder()
            .name("MLServer").build());
    private ServerSocket serverSocket;
    private final MLServerOptions options;

    public MLServer(MLServerOptions options) {
        this.options = options;
    }

    public MLServerOptions getOptions() {
        return this.options;
    }

    public MLInstancePool getPool() {
        return instancePool;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void start() throws IOException, MLConnectorException, SSLSocketCreationException {
        setup();
        while (true) {
            awaitConnection();
        }
    }

    private void setup() throws IOException, MLConnectorException, SSLSocketCreationException {
        synchronized (this) {
            checkState(getPool() == null, "Server already started.");
        }
        // create out matlab instance instancePool
        final MLInstanceConfig instanceConfig = MLInstanceConfig.builder()
                .withBaseDir(getOptions().getPath())
                .build();
        final MLInstancePoolConfig poolConfig = MLInstancePoolConfig
                .builder()
                .withMaximalNumInstances(getOptions().getThreads())
                .withInstanceConfig(instanceConfig)
                .build();
        instancePool = new MLInstancePool(poolConfig);
        this.serverSocket = getServerSocketFactory()
                .createServerSocket(getOptions().getPort());
        log.info("Listening on port {}...", getOptions().getPort());
                        Runtime.getRuntime()
                        .addShutdownHook(new MLServerShutdownHook(this));

    }

    private void awaitConnection() {
        try {
            final Socket socket = getServerSocket().accept();
            log.info("Client {} connected.", socket.getRemoteSocketAddress());
            threadPool.execute(new MLServerTask(socket, getPool()));
        } catch (IOException e) {
            // this exception will be thrown a few times during shutdown, hence the check here
            if (!getServerSocket().isClosed()) {
                log.error("Could not accept client connection: {}",
                          e.getMessage());
            }
        }
    }

    private ServerSocketFactory getServerSocketFactory()
            throws IOException, SSLSocketCreationException {
        if (getOptions().getSSLConfiguration().isPresent()) {
            return new SSLSocketFactory(getOptions().getSSLConfiguration().get());
        } else {
            return new DefaultSocketFactory();
        }
    }
}
