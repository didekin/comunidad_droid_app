package com.didekindroid.util;

import com.didekindroid.exception.UiException;

import java.io.IOException;

import retrofit2.Response;

import static com.didekindroid.AppInitializer.creator;

/**
 * User: pedro@didekin
 * Date: 21/11/16
 * Time: 19:39
 */

public final class DaoUtil {

    private DaoUtil()
    {
    }

    public static <T> T getResponseBody(Response<T> response) throws UiException, IOException
    {
        if (response.isSuccessful()) {
            return response.body();
        } else {
            throw new UiException(creator.get().getRetrofitHandler().getErrorBean(response));
        }
    }
}
