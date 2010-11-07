package com.eatnumber1.mapy;

import org.apache.commons.io.IOUtils;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

/**
 * @author Russell Harmon
 * @since Nov 5, 2010
 */
public class CSHSocketFactory extends SocketFactory {
	private static KeyStore keyStore;

	private SSLSocketFactory delegateFactory;

	static {
		InputStream certStream = ClassLoader.getSystemResourceAsStream("opcomm.crt");
		KeyStore ks;
		try {
			Certificate cert = CertificateFactory.getInstance("X.509").generateCertificate(certStream);
			ks = KeyStore.getInstance(KeyStore.getDefaultType());
			ks.load(null, null);
			ks.setCertificateEntry("opcomm", cert);
		} catch( KeyStoreException e ) {
			throw new RuntimeException(e);
		} catch( CertificateException e ) {
			throw new RuntimeException(e);
		} catch( NoSuchAlgorithmException e ) {
			throw new RuntimeException(e);
		} catch( IOException e ) {
			throw new RuntimeException(e);
		} finally {
			IOUtils.closeQuietly(certStream);
		}
		keyStore = ks;
	}

	{
		SSLContext ctx;
		try {
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			tmf.init(keyStore);

			ctx = SSLContext.getInstance("TLS");
			ctx.init(null, tmf.getTrustManagers(), null);
		} catch( KeyStoreException e ) {
			throw new RuntimeException(e);
		} catch( NoSuchAlgorithmException e ) {
			throw new RuntimeException(e);
		} catch( KeyManagementException e ) {
			throw new RuntimeException(e);
		}
		delegateFactory = ctx.getSocketFactory();
	}


	@Override
	public Socket createSocket( String s, int i ) throws IOException {
		return delegateFactory.createSocket(s, i);
	}

	@Override
	public Socket createSocket( String s, int i, InetAddress inetAddress, int i1 ) throws IOException {
		return delegateFactory.createSocket(s, i, inetAddress, i1);
	}

	@Override
	public Socket createSocket( InetAddress inetAddress, int i ) throws IOException {
		return delegateFactory.createSocket(inetAddress, i);
	}

	@Override
	public Socket createSocket( InetAddress inetAddress, int i, InetAddress inetAddress1, int i1 ) throws IOException {
		return delegateFactory.createSocket(inetAddress, i, inetAddress1, i1);
	}

	@SuppressWarnings({ "UnusedDeclaration" })
	public static CSHSocketFactory getDefault() {
		return new CSHSocketFactory();
	}
}
