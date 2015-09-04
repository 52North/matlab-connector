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
package org.n52.matlab.connector.server;

import java.io.IOException;

import javax.websocket.CloseReason;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.n52.matlab.connector.MatlabException;
import org.n52.matlab.connector.MatlabRequest;
import org.n52.matlab.connector.MatlabResponse;
import org.n52.matlab.connector.instance.MatlabInstance;
import org.n52.matlab.connector.instance.MatlabInstancePool;
import org.n52.matlab.connector.websocket.Configuration;
import org.n52.matlab.connector.websocket.MatlabRequestDecoder;
import org.n52.matlab.connector.websocket.MatlabRequestEncoder;
import org.n52.matlab.connector.websocket.MatlabResponseDecoder;
import org.n52.matlab.connector.websocket.MatlabResponseEncoder;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
@ServerEndpoint(value = "/",
                decoders = { MatlabRequestDecoder.class,
                             MatlabResponseDecoder.class },
                encoders = { MatlabRequestEncoder.class,
                             MatlabResponseEncoder.class })
public class MatlabServerEndpoint {
    private final Logger log = LoggerFactory
            .getLogger(MatlabServerEndpoint.class);

    private final MatlabInstancePool pool;

    public MatlabServerEndpoint(MatlabInstancePool pool) {
        this.pool = pool;
    }

    @OnOpen
    public void onOpen(Session session) {
        log.info("Session {} opened.", session.getId());
        session.setMaxTextMessageBufferSize(Configuration.MAX_MESSAGE_SIZE);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        log.info("Session {} closed because of {}.", session.getId(), closeReason);
    }

    @OnMessage
    public MatlabResponse onMessage(Session session, MatlabRequest request)
            throws MatlabException {
        log.info("Session {} requests function '{}'.",
                 session.getId(), request.getFunction());
        MatlabInstance instance = null;
        try {
            instance = this.pool.getInstance();
            MatlabResponse response = instance.handle(request);
            log.info("Handled request for session {} successfully.",
                     session.getId());
            return response;
        } catch (MatlabException e) {
            e.setId(request.getId());
            throw e;
        } finally {
            this.pool.returnInstance(instance);
        }
    }

    @OnError
    public void onError(Session session, Throwable t)
            throws IOException, EncodeException {
        log.error("Caught exception while handling request for session " + session.getId(), t);
        if (t instanceof MatlabException) {
            session.getBasicRemote().sendObject(t);
        } else {
            session.getBasicRemote()
                    .sendObject(new MatlabException("Could not execute request", t));
        }
    }

}
