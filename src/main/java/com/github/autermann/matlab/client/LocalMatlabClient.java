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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.github.autermann.matlab.MatlabException;
import com.github.autermann.matlab.MatlabRequest;
import com.github.autermann.matlab.MatlabResult;
import com.github.autermann.matlab.server.MatlabInstance;
import com.github.autermann.matlab.server.MatlabInstancePool;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann
 */
public class LocalMatlabClient extends MatlabClient {

    private final MatlabInstancePool pool;
    private final ExecutorService executor;

    public LocalMatlabClient(LocalMatlabClientConfiguration options)
            throws MatlabException {
        int threads = options.getInstanceConfiguration().getNumThreads();
        this.executor = Executors.newFixedThreadPool(threads);
        this.pool = new MatlabInstancePool(options.getInstanceConfiguration());
    }

    @Override
    public MatlabResult execSync(MatlabRequest request)
            throws MatlabException {
        MatlabInstance instance = this.pool.getInstance();
        try {
            return instance.handle(request);
        } finally {
            this.pool.returnInstance(instance);
        }
    }

    @Override
    public void close()
            throws IOException {
        this.executor.shutdown();
        this.pool.destroy();
    }

    @Override
    public Future<MatlabResult> exec(MatlabRequest request) {
        return this.executor.submit(() -> execSync(request));
    }

}
