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
package com.github.autermann.matlab.client;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.EncodeException;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.eclipse.jetty.websocket.jsr356.ClientContainer;

import com.github.autermann.matlab.MatlabException;
import com.github.autermann.matlab.MatlabRequest;
import com.github.autermann.matlab.MatlabResponse;
import com.github.autermann.matlab.MatlabResult;
import com.github.autermann.matlab.websocket.MatlabRequestDecoder;
import com.github.autermann.matlab.websocket.MatlabRequestEncoder;
import com.github.autermann.matlab.websocket.MatlabResponseDecoder;
import com.github.autermann.matlab.websocket.MatlabResponseEncoder;
import com.google.common.base.Preconditions;
import com.google.common.collect.Queues;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <autermann@uni-muenster.de>
 */
public class MatlabClient /*implements Runnable*/ {
    private final MatlabClientConfiguration options;
//    private final Thread thread = new Thread(this);
    private MatlabClientEndpoint endpoint;
    private WebSocketContainer container;
    private Session session;

    public MatlabClient(MatlabClientConfiguration options) throws
            DeploymentException, IOException {
        this.options = options;
        endpoint = new MatlabClientEndpoint();
        container = ContainerProvider.getWebSocketContainer();
        session = container.connectToServer(endpoint, options.getAddress());
    }

    public synchronized  MatlabResult exec(MatlabRequest request)
            throws IOException, MatlabException {
        Preconditions.checkState(session != null &&
                                 session.isOpen());
        try {
            session.getBasicRemote().sendObject(request);
        } catch (EncodeException ex) {
            throw new RuntimeException(ex);
        }
        MatlabResponse response = endpoint.getResponse();
        if (response instanceof MatlabException) {
            throw (MatlabException) response;
        }
        return (MatlabResult) response;
    }

    public MatlabClient start() throws DeploymentException, IOException {

        return this;
    }

    public void close() {
        try {
            session.close();
            ((ClientContainer) container).stop();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @ClientEndpoint(decoders = { MatlabRequestDecoder.class,
                                 MatlabResponseDecoder.class },
                    encoders = { MatlabRequestEncoder.class,
                                 MatlabResponseEncoder.class })
    public class MatlabClientEndpoint {
        private final BlockingQueue<MatlabResponse> responses
                = Queues.newLinkedBlockingQueue();

        @OnMessage
        public void onMessage(MatlabResponse response) {
            this.responses.add(response);
        }

        @OnError
        public void onError(Throwable thr) {
            if (thr instanceof MatlabException) {
                this.responses.add((MatlabException) thr);
            } else {
                this.responses
                        .add(new MatlabException("Could not execute request", thr));
            }
        }

        public MatlabResponse getResponse() {
            try {
                return responses.take();
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
