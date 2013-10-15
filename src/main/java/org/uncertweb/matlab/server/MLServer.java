package org.uncertweb.matlab.server;

import static com.google.common.base.Preconditions.checkState;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uncertweb.matlab.util.NamedAndGroupedThreadFactory;

public class MLServer {
    private static final Logger log = LoggerFactory.getLogger(MLServer.class);
    public static final String JKS_TYPE = "JKS";
    public static final String TLS_V1_PROTOCOL = "TLSv1";
    public static final String SUN_X509_ALGORITHM = "SunX509";
    public static final String SUN_JSSE_PROVIDER = "SunJSSE";
    public static final String PKIX_ALGORITHM = "PKIX";
    public static final String TLS_PROTOCOL = "TLS";
    private MLInstancePool instancePool;
    private final ExecutorService threadPool = Executors
            .newCachedThreadPool(NamedAndGroupedThreadFactory.builder()
            .name("MLServer").build());
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
        final MLInstancePoolConfig poolConfig = MLInstancePoolConfig
                .builder()
                .withMaximalNumInstances(getOptions().getThreads())
                .withInstanceConfig(instanceConfig)
                .build();
        instancePool = new MLInstancePool(poolConfig);
        this.serverSocket = createServerSocket();
        log.info("Listening on port {}...", getOptions().getPort());
                        Runtime.getRuntime()
                        .addShutdownHook(new MLServerShutdownHook(this));

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

    private X509KeyManager getKeyManager(KeyStore keyStore, char[] keyStorePass)
            throws NoSuchProviderException, UnrecoverableKeyException,
                   KeyStoreException, NoSuchAlgorithmException {
        KeyManagerFactory keyManagerFactory =
                KeyManagerFactory
                .getInstance(SUN_X509_ALGORITHM, SUN_JSSE_PROVIDER);
        keyManagerFactory.init(keyStore, keyStorePass);
        for (KeyManager keyManager : keyManagerFactory.getKeyManagers()) {
            if (keyManager instanceof X509KeyManager) {
                return (X509KeyManager) keyManager;
            }
        }

        throw new NullPointerException();
    }

    private X509TrustManager getTrustManager(KeyStore trustStore)
            throws NullPointerException, NoSuchAlgorithmException,
                   NoSuchProviderException, KeyStoreException {
        TrustManagerFactory trustManagerFactory =
                TrustManagerFactory
                .getInstance(PKIX_ALGORITHM, SUN_JSSE_PROVIDER);
        trustManagerFactory.init(trustStore);
        for (TrustManager trustManager : trustManagerFactory
                .getTrustManagers()) {
            if (trustManager instanceof X509TrustManager) {
                return (X509TrustManager) trustManager;
            }
        }
        throw new NullPointerException();
    }

    private KeyStore loadKeyStore(String type, String path, char[] pass)
            throws IOException, NoSuchAlgorithmException,
                   CertificateException, KeyStoreException {
        KeyStore keyStore = KeyStore.getInstance(type);
        keyStore.load(new FileInputStream(path), pass);
        return keyStore;
    }

    private SSLContext createSSLContext(X509KeyManager keyManager,
                                        X509TrustManager trustManager)
            throws KeyManagementException, NoSuchAlgorithmException {
        SSLContext sslContext = SSLContext.getInstance(TLS_PROTOCOL);
        sslContext.init(new KeyManager[] { keyManager },
                        new TrustManager[] { trustManager }, null);
        return sslContext;
    }

    private ServerSocket createServerSocket() throws IOException,
                                                     NullPointerException {
        if (getOptions().isSSL()) {
            final SSLOptions sslOptions = getOptions().getSSLOptions();
            try {
                KeyStore keyStore = loadKeyStore(JKS_TYPE, sslOptions
                        .getKeyStorePath(), sslOptions.getKeyStorePass());
                KeyStore trustStore = loadKeyStore(JKS_TYPE, sslOptions
                        .getTrustStorePath(), sslOptions.getTrustStorePass());
                X509TrustManager trustManager = getTrustManager(trustStore);
                X509KeyManager keyManager =
                        getKeyManager(keyStore, sslOptions.getKeyStorePass());
                SSLContext sslContext =
                        createSSLContext(keyManager, trustManager);
                SSLServerSocket sslSocket = (SSLServerSocket) sslContext
                        .getServerSocketFactory()
                        .createServerSocket(getOptions().getPort());
                sslSocket.setNeedClientAuth(true);
                sslSocket.setEnabledProtocols(new String[] { TLS_V1_PROTOCOL });
                return sslSocket;

            } catch (NoSuchAlgorithmException ex) {
                throw new RuntimeException(ex);
            } catch (NoSuchProviderException ex) {
                throw new RuntimeException(ex);
            } catch (CertificateException ex) {
                throw new RuntimeException(ex);
            } catch (KeyStoreException ex) {
                throw new RuntimeException(ex);
            } catch (UnrecoverableKeyException ex) {
                throw new RuntimeException(ex);
            } catch (KeyManagementException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            return new ServerSocket(getOptions().getPort());
        }
    }
}
