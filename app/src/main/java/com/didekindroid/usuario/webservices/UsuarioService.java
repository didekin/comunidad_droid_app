package com.didekindroid.usuario.webservices;

import com.didekin.common.controller.RetrofitHandler;
import com.didekin.usuario.controller.GcmTokenWrapper;
import com.didekin.usuario.controller.UsuarioEndPoints;
import com.didekin.usuario.dominio.Comunidad;
import com.didekin.usuario.dominio.Usuario;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.common.webservices.JksInAndroidApp;

import java.io.EOFException;
import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

import static com.didekin.common.exception.ErrorBean.GENERIC_ERROR;
import static com.didekindroid.DidekindroidApp.getBaseURL;
import static com.didekindroid.DidekindroidApp.getHttpTimeOut;
import static com.didekindroid.DidekindroidApp.getJksPassword;
import static com.didekindroid.DidekindroidApp.getJksResourceId;
import static com.didekindroid.common.utils.UIutils.checkBearerToken;

/**
 * User: pedro@didekin
 * Date: 07/06/15
 * Time: 15:06
 */
@SuppressWarnings("unused")
public final class UsuarioService implements UsuarioEndPoints {

    public static final RetrofitHandler retrofitHandler = new RetrofitHandler(getBaseURL(),new JksInAndroidApp(getJksPassword(), getJksResourceId()),getHttpTimeOut());
    public static final UsuarioService ServOne = new UsuarioService();
    private final UsuarioEndPoints endPoint;

    private UsuarioService()
    {
        endPoint = retrofitHandler.getService(UsuarioEndPoints.class);
    }

//  ================================== UsuarioEndPoints implementation ============================

    @Override
    public Call<Boolean> deleteAccessToken(String accessToken, String oldAccessToken)
    {
        return endPoint.deleteAccessToken(accessToken, oldAccessToken);
    }

    @Override
    public Call<Boolean> deleteUser(String accessToken)
    {
        return endPoint.deleteUser(accessToken);
    }

    @Override
    public Call<Integer> deleteUserComu(String accessToken, long comunidadId)
    {
        return endPoint.deleteUserComu(accessToken, comunidadId);
    }

    @Override
    public Call<Comunidad> getComuData(String accessToken, long idComunidad)
    {
        return endPoint.getComuData(accessToken, idComunidad);
    }

    @Override
    public Call<List<Comunidad>> getComusByUser(String accessToken)
    {
        return endPoint.getComusByUser(accessToken);
    }

    @Override
    public Call<GcmTokenWrapper> getGcmToken(String accessToken)
    {
        return endPoint.getGcmToken(accessToken);
    }

    @Override
    public Call<UsuarioComunidad> getUserComuByUserAndComu(String accessToken, long comunidadId)
    {
        return endPoint.getUserComuByUserAndComu(accessToken, comunidadId);
    }

    @Override
    public Call<Usuario> getUserData(String accessToken)
    {
        return endPoint.getUserData(accessToken);
    }

    @Override
    public Call<Boolean> isOldestOrAdmonUserComu(String accessToken, long comunidadId)
    {
        return endPoint.isOldestOrAdmonUserComu(accessToken, comunidadId);
    }

    @Override
    public Call<Boolean> login(String userName, String password)
    {
        return endPoint.login(userName, password);
    }

    @Override
    public Call<Integer> modifyComuData(String currentAccessToken, Comunidad comunidad)
    {
        return endPoint.modifyComuData(currentAccessToken, comunidad);
    }

    @Override
    public Call<Integer> modifyUserGcmToken(String accessToken, String gcmToken)
    {
        return endPoint.modifyUserGcmToken(accessToken, gcmToken);
    }

    @Override
    public Call<Integer> modifyUser(String accessToken, Usuario usuario)
    {
        return endPoint.modifyUser(accessToken, usuario);
    }

    @Override
    public Call<Integer> modifyUserComu(String accessToken, UsuarioComunidad userComu)
    {
        return endPoint.modifyUserComu(accessToken, userComu);
    }

    @Override
    public Call<Integer> passwordChange(String accessToken, String newPassword)
    {
        return endPoint.passwordChange(accessToken, newPassword);
    }

