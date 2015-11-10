package com.didekin;

import com.didekin.serviceone.ServiceOneException;
import com.didekin.serviceone.exception.ErrorBean;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.sql.Timestamp;

import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Request;
import retrofit.client.UrlConnectionClient;

/**
 * User: pedro@didekin
 * Date: 05/08/15
 * Time: 20:07
 */
public enum RetrofitRestBuilder {

    BUILDER,;

    // .......... INSTANCE METHODS ..........

    public <T> T getService(Class<T> endPointInterface, String hostAndPort)
    {
//        Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimeStampGsonAdapter()).create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(hostAndPort)
//                .setConverter(new GsonConverter(gson))
                .setErrorHandler(new ServiceOneExceptionHandler())
                .build();

        return restAdapter.create(endPointInterface);
    }

    @SuppressWarnings("unused")
    public <T> T getServiceDebug(Class<T> endPointInterface, String hostAndPort)
    {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(hostAndPort)
                .setClient(new MyUrlConnectionClient())
                .setErrorHandler(new ServiceOneExceptionHandler())
                .build();

        return restAdapter.create(endPointInterface);
    }

//    .............. STATIC FIELDS/ METHODS ..............


    // ............. HELPER CLASSES ..............

    @SuppressWarnings("unused")
    private static class TimeStampGsonAdapter implements JsonDeserializer<Timestamp>, JsonSerializer<Timestamp> {

        @Override
        public JsonElement serialize(Timestamp src, Type srcType, JsonSerializationContext context)
        {
            return new JsonPrimitive(src.getTime());
        }

        @Override
        public Timestamp deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws
                JsonParseException
        {
            return new Timestamp(jsonElement.getAsJsonPrimitive().getAsLong());
        }
    }

    public static class ServiceOneExceptionHandler implements ErrorHandler {

        @Override
        public Throwable handleError(RetrofitError rErr)
        {
            ErrorBean errorBean = null;
            try {
                errorBean = (ErrorBean) rErr.getBodyAs(ErrorBean.class);
            } catch (RuntimeException e) { /* To catch conversion exception.*/
            } finally {
                if (errorBean == null || errorBean.getMessage() == null) {
                    errorBean = new ErrorBean(rErr.getResponse().getReason(), rErr.getResponse().getStatus());
                }
            }
            return new ServiceOneException(errorBean, rErr);
        }
    }

    public static final class MyUrlConnectionClient extends UrlConnectionClient {

        @Override
        protected java.net.HttpURLConnection openConnection(Request request) throws IOException
        {
            HttpURLConnection connection = super.openConnection(request);
            connection.setConnectTimeout(30 * 1000);
            connection.setReadTimeout(300 * 1000);
            return connection;
        }
    }
}