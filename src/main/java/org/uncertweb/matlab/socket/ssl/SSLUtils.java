/**
 * Copyright (C) 2013
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */
package org.uncertweb.matlab.socket.ssl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;
import com.google.common.io.Closer;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;

/**
 * TODO JavaDoc
 *
 * @author Christian Autermann <c.autermann@52north.org>
 */
public class SSLUtils {
    private static final Logger log = LoggerFactory.getLogger(SSLUtils.class);
    private SSLUtils() {
    }

    public static KeyStore createEmptyKeyStore()
            throws KeyStoreException,
                   CertificateException,
                   NoSuchAlgorithmException,
                   IOException {
        KeyStore store = KeyStore.getInstance(KeyStore.getDefaultType());
        store.load(null, null);
        return store;
    }

    public static String randomAlias() {
        return UUID.randomUUID().toString();
    }

    public static PrivateKey createKeyFromDER(byte[] key)
            throws NoSuchAlgorithmException,
                   InvalidKeySpecException {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key);
        KeyFactory kf = KeyFactory.getInstance(SSLConstants.KEY_ALGORITHM_RSA);
        return kf.generatePrivate(keySpec);
    }

    public static Certificate[] toChain(List<? extends Certificate> certs) {
        return Lists.reverse(certs).toArray(new Certificate[certs.size()]);
    }

    public static Certificate[] readChain(String fileName)
            throws CertificateException,
                   IOException {
        return toChain(readCertificates(fileName));
    }

    public static Certificate[] readChain(File file)
            throws CertificateException,
                   IOException {
        return toChain(readCertificates(file));
    }

    public static Certificate[] readChain(
            InputSupplier<? extends InputStream> in)
            throws CertificateException,
                   IOException {
        return toChain(readCertificates(in));
    }

    public static PrivateKey readKey(String fileName)
            throws IOException,
                   NoSuchAlgorithmException,
                   InvalidKeySpecException {
        return readKey(new File(fileName));
    }

    public static PrivateKey readKey(File file)
            throws IOException,
                   NoSuchAlgorithmException,
                   InvalidKeySpecException {
        return readKey(Files.newInputStreamSupplier(file));
    }

    public static PrivateKey readKey(InputSupplier<? extends InputStream> in)
            throws IOException,
                   NoSuchAlgorithmException,
                   InvalidKeySpecException {
        Closer closer = Closer.create();
        try {
            Reader reader = closer.register(CharStreams
                    .newReaderSupplier(in, Charsets.UTF_8).getInput());
            return createPrivateKey(new PemReader(reader).readPemObject());
        } catch (IOException e) {
            throw closer.rethrow(e);
        } finally {
            closer.close();
        }
    }

    public static List<X509Certificate> readCertificates(String filename)
            throws CertificateException,
                   IOException {
        return readCertificates(new File(filename));
    }

    public static List<X509Certificate> readCertificates(File file)
            throws CertificateException,
                   IOException {
        return readCertificates(Files.newInputStreamSupplier(file));

    }

    public static List<X509Certificate> readCertificates(
            InputSupplier<? extends InputStream> in)
            throws CertificateException,
                   IOException {
        InputStream is = null;
        try {
            is = in.getInput();
            List<X509Certificate> certs = new LinkedList<X509Certificate>();
            CertificateFactory cf = CertificateFactory
                    .getInstance(SSLConstants.CERTIFICATE_TYPE_X509);
            while (is.available() > 0) {
                X509Certificate cert = (X509Certificate) cf.generateCertificate(is);
                certs.add(cert);
                log.info("Read {}", cert.getSubjectX500Principal().getName());
            }
            return certs;
        } finally {
            Closeables.close(is, true);
        }
    }

    private static PrivateKey createPrivateKey(PemObject privatePemObject)
            throws IOException, InvalidKeySpecException,
                   NoSuchAlgorithmException {
        AlgorithmIdentifier algId = new AlgorithmIdentifier(
                PKCSObjectIdentifiers.rsaEncryption, DERNull.INSTANCE);
        RSAPrivateKey instance = RSAPrivateKey.getInstance(privatePemObject.getContent());
        PrivateKeyInfo privateKeyInfo = new PrivateKeyInfo(algId, instance);
        return createKeyFromDER(privateKeyInfo.toASN1Primitive().getEncoded());
    }
}
