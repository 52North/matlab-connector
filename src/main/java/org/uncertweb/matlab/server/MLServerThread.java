package org.uncertweb.matlab.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import matlabcontrol.LocalMatlabProxy;
import matlabcontrol.MatlabInvocationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uncertweb.matlab.MLException;
import org.uncertweb.matlab.MLHandler;
import org.uncertweb.matlab.MLRequest;
import org.uncertweb.matlab.MLResult;
import org.uncertweb.matlab.value.MLArray;
import org.uncertweb.matlab.value.MLMatrix;
import org.uncertweb.matlab.value.MLScalar;

public class MLServerThread extends Thread {

	private final Logger logger = LoggerFactory.getLogger(MLServerThread.class);
	private Socket socket;

	public MLServerThread(Socket socket) {
		super("MLServerThread");
		this.socket = socket;
	}

	public void run() {
		try {
			MLHandler handler = new MLHandler();

			InputStream inputStream = socket.getInputStream();
			OutputStream outputStream = socket.getOutputStream();

			MLRequest request = handler.parseRequest(inputStream);
			logger.info("Received request for function '" + request.getFunction() + "'.");

			try {
				LocalMatlabProxy.eval(request.toEvalString() + ";");

				MLResult result = new MLResult();

				for (int i = 1; i <= request.getResultCount(); i++) {
					LocalMatlabProxy.eval("v=result" + i + ";vsize=size(v);rows=vsize(:,1);cols=vsize(:,2);");
					int rows = (int) ((double[]) LocalMatlabProxy.returningEval("rows;", 1))[0];
					int cols = (int) ((double[]) LocalMatlabProxy.returningEval("cols;", 1))[0];
					double[] v = (double[]) LocalMatlabProxy.returningEval("v;", 1);
					if (rows == 1) {
						if (cols == 1) {
							// scalar
							result.addResult(new MLScalar(v[0]));
						}
						else {
							// array
							result.addResult(new MLArray(v));
						}
					}
					else {
						// matrix
						double[][] matrix = new double[rows][cols];
						for (int row = 0; row < rows; row++) {
							for (int col = 0; col < cols; col++) {
								matrix[row][col] = v[(row * cols) + col];
								
							}
						}
						result.addResult(new MLMatrix(matrix));
					}
				}
				
				logger.info("Handled request successfully.");
				handler.outputResult(result, outputStream);
			}
			catch (MatlabInvocationException e) {
				logger.error("Caught exception when calling MATLAB.", e);
				handler.outputException(new MLException(e.getMessage()), outputStream);
			}

			outputStream.close();
			inputStream.close();
			socket.close();
		}
		catch (IOException e) {
			logger.error("Couldn't handle input/output streams: " + e.getMessage());
		}
	}

}
