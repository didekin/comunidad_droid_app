package com.didekindroid.lib_one;

import android.content.Context;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.security.JksInAndroidApp;
import com.didekindroid.lib_one.util.CommonAssertionMsg;
import com.didekinlib.http.HttpHandler;
import com.didekinlib.http.JksInClient;
import com.didekinlib.model.common.dominio.BeanBuilder;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Response;

import static java.lang.Integer.parseInt;

/**
 * User: pedro@didekin
 * Date: 17/11/16
 * Time: 14:21
 */
public final class HttpInitializer {

    public static final AtomicReference<HttpInitializer> httpInitializer = new AtomicReference<>();
    private final Context context;
    private final JksInClient jksInClient;
    private final HttpHandler httpHandler;

    @SuppressWarnings("SyntheticAccessorCall")
    private HttpInitializer(HttpInitializerBuilder builder)
    {
        context = builder.context;
        httpHandler = builder.httpHandler;
        jksInClient = builder.jksInClient;
    }

    public Context getContext()
    {
        return context;
    }

    public HttpHandler getHttpHandler()
    {
        return httpHandler;
    }

    public <T> T getResponseBody(Response<T> response) throws UiException, IOException
    {
        if (response.isSuccessful()) {
            return response.body();
        } else {
            throw new UiException(httpHandler.getErrorBean(response));
        }
    }

    //    ==================== BUILDER ====================

    public static class HttpInitializerBuilder implements BeanBuilder<HttpInitializer> {

        private HttpHandler httpHandler = null;
        private Context context;
        private JksInClient jksInClient;

        public HttpInitializerBuilder(Context context)
        {
            this.context = context;
        }

        public HttpInitializerBuilder httpHandler(int webHost, int webHostPort, int timeOut)
        {
            httpHandler = new HttpHandler(context.getString(webHost) + context.getString(webHostPort), parseInt(context.getString(timeOut)));
            return this;
        }

        public HttpInitializerBuilder jksInClient(int bksPswd, int bksName)
        {
            String bksPasswordStr = context.getString(bksPswd);
            String bksNameStr = context.getString(bksName);

            int bksRawFileResourceId = context.getResources().getIdentifier(bksNameStr, "raw", context.getPackageName());

            if (bksPasswordStr.isEmpty() || bksRawFileResourceId <= 0) {
                throw new IllegalStateException("BKS should be initialized in client application.");
            }
            jksInClient = new JksInAndroidApp(bksPasswordStr, bksRawFileResourceId);
            return this;
        }

        @SuppressWarnings("SyntheticAccessorCall")
        @Override
        public HttpInitializer build()
        {
            HttpInitializer httpInitializer = new HttpInitializer(this);
            if (httpInitializer.httpHandler == null || httpInitializer.jksInClient == null) {
                throw new IllegalStateException(CommonAssertionMsg.httpInitializer_wrong_build_data);
            }
            return httpInitializer;
        }
    }
}
