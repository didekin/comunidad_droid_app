package com.didekindroid.common;

import android.util.Log;
import com.didekindroid.usuario.webservices.ServiceOneExceptionHandler;
import com.google.gson.*;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

import java.lang.reflect.Type;
import java.sql.Timestamp;

/**
 * User: pedro@didekin
 * Date: 05/08/15
 * Time: 20:07
 */
public enum RetrofitRestBuilder {

    BUILDER,;

    public static <T> T getService(Class<T> endPointInterface, String hostAndPort)
    {
        Log.d(".RetrofitRestBuilder", "getService()");

        Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimeStampGsonAdapter()).create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(hostAndPort)
                .setConverter(new GsonConverter(gson))
                .setErrorHandler(new ServiceOneExceptionHandler())
                .build();

        T endPoint = restAdapter.create(endPointInterface);

        return endPoint;
    }

    private static class TimeStampGsonAdapter implements JsonDeserializer<Timestamp> {

        @Override
        public Timestamp deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws
                JsonParseException
        {
            return new Timestamp(jsonElement.getAsJsonPrimitive().getAsLong());
        }
    }
}
