package org.uncertweb.matlab.server;

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uncertweb.matlab.util.NamedAndGroupedThreadFactory;

public class MLServer {
    private static final Logger log = LoggerFactory.getLogger(MLServer.class);
    private MLInstancePool instancePool;
    private final ExecutorService threadPool = Executors
            .newCachedThreadPool(NamedAndGroupedThreadFactory.builder().name("MLServer").build());
    private ServerSocket serverSocket;
    private final MLServerOptions options;

    public MLServer(MLServerOptions options) {
        this.options = options;
    }

    public MLServerOptions getOptions() {
        return this.options;
    }

    public MLInstancePool getPool() {
        return instancePool;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void start() throws IOException, MLConnectorException {
        setup();
        while (true) {
            awaitConnection();
        }
    }

    private void setup() throws IOException, MLConnectorException {

        synchronized (this) {
            checkState(getPool() == null, "Server already started.");
        }

        // create out matlab instance instancePool
        final MLInstanceConfig instanceConfig = MLInstanceConfig.builder()
                .withBaseDir(getOptions().getPath())
                .build();
        final MLInstancePoolConfig poolConfig = MLInstancePoolConfig.builder()
                .withMaximalNumInstances(getOptions().getThreads())
                .withInstanceConfig(instanceConfig)
                .build();
        instancePool = new MLInstancePool(poolConfig);

        // create socket
        serverSocket = new ServerSocket(getOptions().getPort());
        log.info("Listening on port {}...", getOptions().getPort());

        // handle shutdown gracefully
        Runtime.getRuntime().addShutdownHook(new MLServerShutdownHook(this));
    }

    private void awaitConnection() {
        try {
            final Socket socket = getServerSocket().accept();
            log.info("Client {} connected.", socket.getRemoteSocketAddress());
            threadPool.execute(new MLServerTask(socket, getPool()));
        } catch (IOException e) {
            // this exception will be thrown a few times during shutdown, hence the check here
            if (!getServerSocket().isClosed()) {
                log.error("Could not accept client connection: {}",
                          e.getMessage());
            }
        }
    }
}
