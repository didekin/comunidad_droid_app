package com.didekin.common;

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

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

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

    public RetrofitHandler(final String hostPort, final JksInClient jksInAppClient)
    {
        retrofit = new Retrofit.Builder()
                .baseUrl(hostPort)
                .client(getOkHttpClient(jksInAppClient))
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
        if (errorBean == null || errorBean.getMessage() == null){
            okhttp3.Response okhttpResponse = response.raw();
            errorBean = new ErrorBean(okhttpResponse.message(),okhttpResponse.code());
        }
        return errorBean;
    }

    // ====================== HELPER METHODS ========================

    private OkHttpClient getOkHttpClient(JksInClient jksInAppClient)
    {
        return new OkHttpClient.Builder()
                .addNetworkInterceptor(doLoggingInterceptor())
                .connectTimeout(100, SECONDS)  //30
                .readTimeout(100, SECONDS)     //60
                .sslSocketFactory(getSslContext(jksInAppClient).getSocketFactory())
                .build();
    }

    protected Interceptor doLoggingInterceptor()
    {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(BODY);
        return loggingInterceptor;
    }

    private SSLContext getSslContext(JksInClient jksInAppClient)
    {
        KeyStore keyStore;
        TrustManagerFactory tmf;
        SSLContext context = null;

        try {
            // Configuración cliente.
            String keyStoreType = KeyStore.getDefaultType();
            keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(jksInAppClient.getInputStream(), jksInAppClient.getJksPswd().toCharArray());
            // Create a TrustManager that trusts the CAs in our JksInAppClient
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);
            context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
        return context;
    }

    public final static class JksInAppClient implements JksInClient {
        /**
         * File path.
         */
        public String jksUri;
        public String jksPswd;

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