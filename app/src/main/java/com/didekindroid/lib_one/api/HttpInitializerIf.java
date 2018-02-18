package com.didekindroid.lib_one.api;

import android.content.Context;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.http.HttpHandler;

import java.io.IOException;

import retrofit2.Response;

/**
 * User: pedro@didekin
 * Date: 14/02/2018
 * Time: 15:40
 */

public interface HttpInitializerIf {

    Context getContext();

    HttpHandler getHttpHandler();

    <T> T getResponseBody(Response<T> response) throws UiException, IOException;
}
