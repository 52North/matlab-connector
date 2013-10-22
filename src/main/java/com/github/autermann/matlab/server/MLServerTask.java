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
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.autermann.matlab.MLException;
import com.github.autermann.matlab.MLHandler;
import com.github.autermann.matlab.MLRequest;
import com.github.autermann.matlab.MLResult;

public class MLServerTask implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(MLServerTask.class);
    private final Socket socket;
    private final MLInstancePool pool;

    public MLServerTask(Socket socket, MLInstancePool pool) {
        this.socket = socket;
        this.pool = pool;
    }

    @Override
    public void run() {
        try {
            final MLHandler handler = new MLHandler();
            final InputStream in = socket.getInputStream();
            final OutputStream out = socket.getOutputStream();
            final MLRequest request = handler.parseRequest(in);
            logger.info("Received request for function '{}'.",
                        request.getFunction());
            MLInstance instance = null;
            try {

                instance = pool.getInstance();
                final MLResult result = instance.handle(request);
                logger.info("Handled request successfully.");
                handler.outputResult(result, out);
            } catch (MLConnectorException e) {
                logger.error("Caught exception when calling MATLAB.", e);
                handler.outputException(new MLException(e.getMessage()), out);
            } finally {
                pool.returnInstance(instance);
            }
        } catch (IOException e) {
            logger.error("Couldn't handle input/output streams: "
                         + e.getMessage(), e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                logger.error("Couldn't close socket: " + e.getMessage(), e);
            }
        }
    }
}
