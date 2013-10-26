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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.autermann.matlab.MatlabEncoding;
import com.github.autermann.matlab.MatlabException;
import com.github.autermann.matlab.MatlabRequest;
import com.github.autermann.matlab.MatlabResponse;
import com.github.autermann.matlab.json.MatlabGSON;
import com.github.autermann.sockets.server.RequestSocketServerCoder;
import com.github.autermann.sockets.server.RequestSocketServerHandler;

/**
 * TODO JavaDoc
 *
 * @author Christian Autexceptionrmann <c.autexceptionrmann@52north.org>
 */
class MatlabServerResponseHandler implements
        RequestSocketServerHandler<MatlabRequest, MatlabResponse>,
        RequestSocketServerCoder<MatlabRequest, MatlabResponse> {
    private static final Logger log = LoggerFactory
            .getLogger(MatlabServerResponseHandler.class);
    private final MatlabInstancePool pool;
    private final MatlabEncoding delegate = new MatlabGSON();

    MatlabServerResponseHandler(MatlabInstancePool pool) {
        this.pool = pool;
    }

    @Override
    public MatlabResponse handle(MatlabRequest request) {
        log.info("Received request for function '{}'.", request.getFunction());
        MatlabInstance instance = null;
        MatlabResponse response;
        try {
            instance = this.pool.getInstance();
            response = instance.handle(request);
            log.info("Handled request successfully.");
        } catch (MatlabException exception) {
            log.error("Caught exception when calling Matlab.", exception);
            response = exception;
        } finally {
            this.pool.returnInstance(instance);
        }
        return response;
    }

    @Override
    public MatlabRequest decode(InputStream in) throws IOException {
        return this.delegate.decodeRequest(in);
    }

    @Override
    public void encode(MatlabResponse response, OutputStream out)
            throws IOException {
        this.delegate.encodeResponse(response, out);
    }

}
