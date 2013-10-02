package org.uncertweb.matlab.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uncertweb.matlab.MLConnectorException;
import org.uncertweb.matlab.MLException;
import org.uncertweb.matlab.MLHandler;
import org.uncertweb.matlab.MLRequest;
import org.uncertweb.matlab.MLResult;
import org.uncertweb.matlab.instance.MLInstance;
import org.uncertweb.matlab.instance.MLInstancePool;

public class MLServerThread extends Thread {
    private final Logger logger = LoggerFactory.getLogger(MLServerThread.class);
    private final Socket socket;
    private final MLInstancePool pool;

    public MLServerThread(Socket socket, MLInstancePool pool) {
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
            try {
                final MLInstance instance = pool.getInstance(); // this will block
                final MLResult result = instance.handle(request);
                logger.info("Handled request successfully.");
                handler.outputResult(result, out);
            } catch (MLConnectorException e) {
                logger.error("Caught exception when calling MATLAB.", e);
                handler.outputException(new MLException(e.getMessage()), out);
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
