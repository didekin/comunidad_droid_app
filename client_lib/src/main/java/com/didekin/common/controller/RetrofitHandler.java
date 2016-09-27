package com.didekin.common.controller;

import com.didekin.common.exception.ErrorBean;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Arrays;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static java.util.concurrent.TimeUnit.SECONDS;
import static okhttp3.logging.HttpLoggingInterceptor.Level.BODY;

/**
 * User: pedro@didekin
 * Date: 05/08/15
 * Time: 20:07
 */
@SuppressWarnings("unused")
public class RetrofitHandler {

    private final Retrofit retrofit;

    public RetrofitHandler(final String hostPort, int timeOut)
    {
        retrofit = new Retrofit.Builder()
                .baseUrl(hostPort)
                .client(getOkHttpClient(null, timeOut))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public RetrofitHandler(final String hostPort, final JksInClient jksInAppClient, int timeOut)
    {
        retrofit = new Retrofit.Builder()
                .baseUrl(hostPort)
                .client(getOkHttpClient(jksInAppClient, timeOut))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public <T> T getService(Class<T> endPointInterface)
    {
        return retrofit.create(endPointInterface);
    }

    public Retrofit getRetrofit()
    {
        return retrofit;
    }

    public ErrorBean getErrorBean(Response<?> response) throws IOException
    {
        Converter<ResponseBody, ErrorBean> converter = retrofit.responseBodyConverter(ErrorBean.class, new Annotation[0]);
        ErrorBean errorBean = converter.convert(response.errorBody());
        if (errorBean == null || errorBean.getMessage() == null) {
            okhttp3.Response okhttpResponse = response.raw();
            errorBean = new ErrorBean(okhttpResponse.message(), okhttpResponse.code());
        }
        return errorBean;
    }

    // ====================== HELPER METHODS ========================

    private OkHttpClient getOkHttpClient(JksInClient jksInAppClient, int timeOut)
    {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addNetworkInterceptor(doLoggingInterceptor())
                .connectTimeout(timeOut, SECONDS)
                .readTimeout(timeOut * 2, SECONDS);
        if (jksInAppClient == null) {
            return builder.build();
        } else {
            X509TrustManager trustManager = getTrustManager(jksInAppClient);
            return builder.sslSocketFactory(getSslSocketFactory(trustManager), trustManager)
                    .build();
        }
    }

    private Interceptor doLoggingInterceptor()
    {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(BODY);
        return loggingInterceptor;
    }

    private X509TrustManager getTrustManager(JksInClient jksInAppClient)
    {
        KeyStore keyStore;
        TrustManagerFactory tmf;

        try {
            // Configuración cliente.
            String keyStoreType = KeyStore.getDefaultType();
            keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(jksInAppClient.getInputStream(), jksInAppClient.getJksPswd().toCharArray());
            // Create a TrustManager that trusts the CAs in our JksInAppClient
            tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keyStore);

            TrustManager[] trustManagers = tmf.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:"
                        + Arrays.toString(trustManagers));
            }
            return (X509TrustManager) trustManagers[0];
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException("TrustManager not initialized");
        }
    }

    private SSLSocketFactory getSslSocketFactory(TrustManager trustManager)
    {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, null);
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("SSLSocketFactory not initialized");
        }
    }

    public final static class JksInAppClient implements JksInClient {
        /**
         * File path.
         */
        String jksUri;
        String jksPswd;

        public JksInAppClient(String jksUri, String jksPswd)
        {
            this.jksUri = jksUri;
            this.jksPswd = jksPswd;
        }

        @Override
        public InputStream getInputStream() throws IOException
        {
            return new FileInputStream(jksUri);
        }

        @Override
        public String getJksPswd()
        {
            return jksPswd;
        }
    }
}