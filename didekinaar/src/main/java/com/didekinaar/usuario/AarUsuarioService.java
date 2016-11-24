package com.didekinaar.usuario;

import com.didekin.usuario.GcmTokenWrapper;
import com.didekin.usuario.Usuario;
import com.didekin.usuario.UsuarioEndPoints;
import com.didekinaar.exception.UiAarException;
import com.didekinaar.utils.AarServiceUtil;

import java.io.EOFException;
import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

import static com.didekin.common.exception.ErrorBean.GENERIC_ERROR;
import static com.didekinaar.PrimalCreator.creator;
import static com.didekinaar.utils.UIutils.checkBearerToken;

/**
 * User: pedro@didekin
 * Date: 07/06/15
 * Time: 15:06
 */
@SuppressWarnings("WeakerAccess")
public final class AarUsuarioService implements UsuarioEndPoints {

    public static final AarUsuarioService AarUserServ = new AarUsuarioService();
    private final UsuarioEndPoints endPoint;

    private AarUsuarioService()
    {
        endPoint = creator.get().getRetrofitHandler().getService(UsuarioEndPoints.class);
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

    public boolean deleteAccessToken(String oldAccessToken) throws UiAarException
    {
        Timber.d("deleteAccessToken()");

        try {
            Response<Boolean> response = deleteAccessToken(checkBearerToken(), oldAccessToken).execute();
            return AarServiceUtil.getResponseBody(response);
        } catch (IOException e) {
            throw new UiAarException(GENERIC_ERROR);
        }
    }

    public boolean deleteUser() throws UiAarException
    {
        Timber.d("deleteUser()");
        try {
            Response<Boolean> response = deleteUser(checkBearerToken()).execute();
            return AarServiceUtil.getResponseBody(response);
        } catch (IOException e) {
            throw new UiAarException(GENERIC_ERROR);
        }
    }

    public String getGcmToken() throws UiAarException
    {
        Timber.d("getGcmToken()");
        try {
            Response<GcmTokenWrapper> response = getGcmToken(checkBearerToken()).execute();
            return AarServiceUtil.getResponseBody(response).getToken();
        } catch (IOException e) {
            throw new UiAarException(GENERIC_ERROR);
        }
    }

    public Usuario getUserData() throws UiAarException
    {
        Timber.d(("getUserData()"));
        try {
            Response<Usuario> response = getUserData(checkBearerToken()).execute();
            return AarServiceUtil.getResponseBody(response);
        } catch (EOFException eo) {
            return null;
        } catch (IOException e) {
            throw new UiAarException(GENERIC_ERROR);
        }
    }

    public boolean loginInternal(String userName, String password) throws UiAarException
    {
        Timber.d("loginInternal()");
        try {
            Response<Boolean> response = login(userName, password).execute();
            return AarServiceUtil.getResponseBody(response);
        } catch (IOException e) {
            throw new UiAarException(GENERIC_ERROR);
        }
    }

    public int modifyUserGcmToken(String gcmToken) throws UiAarException
    {
        Timber.d("modifyUserGcmToken()");
        try {
            Response<Integer> response = modifyUserGcmToken(checkBearerToken(), gcmToken).execute();
            return AarServiceUtil.getResponseBody(response);
        } catch (IOException e) {
            throw new UiAarException(GENERIC_ERROR);
        }
    }

    public int modifyUser(Usuario usuario) throws UiAarException
    {
        Timber.d("modifyUser()");
        try {
            Response<Integer> response = modifyUser(checkBearerToken(), usuario).execute();
            return AarServiceUtil.getResponseBody(response);
        } catch (IOException e) {
            throw new UiAarException(GENERIC_ERROR);
        }
    }

    public int passwordChange(String newPassword) throws UiAarException
    {
        Timber.d("passwordChange()");
        try {
            Response<Integer> response = passwordChange(checkBearerToken(), newPassword).execute();
            return AarServiceUtil.getResponseBody(response);
        } catch (IOException e) {
            throw new UiAarException(GENERIC_ERROR);
        }
    }
}