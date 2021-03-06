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
package org.n52.matlab.connector.websocket;

import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import org.n52.matlab.connector.MatlabEncoding;

import org.n52.matlab.connector.json.MatlabJSONEncoding;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public abstract class AbstractEncoder<T> implements Encoder.Text<T> {

    private final MatlabEncoding delegate = new MatlabJSONEncoding();

    public MatlabEncoding getDelegate() {
        return delegate;
    }

    @Override
    public void init(EndpointConfig config) {
    }

    @Override
    public void destroy() {
    }

}
