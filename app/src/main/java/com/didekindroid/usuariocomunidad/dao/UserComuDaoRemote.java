package com.didekindroid.usuariocomunidad.dao;

import com.didekindroid.exception.UiException;
import com.didekindroid.security.IdentityCacher;
import com.didekinlib.http.ErrorBean;
import com.didekinlib.http.retrofit.UsuarioComunidadEndPoints;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.io.EOFException;
import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

import static com.didekindroid.AppInitializer.creator;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.util.DaoUtil.getResponseBody;
import static com.didekinlib.http.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;

/**
 * User: pedro@didekin
 * Date: 21/11/16
 * Time: 19:15
 */

@SuppressWarnings("WeakerAccess")
public final class UserComuDaoRemote implements UsuarioComunidadEndPoints {

    public static final UserComuDaoRemote userComuDaoRemote = new UserComuDaoRemote();
    private final UsuarioComunidadEndPoints endPoint;
    private final IdentityCacher identityCacher;

    private UserComuDaoRemote()
    {
        this(creator.get().getRetrofitHandler().getService(UsuarioComunidadEndPoints.class), TKhandler);
    }

    public UserComuDaoRemote(UsuarioComunidadEndPoints endPoint, IdentityCacher identityCacher)
    {
        this.endPoint = endPoint;
        this.identityCacher = identityCacher;
    }

    //  ================================== UserComuEndPoints implementation ============================

    @Override
    public Call<Integer> deleteUserComu(String accessToken, long comunidadId)
    {
        return endPoint.deleteUserComu(accessToken, comunidadId);
    }

    @Override
    public Call<List<Comunidad>> getComusByUser(String accessToken)
    {
        return endPoint.getComusByUser(accessToken);
    }

    @Override
    public Call<UsuarioComunidad> getUserComuByUserAndComu(String accessToken, long comunidadId)
    {
        return endPoint.getUserComuByUserAndComu(accessToken, comunidadId);
    }

    @Override
    public Call<Boolean> isOldestOrAdmonUserComu(String accessToken, long comunidadId)
    {
        return endPoint.isOldestOrAdmonUserComu(accessToken, comunidadId);
    }

    @Override
    public Call<Integer> modifyComuData(String currentAccessToken, Comunidad comunidad)
    {
        return endPoint.modifyComuData(currentAccessToken, comunidad);
    }

    @Override
    public Call<Integer> modifyUserComu(String accessToken, UsuarioComunidad userComu)
    {
        return endPoint.modifyUserComu(accessToken, userComu);
    }

    @Override
    public Call<Boolean> regComuAndUserAndUserComu(UsuarioComunidad usuarioCom)
    {
        Timber.d(("regComuAndUserAndUserComu()"));
        return endPoint.regComuAndUserAndUserComu(usuarioCom);
    }

    @Override
    public Call<Boolean> regComuAndUserComu(String accessToken, UsuarioComunidad usuarioCom)
    {
        return endPoint.regComuAndUserComu(accessToken, usuarioCom);
    }

    @Override
    public Call<Boolean> regUserAndUserComu(UsuarioComunidad userCom)
    {
        Timber.d("regUserAndUserComu()");
        return endPoint.regUserAndUserComu(userCom);
    }

    @Override
    public Call<Integer> regUserComu(String accessToken, UsuarioComunidad usuarioComunidad)
    {
        return endPoint.regUserComu(accessToken, usuarioComunidad);
    }

    @Override
    public Call<List<UsuarioComunidad>> seeUserComusByComu(String accessToken, long comunidadId)
    {
        return endPoint.seeUserComusByComu(accessToken, comunidadId);
    }

    @Override
    public Call<List<UsuarioComunidad>> seeUserComusByUser(String accessToken)
    {
        return endPoint.seeUserComusByUser(accessToken);
    }

//  =============================================================================
//                          CONVENIENCE METHODS
//  =============================================================================

    public int deleteUserComu(long comunidadId) throws UiException
    {
        Timber.d("deleteUserComu()");
        try {
            Response<Integer> response = deleteUserComu(identityCacher.checkBearerTokenInCache(), comunidadId).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    public List<Comunidad> getComusByUser() throws UiException
    {
        Timber.d("getComusByUser()");
        try {
            Response<List<Comunidad>> response = getComusByUser(identityCacher.checkBearerTokenInCache()).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    public UsuarioComunidad getUserComuByUserAndComu(long comunidadId) throws UiException
    {
        Timber.d("getUserComuByUserAndComu()");
        try {
            Response<UsuarioComunidad> response = getUserComuByUserAndComu(identityCacher.checkBearerTokenInCache(), comunidadId).execute();
            return getResponseBody(response);
        } catch (EOFException eo) {
            return null;
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    public boolean isOldestOrAdmonUserComu(long comunidadId) throws UiException
    {
        Timber.d("isOldestOrAdmonUserComu()");
        try {
            Response<Boolean> response = isOldestOrAdmonUserComu(identityCacher.checkBearerTokenInCache(), comunidadId).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    public int modifyComuData(Comunidad comunidad) throws UiException
    {
        Timber.d("modifyComuData()");
        try {
            Response<Integer> response = modifyComuData(identityCacher.checkBearerTokenInCache(), comunidad).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    public int modifyUserComu(UsuarioComunidad userComu) throws UiException
    {
        Timber.d("modifyUserComu()");
        try {
            Response<Integer> response = modifyUserComu(identityCacher.checkBearerTokenInCache(), userComu).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    public boolean regComuAndUserComu(UsuarioComunidad usuarioComunidad) throws UiException
    {
        Timber.d("regComuAndUserComu()");
        try {
            Response<Boolean> response = regComuAndUserComu(identityCacher.checkBearerTokenInCache(), usuarioComunidad).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }

    }

    public int regUserComu(UsuarioComunidad usuarioComunidad) throws UiException
    {
        Timber.d("regUserComu()");
        try {
            Response<Integer> response = regUserComu(identityCacher.checkBearerTokenInCache(), usuarioComunidad).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    public List<UsuarioComunidad> seeUserComusByComu(long idComunidad) throws UiException
    {
        Timber.d("seeUserComusByComu()");
        try {
            Response<List<UsuarioComunidad>> response = seeUserComusByComu(identityCacher.checkBearerTokenInCache(), idComunidad).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    public List<UsuarioComunidad> seeUserComusByUser() throws UiException
    {
        Timber.d("seeUserComusByUser()");
        try {
            Response<List<UsuarioComunidad>> response = seeUserComusByUser(identityCacher.checkBearerTokenInCache()).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }
}
