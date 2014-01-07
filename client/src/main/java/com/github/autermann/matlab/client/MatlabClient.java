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

import com.github.autermann.matlab.MatlabException;
import com.github.autermann.matlab.MatlabRequest;
import com.github.autermann.matlab.MatlabResponse;
import com.github.autermann.matlab.MatlabResult;
import com.github.autermann.sockets.client.RequestSocketClient;
import com.github.autermann.sockets.client.SocketClientBuilder;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <autermann@uni-muenster.de>
 */
public class MatlabClient {
    private final RequestSocketClient<MatlabRequest, MatlabResponse> client;

    public MatlabClient(MatlabClientConfiguration options) {
        client = SocketClientBuilder.create()
                .withAddress(options.getAddress())
                .withSocketFactory(options.getSocketFactory())
                .withTimeout(options.getTimeOut())
                .build(new MatlabClientRequestHandler());
    }

    public MatlabResult exec(MatlabRequest request) throws IOException,
                                                           MatlabException {
        MatlabResponse response = client.exec(request);
        if (response instanceof MatlabException) {
            throw (MatlabException) response;
        }
        return (MatlabResult) response;
    }

    public void close() {
        this.client.close();
    }
}
