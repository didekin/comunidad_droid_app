package com.didekindroid.usuario.dao;

import com.didekindroid.exception.UiException;
import com.didekinlib.http.ErrorBean;
import com.didekinlib.http.retrofit.UsuarioEndPoints;
import com.didekinlib.model.usuario.GcmTokenWrapper;
import com.didekinlib.model.usuario.Usuario;

import java.io.EOFException;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

import static com.didekindroid.AppInitializer.creator;
import static com.didekindroid.util.DaoUtil.getResponseBody;
import static com.didekindroid.util.UIutils.checkBearerToken;
import static com.didekinlib.http.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;

/**
 * User: pedro@didekin
 * Date: 07/06/15
 * Time: 15:06
 */
@SuppressWarnings("WeakerAccess")
public final class UsuarioDaoRemote implements UsuarioEndPoints, UsuarioDao {

    public static final UsuarioDao usuarioDao = new UsuarioDaoRemote(creator.get().getRetrofitHandler().getService(UsuarioEndPoints.class));
    private final UsuarioEndPoints endPoint;

    private UsuarioDaoRemote(UsuarioEndPoints endPoints)
    {
        endPoint = endPoints;
    }

    @Override
    public UsuarioEndPoints getEndPoint()
    {
        return endPoint;
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
    public Call<GcmTokenWrapper> getGcmToken(String accessToken)
    {
        return endPoint.getGcmToken(accessToken);
    }

    @Override
    public Call<Usuario> getUserData(String accessToken)
    {
        return endPoint.getUserData(accessToken);
    }

    @Override
    public Call<Boolean> login(String userName, String password)
    {
        return endPoint.login(userName, password);
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

//  =============================================================================
//                          CONVENIENCE METHODS
//  =============================================================================

    @Override
    public boolean deleteAccessToken(String oldAccessToken) throws UiException
    {
        Timber.d("deleteAccessToken()");

        try {
            Response<Boolean> response = deleteAccessToken(checkBearerToken(), oldAccessToken).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    @Override
    public boolean deleteUser() throws UiException
    {
        Timber.d("deleteUser()");
        try {
            Response<Boolean> response = deleteUser(checkBearerToken()).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    @Override
    public String getGcmToken() throws UiException
    {
        Timber.d("getGcmToken()");
        try {
            Response<GcmTokenWrapper> response = getGcmToken(checkBearerToken()).execute();
            return getResponseBody(response).getToken();
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    @Override
    public Usuario getUserData() throws UiException
    {
        Timber.d(("getUserData()"));
        try {
            Response<Usuario> response = getUserData(checkBearerToken()).execute();
            return getResponseBody(response);
        } catch (EOFException eo) {
            return null;
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    /**
     * @return  false if the userName exists but the password doesn't match that in the data base.
     * @throws  UiException if USER_NAME_NOT_FOUND or GENERIC_INTERNAL_ERROR.
     */
    @Override
    public boolean loginInternal(String userName, String password) throws UiException
    {
        Timber.d("loginInternal()");
        try {
            Response<Boolean> response = login(userName, password).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    @Override
    public int modifyUserGcmToken(String gcmToken) throws UiException
    {
        Timber.d("modifyUserGcmToken()");
        try {
            Response<Integer> response = modifyUserGcmToken(checkBearerToken(), gcmToken).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    @Override
    public int modifyUser(Usuario usuario) throws UiException
    {
        Timber.d("modifyUser()");
        try {
            Response<Integer> response = modifyUser(checkBearerToken(), usuario).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    @Override
    public int passwordChange(String newPassword) throws UiException
    {
        Timber.d("passwordChange()");
        try {
            Response<Integer> response = passwordChange(checkBearerToken(), newPassword).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    @Override
    public boolean sendPassword(String email) throws UiException
    {
        Timber.d("sendPassword()");
        try {
            return getResponseBody(passwordSend(email).execute());
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }
}