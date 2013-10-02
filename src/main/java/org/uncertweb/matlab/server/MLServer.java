package org.uncertweb.matlab.server;

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uncertweb.matlab.MLConnectorException;
import org.uncertweb.matlab.instance.MLInstancePool;
import org.uncertweb.matlab.util.NamedAndGroupedThreadFactory;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;

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
        instancePool = new MLInstancePool(getOptions().getThreads(),
                                          getOptions().getPath());

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

    public static void main(String[] args) {
        final MLServerOptions options = new MLServerOptions(
                7000, 5, System.getProperty("user.dir"));
        final JCommander cli = new JCommander(options);
        cli.setProgramName("java " + MLServer.class.getName());
        try {
            cli.parse(args);
        } catch (ParameterException e) {
            cli.usage();
            errorExit(e.getMessage());
        }
        if (options.isHelp()) {
            cli.usage();
        } else {
            try {
                new MLServer(options).start();
            } catch (IOException e) {
                errorExit("Couldn't listen on port %d.\n", options.getPort());
            } catch (MLConnectorException e) {
                errorExit("Couldn't setup MatLab instance pool.");
            }
        }
    }

    private static void errorExit(String format, Object... args) {
        if (format != null) {
            System.err.printf(format + "\n", args);
        }
        System.exit(1);
    }
}
