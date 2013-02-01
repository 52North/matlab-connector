package org.uncertweb.matlab.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uncertweb.matlab.MLConnectorException;
import org.uncertweb.matlab.instance.MLInstancePool;

public class MLServer {

	public static void main(String[] args) {
		final Logger logger = LoggerFactory.getLogger(MLServer.class);

		if (args.length > 0) {
			final int port = Integer.parseInt(args[0]);
			int threads = 1;
			if (args.length > 1) {
				threads = Integer.parseInt(args[1]);
			}
			String baseDir = System.getProperty("user.dir");
			if (args.length > 2) {
				baseDir = args[2];
			}
			
			try {
				// create out matlab instance pool
				final MLInstancePool pool = new MLInstancePool(threads, baseDir);

				// create socket
				final ServerSocket serverSocket = new ServerSocket(port);
				logger.info("Listening on port " + port + "...");
				
				// handle shutdown gracefully
				Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
					@Override
					public void run() {
						logger.info("Shutting down...");
						
						// close socket
						try {
							serverSocket.close();
						}
						catch (IOException e) {
							logger.warn("Couldn't close server socket on port " + port + ".");
						}
						
						// destroy pool
						pool.destroy();
						
						logger.info("Bye!");
					}					
				}));

				while (true) {
					try {
						Socket socket = serverSocket.accept();
						logger.info("Client " + socket.getRemoteSocketAddress() + " connected.");
						new MLServerThread(socket, pool).start();						
					}
					catch (IOException e) {
						logger.error("Could not accept client connection: " + e.getMessage());
					}
				}
			}
			catch (IOException e1) {
				System.err.println("Couldn't listen on port " + port + ".");
			}
			catch (MLConnectorException e2) {
				System.err.println("Couldn't setup MATLAB instance pool.");
			}
		}
		else {
			System.err.println("Please specify the port you wish to listen on.");
		}
	}

}
