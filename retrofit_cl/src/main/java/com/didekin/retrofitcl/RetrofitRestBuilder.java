package com.didekin.retrofitcl;

import com.didekin.exception.ErrorBean;
import com.google.gson.*;
import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;

import java.lang.reflect.Type;
import java.sql.Timestamp;

/**
 * User: pedro@didekin
 * Date: 05/08/15
 * Time: 20:07
 */
public enum RetrofitRestBuilder {

    BUILDER,
    ;

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

        T endPoint = restAdapter.create(endPointInterface);

        return endPoint;
    }


     // ............. HELPER CLASSES ..............

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

        private static final String TAG = ServiceOneExceptionHandler.class.getCanonicalName();

        @Override
        public Throwable handleError(RetrofitError retrofitError)
        {
           ErrorBean errorBean = null;
            try {
                errorBean = (ErrorBean) retrofitError.getBodyAs(ErrorBean.class);
            } catch (RuntimeException e) { /* To catch conversion exception.*/
            } finally {
                if (errorBean == null || errorBean.getMessage() == null) {
                    errorBean = new ErrorBean(
                            retrofitError.getCause() != null ?
                                    retrofitError.getCause().getMessage() :
                                    retrofitError.getResponse().getReason(),
                            retrofitError.getResponse().getStatus());
                }
            }
            return new ServiceOneException(errorBean, retrofitError);
        }
    }
}