    @Override
    public Call<Boolean> passwordSend(String userName)
    {
        Timber.d("passwordSend()");
        return endPoint.passwordSend(userName);
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

    /**
     * An object comunidad is used as search criterium, with the fields:
     * -- tipoVia.
     * -- nombreVia.
     * -- numero.
     * -- sufijoNumero (it can be an empty string).
     * -- municipio with codInProvincia and provinciaId.
     */
    @Override
    public Call<List<Comunidad>> searchComunidades(Comunidad comunidad)
    {
        Timber.d("searchComunidades()");
        return endPoint.searchComunidades(comunidad);
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

    public boolean deleteAccessToken(String oldAccessToken) throws UiException
    {
        Timber.d("deleteAccessToken()");

        try {
            Response<Boolean> response = deleteAccessToken(checkBearerToken(), oldAccessToken).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public boolean deleteUser() throws UiException
    {
        Timber.d("deleteUser()");
        try {
            Response<Boolean> response = deleteUser(checkBearerToken()).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }


    public int deleteUserComu(long comunidadId) throws UiException
    {
        Timber.d("deleteUserComu()");
        try {
            Response<Integer> response = deleteUserComu(checkBearerToken(), comunidadId).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public Comunidad getComuData(long idComunidad) throws UiException
    {
        Timber.d("getComuData()");

        try {
            Response<Comunidad> response = getComuData(checkBearerToken(), idComunidad).execute();
            return getResponseBody(response);
        } catch (EOFException eo) {
            return null;
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public List<Comunidad> getComusByUser() throws UiException
    {
        Timber.d("getComusByUser()");
        try {
            Response<List<Comunidad>> response = getComusByUser(checkBearerToken()).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public String getGcmToken() throws UiException
    {
        Timber.d("getGcmToken()");
        try {
            Response<GcmTokenWrapper> response = getGcmToken(checkBearerToken()).execute();
            return getResponseBody(response).getToken();
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public UsuarioComunidad getUserComuByUserAndComu(long comunidadId) throws UiException
    {
        Timber.d("getUserComuByUserAndComu()");
        try {
            Response<UsuarioComunidad> response = getUserComuByUserAndComu(checkBearerToken(), comunidadId).execute();
            return getResponseBody(response);
        } catch (EOFException eo) {
            return null;
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public Usuario getUserData() throws UiException
    {
        Timber.d(("getUserData()"));
        try {
            Response<Usuario> response = getUserData(checkBearerToken()).execute();
            return getResponseBody(response);
        } catch (EOFException eo) {
            return null;
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public boolean isOldestOrAdmonUserComu(long comunidadId) throws UiException
    {
        Timber.d("isOldestOrAdmonUserComu()");
        try {
            Response<Boolean> response = isOldestOrAdmonUserComu(checkBearerToken(), comunidadId).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public boolean loginInternal(String userName, String password) throws UiException
    {
        Timber.d("loginInternal()");
        try {
            Response<Boolean> response = login(userName, password).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public int modifyComuData(Comunidad comunidad) throws UiException
    {
        Timber.d("modifyComuData()");
        try {
            Response<Integer> response = modifyComuData(checkBearerToken(), comunidad).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public int modifyUserGcmToken(String gcmToken) throws UiException
    {
        Timber.d("modifyUserGcmToken()");
        try {
            Response<Integer> response = modifyUserGcmToken(checkBearerToken(), gcmToken).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public int modifyUser(Usuario usuario) throws UiException
    {
        Timber.d("modifyUser()");
        try {
            Response<Integer> response = modifyUser(checkBearerToken(), usuario).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public int modifyUserComu(UsuarioComunidad userComu) throws UiException
    {
        Timber.d("modifyUserComu()");
        try {
            Response<Integer> response = modifyUserComu(checkBearerToken(), userComu).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public int passwordChange(String newPassword) throws UiException
    {
        Timber.d("passwordChange()");
        try {
            Response<Integer> response = passwordChange(checkBearerToken(), newPassword).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public boolean regComuAndUserComu(UsuarioComunidad usuarioComunidad) throws UiException
    {
        Timber.d("regComuAndUserComu()");
        try {
            Response<Boolean> response = regComuAndUserComu(checkBearerToken(), usuarioComunidad).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public int regUserComu(UsuarioComunidad usuarioComunidad) throws UiException
    {
        Timber.d("regUserComu()");
        try {
            Response<Integer> response = regUserComu(checkBearerToken(), usuarioComunidad).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public List<UsuarioComunidad> seeUserComusByComu(long idComunidad) throws UiException
    {
        Timber.d("seeUserComusByComu()");
        try {
            Response<List<UsuarioComunidad>> response = seeUserComusByComu(checkBearerToken(), idComunidad).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public List<UsuarioComunidad> seeUserComusByUser() throws UiException
    {
        Timber.d("seeUserComusByUser()");
        try {
            Response<List<UsuarioComunidad>> response = seeUserComusByUser(checkBearerToken()).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

//  =============================================================================
//                          HELPER METHODS
//  =============================================================================

    private static <T> T getResponseBody(Response<T> response) throws UiException, IOException
    {
        if (response.isSuccessful()) {
            return response.body();
        } else {
            throw new UiException(retrofitHandler.getErrorBean(response));
        }
    }
}