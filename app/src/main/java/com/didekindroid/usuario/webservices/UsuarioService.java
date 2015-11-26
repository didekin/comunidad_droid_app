package com.didekindroid.usuario.webservices;

import android.util.Log;

import com.didekin.common.exception.InServiceException;
import com.didekin.serviceone.controller.UsuarioEndPoints;
import com.didekin.serviceone.domain.Comunidad;
import com.didekin.serviceone.domain.Usuario;
import com.didekin.serviceone.domain.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.UiException;

import java.util.List;

import retrofit.http.Header;
import retrofit.http.Path;

import static com.didekin.common.RetrofitRestBuilder.BUILDER;
import static com.didekin.common.exception.DidekinExceptionMsg.USER_NAME_NOT_FOUND;
import static com.didekindroid.DidekindroidApp.getBaseURL;
import static com.didekindroid.common.UiException.UiAction.SEARCH_COMU;
import static com.didekindroid.common.UiException.UiAction.TOKEN_TO_ERASE;
import static com.didekindroid.common.utils.UIutils.catchAuthenticationException;
import static com.didekindroid.common.utils.UIutils.checkBearerToken;

/**
 * User: pedro@didekin
 * Date: 07/06/15
 * Time: 15:06
 */
public enum UsuarioService implements UsuarioEndPoints {

