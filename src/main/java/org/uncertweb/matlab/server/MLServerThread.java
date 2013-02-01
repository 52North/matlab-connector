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
	
	private Socket socket;
	private MLInstancePool pool;

	public MLServerThread(Socket socket, MLInstancePool pool) {
		super();
		this.socket = socket;
		this.pool = pool;
	}

	public void run() {
		try {
			MLHandler handler = new MLHandler();

			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();

			MLRequest request = handler.parseRequest(inputStream);
			logger.info("Received request for function '" + request.getFunction() + "'.");

			try {
				MLInstance instance = pool.getInstance(); // this will block
				MLResult result = instance.handle(request);				
				logger.info("Handled request successfully.");
				handler.outputResult(result, outputStream);
			}
			catch (MLConnectorException e) {
				logger.error("Caught exception when calling MATLAB.", e);
				handler.outputException(new MLException(e.getMessage()), outputStream);
			}

			outputStream.close();
			inputStream.close();
			socket.close();
		}
		catch (IOException e) {
			logger.error("Couldn't handle input/output streams: " + e.getMessage(), e);
		}
	}

}
