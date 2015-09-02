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
package org.n52.matlab.connector.client;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.eclipse.jetty.websocket.jsr356.ClientContainer;

import org.n52.matlab.connector.MatlabException;
import org.n52.matlab.connector.MatlabRequest;
import org.n52.matlab.connector.MatlabResponse;
import org.n52.matlab.connector.MatlabResult;
import org.n52.matlab.connector.server.MatlabServerEndpoint;

import org.n52.matlab.connector.websocket.MatlabRequestDecoder;
import org.n52.matlab.connector.websocket.MatlabRequestEncoder;
import org.n52.matlab.connector.websocket.MatlabResponseDecoder;
import org.n52.matlab.connector.websocket.MatlabResponseEncoder;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.SettableFuture;

/**
 * TODO JavaDoc
 * @author Christian Autermann
 */
public class RemoteMatlabClient extends MatlabClient {

    private final MatlabClientEndpoint endpoint;
    private final WebSocketContainer container;
    private final Session session;
    private final Map<Long,SettableFuture<MatlabResult>> responses;

    public RemoteMatlabClient(RemoteMatlabClientConfiguration options)
            throws MatlabException, IOException {
        try {
            this.responses = Collections.synchronizedMap(new HashMap<>());
            this.endpoint = new MatlabClientEndpoint();
            this.container = ContainerProvider.getWebSocketContainer();
            this.session = this.container.connectToServer(endpoint, options.getAddress());
            this.session.setMaxTextMessageBufferSize(MatlabServerEndpoint.MAX_MESSAGE_SIZE);
        } catch (DeploymentException ex) {
            throw new MatlabException("Error connecting to server", ex);
        }
    }

    @Override
    public Future<MatlabResult> exec(MatlabRequest request)
            throws MatlabException, IOException {
        Preconditions.checkState(session != null && session.isOpen());
        try {
            SettableFuture<MatlabResult> future = SettableFuture.create();
            SettableFuture<MatlabResult> oldFuture
                    = this.responses.putIfAbsent(request.getId(), future);

            if (oldFuture != null) {
                // we already executed that request...
                return oldFuture;
            }
            session.getBasicRemote().sendObject(request);
            return future;

        } catch (EncodeException ex) {
            throw new RuntimeException(ex);
        }
    }

    public MatlabResult execSync(MatlabRequest request)
            throws MatlabException, IOException {
        try {
            return this.exec(request).get();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof MatlabException) {
                throw (MatlabException) cause;
            } else if (cause instanceof IOException) {
                throw (IOException) cause;
            } else {
                throw new MatlabException("error executing request", cause);
            }
        } catch (InterruptedException ex) {
            throw new MatlabException("error executing request", ex);
        }
    }

    @Override
    public void close() {
        try {
            session.close();
            if (container instanceof ClientContainer) {
                ((ClientContainer) container).stop();
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @ClientEndpoint(decoders = { MatlabRequestDecoder.class,
                                 MatlabResponseDecoder.class },
                    encoders = { MatlabRequestEncoder.class,
                                 MatlabResponseEncoder.class })
    public class MatlabClientEndpoint {
        @OnMessage
        public void onMessage(MatlabResponse response) {
            SettableFuture<MatlabResult> future = responses.computeIfAbsent(response.getId(), id -> SettableFuture.create());
            synchronized (responses) {
                if (response instanceof MatlabResult) {
                    future.set((MatlabResult) response);
                } else if (response instanceof MatlabException) {
                    future.setException((MatlabException) response);
                } else {
                    future.setException(new MatlabException("unknown response"));
                }
            }

        }

        @OnError
        public void onError(Throwable thr) {
            synchronized (responses) {
                MatlabException ex = asMatlabException(thr);
                // we can't say which request caused this...
                responses.values().stream().filter(this::notDone)
                        .forEach(f -> f.setException(ex));
            }
        }

        private <T> boolean notDone(SettableFuture<T> future) {
            return !future.isDone();
        }

        private MatlabException asMatlabException(Throwable thr) {
            MatlabException ex;
            if (thr instanceof MatlabException) {
                ex = (MatlabException) thr;
            } else {
                ex = new MatlabException("Could not execute request", thr);
            }
            return ex;
        }

    }

}
