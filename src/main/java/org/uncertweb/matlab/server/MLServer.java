package org.uncertweb.matlab.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// FIXME: for 0.2. The server doesn't work properly - it gives you the wrong answers?!
// FIXME: for 0.2. Missing parsing for MLString?
public class MLServer {
	
	public static void main(String[] args) {
		final Logger logger = LoggerFactory.getLogger(MLServer.class);
		
		if (args.length > 0) {
			int port = Integer.parseInt(args[0]);
			ServerSocket serverSocket = null;

			try {
				serverSocket = new ServerSocket(port);
				logger.info("Listening on port " + port + "...");

				while (true) {
					try {
						Socket socket = serverSocket.accept();
						logger.info("Client " + socket.getRemoteSocketAddress() + " connected.");
						/**
						 * If any thread calls the MATLAB computational engine (which is single 
						 * threaded), they block. The MLServerThread contains calls to the MATLAB
						 * computational engine. Therefore here we are currently using the 
						 * non-threaded method run() instead of starting the thread with start().
						 */
						new MLServerThread(socket).run();						
					}
					catch (IOException e) {
						logger.error("Could not accept client connection: " + e.getMessage());
					}
				}
			}
			catch (IOException e) {
				logger.error("Could not listen on port " + port + ".");
				System.err.println("Could not listen on port " + port + ".");
			}
			finally {
				if (serverSocket != null) {
					try {
						serverSocket.close();
					}
					catch (IOException e) {
						logger.warn("Could not close server socket on port " + port + ".");
					}
				}				
			}			
		}
		else {
			logger.error("No port specified in program arguments.");
			System.err.println("Please specify the port you wish to listen on.");
		}
	}

}
