package com.didekindroid.usuariocomunidad.testutil;

import com.didekinlib.http.HttpHandler;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import retrofit2.Call;

import static com.didekindroid.lib_one.HttpInitializer.httpInitializer;

/**
 * User: pedro@didekin
 * Date: 21/11/16
 * Time: 19:15
 */

@SuppressWarnings("WeakerAccess")
public final class UserComuMockDaoRemote implements UserComuMockEndPoints {

    public static final UserComuMockDaoRemote userComuMockDao =
            new UserComuMockDaoRemote(httpInitializer.get().getHttpHandler());
    private final UserComuMockEndPoints endPoint;

    private UserComuMockDaoRemote(HttpHandler httpHandlerIn)
    {
        endPoint = httpHandlerIn.getService(UserComuMockEndPoints.class);
    }

    @Override
    public Call<Boolean> deleteUser(String userName)
    {
        return endPoint.deleteUser(userName);
    }

    @Override
    public Call<Boolean> regComuAndUserAndUserComu(UsuarioComunidad usuarioCom)
    {
        return endPoint.regComuAndUserAndUserComu(usuarioCom);
    }

    @Override
    public Call<Boolean> regUserAndUserComu(UsuarioComunidad userCom)
    {
        return endPoint.regUserAndUserComu(userCom);
    }
}
