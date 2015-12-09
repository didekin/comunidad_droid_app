package com.didekin.common;

import com.didekin.common.exception.ErrorBean;
import com.didekin.common.exception.InServiceException;
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
@SuppressWarnings("unused")
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
                .setErrorHandler(new ServicesExceptionHandler())
                .build();

        return restAdapter.create(endPointInterface);
    }

    public <T> T getServiceDebug(Class<T> endPointInterface, String hostAndPort)
    {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(hostAndPort)
                .setClient(new MyUrlConnectionClient())
                .setErrorHandler(new ServicesExceptionHandler())
                .build();

        return restAdapter.create(endPointInterface);
    }

//    .............. EXCEPTION HANDLER ..............


    public static class ServicesExceptionHandler implements ErrorHandler {

        @Override
        public Throwable handleError(RetrofitError rErr)
        {
            ErrorBean errorBean = null;
            try {
                errorBean = (ErrorBean) rErr.getBodyAs(ErrorBean.class);
            } catch (RuntimeException e) { /* To catch conversion exception.*/
            } finally {
                if (rErr.getResponse() != null && (errorBean == null || errorBean.getMessage() == null)) {
                    errorBean = new ErrorBean(rErr.getResponse().getReason(), rErr.getResponse().getStatus());
                }
            }
            return new InServiceException(errorBean, rErr);
        }
    }
    // ............. HELPER CLASSES ..............

    private static class TimeStampGsonAdapter implements JsonDeserializer<Timestamp>,
            JsonSerializer<Timestamp> {

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

    public static final class MyUrlConnectionClient extends UrlConnectionClient {

        @Override
        protected java.net.HttpURLConnection openConnection(Request request) throws IOException
        {
            HttpURLConnection connection = super.openConnection(request);
            connection.setConnectTimeout(30 * 1000);
            connection.setReadTimeout(1800 * 1000);
            return connection;
        }
    }
}


/*
* final class InterfaceAdapter<T> implements JsonSerializer<T>, JsonDeserializer<T> {
    public JsonElement serialize(T object, Type interfaceType, JsonSerializationContext context) {
        final JsonObject wrapper = new JsonObject();
        wrapper.addProperty("type", object.getClass().getName());
        wrapper.add("data", context.serialize(object));
        return wrapper;
    }

    public T deserialize(JsonElement elem, Type interfaceType, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject wrapper = (JsonObject) elem;
        final JsonElement typeName = get(wrapper, "type");
        final JsonElement data = get(wrapper, "data");
        final Type actualType = typeForName(typeName);
        return context.deserialize(data, actualType);
    }

    private Type typeForName(final JsonElement typeElem) {
        try {
            return Class.forName(typeElem.getAsString());
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }
    }

    private JsonElement get(final JsonObject wrapper, String memberName) {
        final JsonElement elem = wrapper.get(memberName);
        if (elem == null) throw new JsonParseException("no '" + memberName + "' member found in what was expected to be an interface wrapper");
        return elem;
    }
}

Gson gson = new GsonBuilder().registerTypeAdapter(Animal.class, new InterfaceAdapter<Animal>())
                             .create();
* */