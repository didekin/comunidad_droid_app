package com.didekindroid.lib_one.usuario;

import com.didekinlib.http.HttpHandler;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import retrofit2.Call;

import static com.didekindroid.lib_one.HttpInitializer.httpInitializer;

/**
 * User: pedro@didekin
 * Date: 21/11/16
 * Time: 19:15
 */
public final class UsuarioMockDao implements UsuarioMockEndPoints {

    public static final UsuarioMockDao usuarioMockDao =
            new UsuarioMockDao(httpInitializer.get().getHttpHandler());
    private final UsuarioMockEndPoints endPoint;

    private UsuarioMockDao(HttpHandler httpHandlerIn)
    {
        endPoint = httpHandlerIn.getService(UsuarioMockEndPoints.class);
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
}
