package com.didekindroid.usuariocomunidad;

import com.didekinlib.http.HttpHandler;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import retrofit2.Call;

import static com.didekindroid.lib_one.HttpInitializer.httpInitializer;

/**
 * User: pedro@didekin
 * Date: 12/02/2018
 * Time: 12:45
 */

public final class UserComuMockDao implements UserComuMockEndPoints {

    public static final UserComuMockDao userComuMockDao = new UserComuMockDao(httpInitializer.get().getHttpHandler());
    private final UserComuMockEndPoints endPoint;

    private UserComuMockDao(HttpHandler httpHandler)
    {
        endPoint = httpHandler.getService(UserComuMockEndPoints.class);
    }

    @Override
    public Call<Boolean> regUserAndUserComu(UsuarioComunidad userCom)
    {
        return endPoint.regUserAndUserComu(userCom);
    }
}
