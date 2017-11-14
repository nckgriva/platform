package com.gracelogic.platform.payment.service;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class HttpClientUtils {
    /**
     * Snippet taken from https://gist.github.com/zjor/a9a27bfeb99d446dc02f
     *
     * @return http client which ignores SSL and ready for multiple threaded environment.
     */
    public static HttpClientBuilder getMultithreadedUnsecuredClientBuilder() {
        try {
            SSLContextBuilder sslbuilder = new SSLContextBuilder();
            sslbuilder.loadTrustMaterial(null, new AlwaysTrustStrategy());

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslbuilder.build(),
                    SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER
            );

            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("https", sslsf)
                    .register("http", new PlainConnectionSocketFactory())
                    .build();

            return HttpClientBuilder.create()
                    .setSSLSocketFactory(sslsf)
                    .setConnectionManager(new PoolingHttpClientConnectionManager(socketFactoryRegistry));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static CloseableHttpClient getMultithreadedUnsecuredClient() {
        return getMultithreadedUnsecuredClientBuilder().build();
    }

    private static class AlwaysTrustStrategy implements TrustStrategy {
        @Override
        public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            return true;
        }
    }

}
