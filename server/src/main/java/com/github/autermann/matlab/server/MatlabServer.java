/*
 * Copyright (C) 2012-2015 by it's authors.
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

import javax.servlet.ServletException;
import javax.websocket.DeploymentException;
import javax.websocket.server.ServerEndpointConfig;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.component.AbstractLifeCycle.AbstractLifeCycleListener;
import org.eclipse.jetty.util.component.LifeCycle;
import org.eclipse.jetty.websocket.jsr356.server.ServerContainer;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MatlabServer {
    private static final Logger log = LoggerFactory
            .getLogger(MatlabServer.class);
    private final MatlabServerConfiguration options;
    private Server server;

    public MatlabServer(MatlabServerConfiguration options) {
        this.options = options;
    }

    public MatlabServerConfiguration getOptions() {
        return this.options;
    }

    public void start() throws Exception {
        synchronized (this) {
            if (this.server == null) {
                this.server = setup();
            }
        }
        this.server.start();
    }

    public void stop() throws Exception {
        synchronized (this) {
            if (this.server == null) {
                return;
            }
        }
        this.server.stop();
    }

    private Server setup() throws IOException, DeploymentException, ServletException {
        synchronized (this) {
            checkState(server == null, "Server already started.");
        }
        MatlabInstancePool pool
                = new MatlabInstancePool(MatlabInstancePoolConfiguration
                        .builder()
                        .withMaximalNumInstances(getOptions().getThreads())
                        .withInstanceConfig(MatlabInstanceConfiguration
                                .builder()
                                .withBaseDir(getOptions().getPath())
                                .hidden(getOptions().isHidden())
                                .build())
                        .build());
        Server jetty = new Server(getOptions().getPort());
        ServletContextHandler handler
                = new ServletContextHandler(ServletContextHandler.SESSIONS);
        handler.setContextPath("/");
        jetty.setHandler(handler);
        handler.addLifeCycleListener(new MatlabInstancePoolDestroyer(pool));
        ServerContainer sc = WebSocketServerContainerInitializer.configureContext(handler);
        sc.addEndpoint(ServerEndpointConfig.Builder.create(MatlabServerEndpoint.class, "/")
                .configurator(new MatlabServerEndpointConfigurator(pool)).build());
        return jetty;
    }

    private class MatlabInstancePoolDestroyer extends AbstractLifeCycleListener {
        private final MatlabInstancePool pool;

        MatlabInstancePoolDestroyer(MatlabInstancePool pool) {
            this.pool = pool;
        }

        @Override
        public void lifeCycleStopped(LifeCycle event) {
            log.info("Destroying Matlab instance pool...");
            pool.destroy();
            log.info("Destroyed Matlab instance pool...");
        }
    }
}
