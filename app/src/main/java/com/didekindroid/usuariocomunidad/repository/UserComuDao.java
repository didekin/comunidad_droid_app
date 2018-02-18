package com.didekindroid.usuariocomunidad.repository;

import com.didekindroid.lib_one.api.HttpInitializerIf;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.security.IdentityCacherIf;
import com.didekindroid.lib_one.security.SecInitializerIf;
import com.didekinlib.http.exception.ErrorBean;
import com.didekinlib.http.usuariocomunidad.UsuarioComunidadEndPoints;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.io.EOFException;
import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

import static com.didekindroid.lib_one.HttpInitializer.httpInitializer;
import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.lib_one.util.Device.getDeviceLanguage;
import static com.didekinlib.http.exception.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;

/**
 * User: pedro@didekin
 * Date: 21/11/16
 * Time: 19:15
 */

@SuppressWarnings("WeakerAccess")
public final class UserComuDao implements UsuarioComunidadEndPoints {

    public static final UserComuDao userComuDao = new UserComuDao(secInitializer.get(), httpInitializer.get());
    private final UsuarioComunidadEndPoints endPoint;
    private final IdentityCacherIf tkCacher;

    public UserComuDao(SecInitializerIf secInitializerIn, HttpInitializerIf httpInitializerIn)
    {
        endPoint = httpInitializerIn.getHttpHandler().getService(UsuarioComunidadEndPoints.class);
        tkCacher = secInitializerIn.getTkCacher();
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
    public Call<Boolean> regComuAndUserAndUserComu(String localeToStr, UsuarioComunidad usuarioCom)
    {
        return endPoint.regComuAndUserAndUserComu(localeToStr, usuarioCom);
    }

    @Override
    public Call<Boolean> regComuAndUserComu(String accessToken, UsuarioComunidad usuarioCom)
    {
        return endPoint.regComuAndUserComu(accessToken, usuarioCom);
    }

    @Override
    public Call<Boolean> regUserAndUserComu(String localeToStr, UsuarioComunidad userCom)
    {
        return endPoint.regUserAndUserComu(localeToStr, userCom);
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
            Response<Integer> response = deleteUserComu(tkCacher.checkBearerTokenInCache(), comunidadId).execute();
            return httpInitializer.get().getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    public List<Comunidad> getComusByUser() throws UiException
    {
        Timber.d("getComusByUser()");
        try {
            Response<List<Comunidad>> response = getComusByUser(tkCacher.checkBearerTokenInCache()).execute();
            return httpInitializer.get().getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    public UsuarioComunidad getUserComuByUserAndComu(long comunidadId) throws UiException
    {
        Timber.d("getUserComuByUserAndComu()");
        try {
            Response<UsuarioComunidad> response = getUserComuByUserAndComu(tkCacher.checkBearerTokenInCache(), comunidadId).execute();
            return httpInitializer.get().getResponseBody(response);
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
            Response<Boolean> response = isOldestOrAdmonUserComu(tkCacher.checkBearerTokenInCache(), comunidadId).execute();
            return httpInitializer.get().getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    public int modifyComuData(Comunidad comunidad) throws UiException
    {
        Timber.d("modifyComuData()");
        try {
            Response<Integer> response = modifyComuData(tkCacher.checkBearerTokenInCache(), comunidad).execute();
            return httpInitializer.get().getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    public int modifyUserComu(UsuarioComunidad userComu) throws UiException
    {
        Timber.d("modifyUserComu()");
        try {
            Response<Integer> response = modifyUserComu(tkCacher.checkBearerTokenInCache(), userComu).execute();
            return httpInitializer.get().getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    public Call<Boolean> regComuAndUserAndUserComu(UsuarioComunidad usuarioCom)
    {
        Timber.d(("regComuAndUserAndUserComu()"));
        return endPoint.regComuAndUserAndUserComu(getDeviceLanguage(), usuarioCom);
    }

    public boolean regComuAndUserComu(UsuarioComunidad usuarioComunidad) throws UiException
    {
        Timber.d("regComuAndUserComu()");
        try {
            Response<Boolean> response = regComuAndUserComu(tkCacher.checkBearerTokenInCache(), usuarioComunidad).execute();
            return httpInitializer.get().getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }

    }

    public Call<Boolean> regUserAndUserComu(UsuarioComunidad userCom)
    {
        Timber.d("regUserAndUserComu()");
        return endPoint.regUserAndUserComu(getDeviceLanguage(), userCom);
    }

    public int regUserComu(UsuarioComunidad usuarioComunidad) throws UiException
    {
        Timber.d("regUserComu()");
        try {
            Response<Integer> response = regUserComu(tkCacher.checkBearerTokenInCache(), usuarioComunidad).execute();
            return httpInitializer.get().getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    public List<UsuarioComunidad> seeUserComusByComu(long idComunidad) throws UiException
    {
        Timber.d("seeUserComusByComu()");
        try {
            Response<List<UsuarioComunidad>> response = seeUserComusByComu(tkCacher.checkBearerTokenInCache(), idComunidad).execute();
            return httpInitializer.get().getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    public List<UsuarioComunidad> seeUserComusByUser() throws UiException
    {
        Timber.d("seeUserComusByUser()");
        try {
            Response<List<UsuarioComunidad>> response = seeUserComusByUser(tkCacher.checkBearerTokenInCache()).execute();
            return httpInitializer.get().getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }
}
