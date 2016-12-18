package com.didekinaar.utils;

import com.didekinaar.exception.UiException;

import java.io.IOException;

import retrofit2.Response;

import static com.didekinaar.PrimalCreator.creator;

/**
 * User: pedro@didekin
 * Date: 21/11/16
 * Time: 19:39
 */

public final class AarServiceUtil {

    private AarServiceUtil()
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
