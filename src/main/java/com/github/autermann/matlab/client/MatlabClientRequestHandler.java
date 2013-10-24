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
import java.io.InputStream;
import java.io.OutputStream;

import com.github.autermann.matlab.MatlabRequest;
import com.github.autermann.matlab.MatlabResponse;
import com.github.autermann.matlab.json.MatlabGSON;
import com.github.autermann.sockets.client.RequestSocketClientHandler;
import com.google.common.io.InputSupplier;
import com.google.common.io.OutputSupplier;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
class MatlabClientRequestHandler implements
        RequestSocketClientHandler<MatlabRequest, MatlabResponse> {

    private final MatlabGSON delegate = new MatlabGSON();

    @Override
    public void encode(MatlabRequest request,
                       OutputSupplier<OutputStream> out)
            throws IOException {
        delegate.encodeRequest(request, out.getOutput());
    }

    @Override
    public MatlabResponse decode(InputSupplier<InputStream> in)
            throws IOException {
        return delegate.decodeResponse(in.getInput());
    }

}
