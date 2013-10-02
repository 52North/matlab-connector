package org.uncertweb.matlab.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;
import com.google.common.io.OutputSupplier;

import org.uncertweb.matlab.MLException;
import org.uncertweb.matlab.MLHandler;
import org.uncertweb.matlab.MLRequest;
import org.uncertweb.matlab.MLResult;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class MLClient {
    private static final Logger log = LoggerFactory.getLogger(MLClient.class);
    private static final int CONNECT_TIMEOUT = 10 * 1000; // 10s to connect
    private final MLHandler handler = new MLHandler();

    /**
     * Sends a request to a MATLAB server. The server must be using the supplied
     * server.m and waiting for
     * connections on the specified port.
     *
     * @param host    the address of the MATLAB server
     * @param port    the port of the MATLAB server
     * @param request the <code>MLRequest</code> to send
     *
     * @return the <code>MLResult</code> of the function
     *
     * @throws MLException if MATLAB encountered an error during function
     *                     execution
     * @throws IOException if the connection to the MATLAB server failed
     */
    // TODO: sometimes a SocketException gets thrown (possible to do with lots of requests in a short amount of time), for now there's just three attempts if this happens
    public MLResult sendRequest(String host, int port, MLRequest request) throws
            MLException, IOException {
        SocketException thrown = null;
        for (int attempt = 0; attempt < 3; attempt++) {
            try {
                return _sendRequest(connect(host, port), request);
            } catch (SocketException e) {
                thrown = e;
            }
        }
        throw thrown;
    }

    public MLResult sendRequest(String mlProxyURL, MLRequest request) throws
            MLException, IOException {
        Connection connection = connect(mlProxyURL);
        connection.getOutput().write("request=".getBytes());
        return _sendRequest(connection, request);
    }

    private Connection connect(String host, int port) throws IOException {
        final Socket socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), CONNECT_TIMEOUT);
        return new Connection() {
            @Override
            public InputStream getInput() throws IOException {
                return socket.getInputStream();
            }

            @Override
            public OutputStream getOutput() throws IOException {
                return socket.getOutputStream();
            }

            @Override
            public void close() {
                try {
                    socket.close();
                } catch (IOException e) {
                    log.error("Error closing socket", e);
                }
            }
        };
    }

    private Connection connect(String mlProxyURL) throws IOException {
        final URLConnection connection = new URL(mlProxyURL).openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        return new Connection() {
            @Override
            public InputStream getInput() throws IOException {
                return connection.getInputStream();
            }

            @Override
            public OutputStream getOutput() throws IOException {
                return connection.getOutputStream();
            }
        };
    }

    private MLResult _sendRequest(Connection con, MLRequest request)
            throws MLException, IOException {
        try {
            handler.outputRequest(request, con.getOutput());
            return parseResponse(con);
        } finally {
            con.close();
        }
    }

    private MLResult parseResponse(InputSupplier<? extends InputStream> in)
            throws IOException, MLException {
        String response = CharStreams.toString(CharStreams
                .newReaderSupplier(in, Charsets.UTF_8));

//        try {
//            throw handler.parse(response, MLException.class);
//        } catch (JsonParseException e1) {
//            try {
//                throw handler.parse(response, IOException.class);
//            } catch (JsonParseException e2) {
//                return handler.parse(response, MLResult.class);
//            }
//        }
        // parse
        // try for exception
        // FIXME speedup, not the best
        if (response.startsWith("{\"exception\"")) {
            throw handler.parseException(response);
        } else {
            // not an error, must be result
            return handler.parseResult(response);
        }
    }

    private abstract class Connection implements InputSupplier<InputStream>,
                                                 OutputSupplier<OutputStream> {
        public void close() {
            try {
                getInput().close();
            } catch (IOException e) {
                log.error("Error closing input stream", e);
            }
            try {
                getOutput().close();
            } catch (IOException e) {
                log.error("Error closing output stream", e);
            }
        }
    }
}