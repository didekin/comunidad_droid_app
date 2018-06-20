package com.didekindroid.usuariocomunidad;

import com.didekinlib.http.HttpHandler;
import com.didekinlib.http.usuario.UserMockEndPoints;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.Response;

import static com.didekindroid.lib_one.HttpInitializer.httpInitializer;
import static com.didekindroid.lib_one.usuario.UserMockDao.usuarioMockDao;

/**
 * User: pedro@didekin
 * Date: 12/02/2018
 * Time: 12:45
 */

public final class UserComuMockDao implements UserMockEndPoints {

    public static final UserComuMockDao userComuMockDao = new UserComuMockDao(httpInitializer.get().getHttpHandler());
    private final UserMockEndPoints endPoint;

    private UserComuMockDao(HttpHandler httpHandlerIn)
    {
        this.endPoint = httpHandlerIn.getService(UserMockEndPoints.class);
    }

    @Override
    public Single<Response<Boolean>> deleteUser(String userName)
    {
        return usuarioMockDao.deleteUser(userName);
    }

    @Override
    public Single<Response<String>> regComuAndUserAndUserComu(UsuarioComunidad usuarioCom)
    {
        return usuarioMockDao.regComuAndUserAndUserComu(usuarioCom);
    }

    @Override
    public Single<Response<String>> regUserAndUserComu(UsuarioComunidad userCom)
    {
        return endPoint.regUserAndUserComu(userCom);
    }

    @Override
    public Call<String> tryTokenInterceptor(String accessToken, String mock_path, String mock2_path)
    {
        return usuarioMockDao.tryTokenInterceptor(accessToken, mock_path, mock2_path);
    }
}
