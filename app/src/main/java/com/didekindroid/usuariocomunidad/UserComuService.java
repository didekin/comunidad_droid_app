package com.didekindroid.usuariocomunidad;

import com.didekin.comunidad.Comunidad;
import com.didekin.usuariocomunidad.UsuarioComunidad;
import com.didekin.usuariocomunidad.UsuarioComunidadEndPoints;
import com.didekinaar.exception.UiException;
import com.didekinaar.utils.AarServiceUtil;
import com.didekindroid.exception.UiAppException;

import java.io.EOFException;
import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

import static com.didekin.common.exception.ErrorBean.GENERIC_ERROR;
import static com.didekinaar.PrimalCreator.creator;
import static com.didekinaar.utils.AarServiceUtil.getResponseBody;
import static com.didekinaar.utils.UIutils.checkBearerToken;

/**
 * User: pedro@didekin
 * Date: 21/11/16
 * Time: 19:15
 */

@SuppressWarnings("WeakerAccess")
public final class UserComuService implements UsuarioComunidadEndPoints {

    public static final UserComuService AppUserComuServ = new UserComuService();
    private final UsuarioComunidadEndPoints endPoint;

    private UserComuService()
    {
        endPoint = creator.get().getRetrofitHandler().getService(UsuarioComunidadEndPoints.class);
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

    public int deleteUserComu(long comunidadId) throws UiAppException
    {
        Timber.d("deleteUserComu()");
        try {
            Response<Integer> response = deleteUserComu(checkBearerToken(), comunidadId).execute();
            return getResponseBody(response);
        } catch (IOException | UiException e) {
            throw new UiAppException(GENERIC_ERROR);
        }
    }

    public List<Comunidad> getComusByUser() throws UiAppException
    {
        Timber.d("getComusByUser()");
        try {
            Response<List<Comunidad>> response = getComusByUser(checkBearerToken()).execute();
            return AarServiceUtil.getResponseBody(response);
        } catch (IOException | UiException e) {
            throw new UiAppException(GENERIC_ERROR);
        }
    }

    public UsuarioComunidad getUserComuByUserAndComu(long comunidadId) throws UiAppException
    {
        Timber.d("getUserComuByUserAndComu()");
        try {
            Response<UsuarioComunidad> response = getUserComuByUserAndComu(checkBearerToken(), comunidadId).execute();
            return AarServiceUtil.getResponseBody(response);
        } catch (EOFException eo) {
            return null;
        } catch (IOException | UiException e) {
            throw new UiAppException(GENERIC_ERROR);
        }
    }

    public boolean isOldestOrAdmonUserComu(long comunidadId) throws UiAppException
    {
        Timber.d("isOldestOrAdmonUserComu()");
        try {
            Response<Boolean> response = isOldestOrAdmonUserComu(checkBearerToken(), comunidadId).execute();
            return AarServiceUtil.getResponseBody(response);
        } catch (IOException | UiException e) {
            throw new UiAppException(GENERIC_ERROR);
        }
    }

    public int modifyComuData(Comunidad comunidad) throws UiAppException
    {
        Timber.d("modifyComuData()");
        try {
            Response<Integer> response = modifyComuData(checkBearerToken(), comunidad).execute();
            return AarServiceUtil.getResponseBody(response);
        } catch (IOException | UiException e) {
            throw new UiAppException(GENERIC_ERROR);
        }
    }

    public int modifyUserComu(UsuarioComunidad userComu) throws UiAppException
    {
        Timber.d("modifyUserComu()");
        try {
            Response<Integer> response = modifyUserComu(checkBearerToken(), userComu).execute();
            return AarServiceUtil.getResponseBody(response);
        } catch (IOException | UiException e) {
            throw new UiAppException(GENERIC_ERROR);
        }
    }

    public boolean regComuAndUserComu(UsuarioComunidad usuarioComunidad) throws UiAppException
    {
        Timber.d("regComuAndUserComu()");
        try {
            Response<Boolean> response = regComuAndUserComu(checkBearerToken(), usuarioComunidad).execute();
            return AarServiceUtil.getResponseBody(response);
        } catch (IOException | UiException e) {
            throw new UiAppException(GENERIC_ERROR);
        }
    }

    public int regUserComu(UsuarioComunidad usuarioComunidad) throws UiAppException
    {
        Timber.d("regUserComu()");
        try {
            Response<Integer> response = regUserComu(checkBearerToken(), usuarioComunidad).execute();
            return AarServiceUtil.getResponseBody(response);
        } catch (IOException | UiException e) {
            throw new UiAppException(GENERIC_ERROR);
        }
    }

    public List<UsuarioComunidad> seeUserComusByComu(long idComunidad) throws UiAppException
    {
        Timber.d("seeUserComusByComu()");
        try {
            Response<List<UsuarioComunidad>> response = seeUserComusByComu(checkBearerToken(), idComunidad).execute();
            return AarServiceUtil.getResponseBody(response);
        } catch (IOException | UiException e) {
            throw new UiAppException(GENERIC_ERROR);
        }
    }

    public List<UsuarioComunidad> seeUserComusByUser() throws UiAppException
    {
        Timber.d("seeUserComusByUser()");
        try {
            Response<List<UsuarioComunidad>> response = seeUserComusByUser(checkBearerToken()).execute();
            return AarServiceUtil.getResponseBody(response);
        } catch (IOException | UiException e) {
            throw new UiAppException(GENERIC_ERROR);
        }
    }
}
