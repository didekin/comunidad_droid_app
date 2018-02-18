package com.didekindroid.lib_one.usuario.dao;

import com.didekindroid.lib_one.api.HttpInitializerIf;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.security.IdentityCacherIf;
import com.didekindroid.lib_one.security.SecInitializerIf;
import com.didekinlib.http.auth.SpringOauthToken;
import com.didekinlib.http.exception.ErrorBean;
import com.didekinlib.http.usuario.UsuarioEndPoints;
import com.didekinlib.model.usuario.GcmTokenWrapper;
import com.didekinlib.model.usuario.Usuario;

import java.io.EOFException;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

import static com.didekindroid.lib_one.HttpInitializer.httpInitializer;
import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.lib_one.util.Device.getDeviceLanguage;
import static com.didekinlib.http.exception.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;


/**
 * User: pedro@didekin
 * Date: 07/06/15
 * Time: 15:06
 */
@SuppressWarnings("WeakerAccess")
public final class UsuarioDao implements UsuarioEndPoints, UsuarioDaoIf {

    public static final UsuarioDaoIf usuarioDaoRemote = new UsuarioDao(secInitializer.get(), httpInitializer.get());
    private final IdentityCacherIf tkCacher;
    private final UsuarioEndPoints endPoint;

    private UsuarioDao(SecInitializerIf secInitializerIn, HttpInitializerIf httpInitializerIn)
    {
        endPoint = httpInitializerIn.getHttpHandler().getService(UsuarioEndPoints.class);
        tkCacher = secInitializerIn.getTkCacher();
    }

    public IdentityCacherIf getTkCacher()
    {
        return tkCacher;
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
    public Call<Integer> modifyUser(String deviceLanguage, String accessToken, Usuario usuario)
    {
        return endPoint.modifyUser(deviceLanguage, accessToken, usuario);
    }

    @Override
    public Call<Integer> passwordChange(String accessToken, String newPassword)
    {
        return endPoint.passwordChange(accessToken, newPassword);
    }

    @Override
    public Call<Boolean> passwordSend(String deviceLanguage, String userName)
    {
        Timber.d("passwordSend()");
        return endPoint.passwordSend(deviceLanguage, userName);
    }

//  =============================================================================
//                          CONVENIENCE METHODS
//  =============================================================================

    @Override
    public boolean deleteAccessToken(String oldAccessToken) throws UiException
    {
        Timber.d("deleteAccessToken(), Thread: %s", Thread.currentThread().getName());

        try {
            Response<Boolean> response = deleteAccessToken(tkCacher.checkBearerTokenInCache(), oldAccessToken).execute();
            return httpInitializer.get().getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    @Override
    public boolean deleteUser() throws UiException
    {
        Timber.d("deleteUser(), Thread: %s", Thread.currentThread().getName());
        try {
            Response<Boolean> response = deleteUser(tkCacher.checkBearerTokenInCache()).execute();
            return httpInitializer.get().getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    @Override
    public String getGcmToken() throws UiException
    {
        Timber.d("getGcmToken(), Thread: %s", Thread.currentThread().getName());
        try {
            Response<GcmTokenWrapper> response = getGcmToken(tkCacher.checkBearerTokenInCache()).execute();
            return httpInitializer.get().getResponseBody(response).getToken();
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    @Override
    public Usuario getUserData() throws UiException
    {
        Timber.d("getUserData(), Thread: %s", Thread.currentThread().getName());
        try {
            Response<Usuario> response = getUserData(tkCacher.checkBearerTokenInCache()).execute();
            return httpInitializer.get().getResponseBody(response);
        } catch (EOFException eo) {
            return null;
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    /**
     * @return false if the userName exists but the password doesn't match that in the data base.
     * @throws UiException if USER_NAME_NOT_FOUND or GENERIC_INTERNAL_ERROR.
     */
    @Override
    public boolean loginInternal(String userName, String password) throws UiException
    {
        Timber.d("loginInternal(), Thread: %s", Thread.currentThread().getName());
        try {
            Response<Boolean> response = login(userName, password).execute();
            return httpInitializer.get().getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    @Override
    public int modifyUserGcmToken(String gcmToken) throws UiException
    {
        Timber.d("modifyUserGcmToken(), Thread: %s", Thread.currentThread().getName());
        try {
            Response<Integer> response = modifyUserGcmToken(tkCacher.checkBearerTokenInCache(), gcmToken).execute();
            return httpInitializer.get().getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    @Override
    public int modifyUserWithToken(SpringOauthToken oauthToken, Usuario usuario) throws UiException
    {
        Timber.d("modifyUserWithToken(), Thread: %s", Thread.currentThread().getName());
        try {
            return httpInitializer.get().getResponseBody(modifyUser(getDeviceLanguage(), tkCacher.checkBearerToken(oauthToken), usuario).execute());
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    @Override
    public int passwordChange(SpringOauthToken oldOauthToken, String newPassword) throws UiException
    {
        Timber.d("passwordChange(), Thread: %s", Thread.currentThread().getName());
        try {
            Response<Integer> response = passwordChange(tkCacher.checkBearerToken(oldOauthToken), newPassword).execute();
            return httpInitializer.get().getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    @Override
    public boolean sendPassword(String email) throws UiException
    {
        Timber.d("sendPassword(), Thread: %s", Thread.currentThread().getName());
        try {
            return httpInitializer.get().getResponseBody(passwordSend(getDeviceLanguage(), email).execute());
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }
}