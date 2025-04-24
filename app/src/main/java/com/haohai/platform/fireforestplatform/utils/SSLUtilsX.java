package com.haohai.platform.fireforestplatform.utils;


import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
public class SSLUtilsX {

    private static X509TrustManager trustManager;

    public static SSLSocketFactory createSSLSocketFactory() {
        try {
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init((KeyStore) null);
            TrustManager[] trustManagers = tmf.getTrustManagers();

            for (TrustManager tm : trustManagers) {
                if (tm instanceof X509TrustManager) {
                    trustManager = (X509TrustManager) tm;
                    break;
                }
            }

            if (trustManager == null) {
                throw new IllegalStateException("No X509TrustManager found");
            }

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[] { trustManager }, new SecureRandom());
            return sslContext.getSocketFactory();

        } catch (Exception e) {
            throw new RuntimeException("Failed to create SSLSocketFactory", e);
        }
    }

    public static X509TrustManager getX509TrustManager() {
        if (trustManager == null) {
            // 保险起见再初始化一次（防止调用时为 null）
            createSSLSocketFactory();
        }
        return trustManager;
    }
}


