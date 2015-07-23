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
package com.github.autermann.matlab.client;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.Future;

import com.github.autermann.matlab.MatlabException;
import com.github.autermann.matlab.MatlabRequest;
import com.github.autermann.matlab.MatlabResult;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public abstract class MatlabClient implements Closeable {

    public abstract Future<MatlabResult> exec(MatlabRequest request)
            throws MatlabException, IOException;

    public abstract MatlabResult execSync(MatlabRequest request)
            throws MatlabException, IOException;

    public static MatlabClient create() throws MatlabException, IOException {
        return create(MatlabClientConfiguration.builder().build());
    }

    public static MatlabClient create(URI remote) throws MatlabException,
                                                         IOException {
        return create(MatlabClientConfiguration.builder().withAddress(remote).build());
    }

    public static MatlabClient create(MatlabClientConfiguration options) throws
            MatlabException, IOException {
        if (options instanceof LocalMatlabClientConfiguration) {
            return new LocalMatlabClient((LocalMatlabClientConfiguration) options);
        } else if (options instanceof RemoteMatlabClientConfiguration) {
            return new RemoteMatlabClient((RemoteMatlabClientConfiguration) options);
        } else {
            throw new IllegalArgumentException();
        }
    }


}
