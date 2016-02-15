package de.tavendo.autobahn;

import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * Created by ajay on 2/13/16.
 */
public class WebSocketSSLContext {

    public static final String TAG = WebSocketSSLContext.class.getSimpleName();
    public static final int DEFAULT_PORT = 443;

    private SSLContext mSSLContext;
    private SSLEngine mSSLEngine = null;
    private KeyManager[] mKeyManagers;
    private TrustManager[] mTrustManagers;

    public WebSocketSSLContext() {
        mSSLContext = getDefaultSSLContext();
    }

    /**
     * Initializes default SSL context using TLSv1.2
     *
     * @throws NoSuchAlgorithmException
     * @throws IOException
     * @throws KeyManagementException
     */
    protected SSLContext getDefaultSSLContext() {
        try {
            return SSLContext.getInstance("TLSv1.2");
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Unable to initialize TLSv1.2 SSL context", e);
        }

        return null;
    }

    /**
     * SSL key managers
     *
     * @param keyManagers {@link KeyManager}
     */
    public void setSSLKeyManagers(KeyManager[] keyManagers) {
        mKeyManagers = keyManagers;
    }

    /**
     * Returns SSL key managers
     *
     * @return array of {@link KeyManager}
     */
    public KeyManager[] getSSLKeyManagers() {
        return mKeyManagers;
    }

    /**
     * SSL X.509 certificate trustmanagers
     *
     * @param trustManagers {@link TrustManager}
     */
    public void setSSLTrustManagers(TrustManager[] trustManagers) {
        mTrustManagers = trustManagers;
    }

    /**
     * Returns SSL X.509 trust managers
     *
     * @return array of {@link TrustManager}
     */
    public TrustManager[] getSSLTrustManagers() {
        return mTrustManagers;
    }

    /**
     * This method may be used to supply alternate {@link SSLContext}. A default {@link SSLContext} is created when using wss://
     *
     * @param sslContext {@link SSLContext}
     */
    public void setSSLContext(SSLContext sslContext) {
        mSSLContext = sslContext;
    }

    /**
     * Returns current SSL context
     *
     * @return {@link SSLContext}
     */
    public SSLContext getSSLContext() {
        return mSSLContext;
    }

    /**
     * Get SSL engine
     *
     * @return {@link SSLEngine}
     */
    public SSLEngine getSSLEngine() {
        return mSSLEngine;
    }

    /**
     * Set SSL engine
     *
     * @param sslEngine {@link SSLEngine}
     */
    public void setSSLEngine(SSLEngine sslEngine) {
        mSSLEngine = sslEngine;
    }

    /**
     * Setup SSL engine and start handshake
     *
     * @param host hostname
     * @param port port number
     */
    public void doHandshake(final String host, int port)
            throws
            IOException,
            UnrecoverableKeyException,
            KeyStoreException,
            NoSuchAlgorithmException,
            CertificateException,
            KeyManagementException {
        if (null == mKeyManagers)
            mKeyManagers = createKeyManagers();
        if (null == mTrustManagers)
            mTrustManagers = createTrustManagers();

        mSSLContext.init(mKeyManagers, mTrustManagers, null);
        if (null == mSSLEngine) {
            mSSLEngine = mSSLContext.createSSLEngine(host, port);
            mSSLEngine.setUseClientMode(true);
            mSSLEngine.beginHandshake();
        }
    }

    /**
     * Setup SSL engine and start handshake using port 443
     *
     * @param host
     * @throws KeyManagementException
     * @throws SSLException
     */
    public void doHandshake(final String host)
            throws
            IOException,
            UnrecoverableKeyException,
            KeyStoreException,
            NoSuchAlgorithmException,
            CertificateException,
            KeyManagementException {
        doHandshake(host, DEFAULT_PORT);
    }


    /**
     * Creates default key managers required to initiate the {@link SSLContext}, using a JKS keystore as an input.
     *
     * @return {@link KeyManager} array that will be used to initiate the {@link SSLContext}.
     * @throws Exception
     */
    public final KeyManager[] createKeyManagers() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try {
            keyStore.load(null, null);
        } finally {
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, null);
        return kmf.getKeyManagers();
    }

    /**
     * Creates the trust managers required to initiate the {@link SSLContext}, using a JKS keystore as an input.
     *
     * @param filepath         - the path to the JKS keystore.
     * @param keystorePassword - the keystore's password.
     * @return {@link TrustManager} array, that will be used to initiate the {@link SSLContext}.
     * @throws Exception
     */
    public final TrustManager[] createTrustManagers(String filepath, String keystorePassword) throws CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException {
        KeyStore trustStore = KeyStore.getInstance("JKS");
        InputStream trustStoreIS = new FileInputStream(filepath);
        try {
            trustStore.load(trustStoreIS, keystorePassword.toCharArray());
        } finally {
            if (trustStoreIS != null) {
                trustStoreIS.close();
            }
        }
        TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustFactory.init(trustStore);
        return trustFactory.getTrustManagers();
    }

    /**
     * Creates default trust managers required to initiate the {@link SSLContext}, using a JKS keystore as an input.
     *
     * @return {@link TrustManager} array, that will be used to initiate the {@link SSLContext}.
     * @throws Exception
     */
    public static final TrustManager[] createTrustManagers() throws CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException {
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try {
            trustStore.load(null, null);
        } finally {
        }

        TrustManagerFactory trustFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustFactory.init(trustStore);
        return trustFactory.getTrustManagers();
    }

}