    ServOne(BUILDER.getService(UsuarioEndPoints.class, getBaseURL())) {
        @Override
        public boolean deleteAccessToken(String accessToken, String oldAccessToken)
        {
            return ServOne.endPoint.deleteAccessToken(accessToken, oldAccessToken);
        }

        @Override
        public boolean deleteUser(String accessToken)
        {
            return ServOne.endPoint.deleteUser(accessToken);
        }

        @Override
        public int deleteUserComu(String accessToken, long comunidadId)
        {
            return ServOne.endPoint.deleteUserComu(accessToken, comunidadId);
        }

        @Override
        public Comunidad getComuData(String accessToken, long idComunidad)
        {
            return ServOne.endPoint.getComuData(accessToken, idComunidad);
        }

        @Override
        public List<Comunidad> getComusByUser(String accessToken)
        {
            return ServOne.endPoint.getComusByUser(accessToken);
        }

        @Override
        public UsuarioComunidad getUserComuByUserAndComu(@Header("Authorization") String accessToken, @Path("comunidadId") long comunidadId)
        {
            return ServOne.endPoint.getUserComuByUserAndComu(accessToken, comunidadId);
        }

        @Override
        public com.didekin.serviceone.domain.Usuario getUserData(String accessToken)
        {
            return ServOne.endPoint.getUserData(accessToken);
        }

        @Override
        public boolean isOldestUserComu(String accessToken, long comunidadId)
        {
            return ServOne.endPoint.isOldestUserComu(accessToken, comunidadId);
        }

        @Override
        public boolean login(String userName, String password)
        {
            return ServOne.endPoint.login(userName, password);
        }

        @Override
        public int modifyComuData(String currentAccessToken, Comunidad comunidad)
        {
            return ServOne.endPoint.modifyComuData(currentAccessToken, comunidad);
        }

        @Override
        public int modifyUserGcmToken(String accessToken, String gcmToken)
        {
            return ServOne.endPoint.modifyUserGcmToken(accessToken, gcmToken);
        }

        @Override
        public int modifyUser(String accessToken, com.didekin.serviceone.domain.Usuario usuario)
        {
            return ServOne.endPoint.modifyUser(accessToken, usuario);
        }

        @Override
        public int modifyUserComu(String accessToken, UsuarioComunidad userComu)
        {
            return ServOne.endPoint.modifyUserComu(accessToken, userComu);
        }

        @Override
        public int passwordChange(String accessToken, String newPassword)
        {
            return ServOne.endPoint.passwordChange(accessToken, newPassword);
        }

        @Override
        public boolean passwordSend(String userName)
        {
            Log.d(TAG, "passwordSend()");
            return ServOne.endPoint.passwordSend(userName);
        }

        @Override
        public boolean regComuAndUserAndUserComu(UsuarioComunidad usuarioCom)
        {
            Log.d(TAG, ("regComuAndUserAndUserComu()"));
            return ServOne.endPoint.regComuAndUserAndUserComu(usuarioCom);
        }

        @Override
        public boolean regComuAndUserComu(String accessToken, UsuarioComunidad usuarioCom)
        {
            return ServOne.endPoint.regComuAndUserComu(accessToken, usuarioCom);
        }

        @Override
        public boolean regUserAndUserComu(UsuarioComunidad userCom)
        {
            Log.d(TAG, "regUserAndUserComu()");
            return ServOne.endPoint.regUserAndUserComu(userCom);
        }

        @Override
        public int regUserComu(String accessToken, UsuarioComunidad usuarioComunidad)
        {
            return ServOne.endPoint.regUserComu(accessToken, usuarioComunidad);
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
        public List<Comunidad> searchComunidades(Comunidad comunidad)
        {
            Log.d(TAG, "searchComunidades()");
            return ServOne.endPoint.searchComunidades(comunidad);
        }

        @Override
        public List<UsuarioComunidad> seeUserComusByComu(String accessToken, long comunidadId)
        {
            return ServOne.endPoint.seeUserComusByComu(accessToken, comunidadId);
        }

        @Override
        public List<UsuarioComunidad> seeUserComusByUser(String accessToken)
        {
            return ServOne.endPoint.seeUserComusByUser(accessToken);
        }
    },;

    private static final String TAG = UsuarioService.class.getCanonicalName();

    private final UsuarioEndPoints endPoint;

    UsuarioService(UsuarioEndPoints retrofitEndPoint)
    {
        endPoint = retrofitEndPoint;
    }

// :::::::::::::::::::::::::::::::::::::::::::::::::::::
//                  CONVENIENCE METHODS
// :::::::::::::::::::::::::::::::::::::::::::::::::::::

    public boolean deleteAccessToken(String oldAccessToken) throws UiException
    {
        Log.d(TAG, "deleteAccessToken()");

        boolean isDeleted;
        try {
            isDeleted = deleteAccessToken(checkBearerToken(), oldAccessToken);
        } catch (InServiceException e) {
            throw new UiException(TOKEN_TO_ERASE, 0, e);
        }
        return isDeleted;
    }

    public boolean deleteUser() throws UiException
    {
        Log.d(TAG, "deleteUser()");
        boolean isDeleted = false;
        try {
            isDeleted = deleteUser(checkBearerToken());
        } catch (InServiceException e) {
            catchAuthenticationException(e);
        }
        return isDeleted;
    }


    public int deleteUserComu(long comunidadId) throws UiException
    {
        Log.d(TAG, "deleteUserComu()");

        int deleted = 0;
        try {
            deleted = deleteUserComu(checkBearerToken(), comunidadId);
        } catch (InServiceException e) {
            catchAuthenticationException(e);
        }
        return deleted;
    }

    public Comunidad getComuData(long idComunidad) throws UiException
    {
        Log.d(TAG, "getComuData()");

        Comunidad comunidad = null;
        try {
            comunidad = getComuData(checkBearerToken(), idComunidad);
        } catch (InServiceException e) {
            catchAuthenticationException(e);
        }
        return comunidad;
    }

    public List<Comunidad> getComusByUser() throws UiException
    {
        Log.d(TAG, "getComusByUser()");

        List<Comunidad> comusByUser = null;
        try {
            comusByUser = endPoint.getComusByUser(checkBearerToken());
        } catch (InServiceException e) {
            catchAuthenticationException(e);
        }
        return comusByUser;
    }

    public UsuarioComunidad getUserComuByUserAndComu(long comunidadId) throws UiException
    {
        Log.d(TAG, "getUserComuByUserAndComu()");

        UsuarioComunidad userComuByUserAndComu = null;
        try {
            userComuByUserAndComu = getUserComuByUserAndComu(checkBearerToken(), comunidadId);
        } catch (InServiceException e) {
            catchAuthenticationException(e);
        }
        return userComuByUserAndComu;
    }

    public com.didekin.serviceone.domain.Usuario getUserData() throws UiException
    {
        Log.d(TAG, ("getUserData()"));

        com.didekin.serviceone.domain.Usuario userData = null;
        try {
            userData = getUserData(checkBearerToken());
        } catch (InServiceException e) {
            catchAuthenticationException(e);
        }
        return userData;
    }

    public boolean isOldestUserComu(long comunidadId) throws UiException
    {
        Log.d(TAG, "isOldestUserComu()");

        boolean isOldestUserComu = false;
        try {
            isOldestUserComu = isOldestUserComu(checkBearerToken(), comunidadId);
        } catch (InServiceException e) {
            catchAuthenticationException(e);
        }
        return isOldestUserComu;
    }

    public boolean loginInternal(String userName, String password) throws UiException
    {
        Log.d(TAG, "loginInternal()");

        boolean isLoginOk = false;
        try {
            isLoginOk = login(userName, password);
        } catch (InServiceException e) {
            if (e.getHttpMessage().equals(USER_NAME_NOT_FOUND.getHttpMessage())) {
                throw new UiException(SEARCH_COMU, R.string.user_without_signedUp, null);
            }
        }
        return isLoginOk;
    }

    public int modifyComuData(Comunidad comunidad) throws UiException
    {
        Log.d(TAG, "modifyComuData()");

        int modifyComuData = 0;
        try {
            modifyComuData = modifyComuData(checkBearerToken(), comunidad);
        } catch (InServiceException e) {
            catchAuthenticationException(e);
        }
        return modifyComuData;
    }

    public int modifyUserGcmToken(String gcmToken) throws InServiceException, UiException
    {
        Log.d(TAG, "modifyUserGcmToken()");
        return modifyUserGcmToken(checkBearerToken(), gcmToken);
    }

    public int modifyUser(Usuario usuario) throws UiException
    {
        Log.d(TAG, "modifyUser()");

        int modifyUser = 0;
        try {
            modifyUser = modifyUser(checkBearerToken(), usuario);
        } catch (InServiceException e) {
            catchAuthenticationException(e);
        }
        return modifyUser;
    }

    public int modifyUserComu(UsuarioComunidad userComu) throws UiException
    {
        Log.d(TAG, "modifyUserComu()");

        int modifyUserComu = 0;
        try {
            modifyUserComu = modifyUserComu(checkBearerToken(), userComu);
        } catch (InServiceException e) {
            catchAuthenticationException(e);
        }
        return modifyUserComu;
    }

    public int passwordChange(String newPassword) throws UiException
    {
        Log.d(TAG, "passwordChange()");

        int passwordChange = 0;
        try {
            passwordChange = passwordChange(checkBearerToken(), newPassword);
        } catch (InServiceException e) {
            catchAuthenticationException(e);
        }
        return passwordChange;
    }

    public boolean regComuAndUserComu(UsuarioComunidad usuarioComunidad) throws UiException
    {
        Log.d(TAG, "regComuAndUserComu()");

        boolean isRegistered = false;
        try {
            isRegistered = regComuAndUserComu(checkBearerToken(), usuarioComunidad);
        } catch (InServiceException e) {
            catchAuthenticationException(e);
        }
        return isRegistered;
    }

    public int regUserComu(UsuarioComunidad usuarioComunidad) throws UiException
    {
        Log.d(TAG, "regUserComu()");

        int regUserComu = 0;
        try {
            regUserComu = regUserComu(checkBearerToken(), usuarioComunidad);
        } catch (InServiceException e) {
            catchAuthenticationException(e);
        }
        return regUserComu;
    }

    public List<UsuarioComunidad> seeUserComusByComu(long idComunidad) throws UiException
    {
        Log.d(TAG, "seeUserComusByComu()");

        List<UsuarioComunidad> usuarioComunidadList = null;
        try {
            usuarioComunidadList = seeUserComusByComu(checkBearerToken(), idComunidad);
        } catch (InServiceException e) {
            catchAuthenticationException(e);
        }
        return usuarioComunidadList;
    }

    public List<UsuarioComunidad> seeUserComusByUser() throws UiException
    {
        Log.d(TAG, "seeUserComusByUser()");

        List<UsuarioComunidad> userComuList = null;

        try {
            userComuList = seeUserComusByUser(checkBearerToken());
        } catch (InServiceException e) {
            catchAuthenticationException(e);
        }
        return userComuList;
    }

//    ============================ HELPER METHODS ============================

}