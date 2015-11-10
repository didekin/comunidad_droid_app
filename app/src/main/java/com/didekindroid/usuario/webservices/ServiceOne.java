package com.didekindroid.usuario.webservices;

import android.util.Log;

import com.didekin.common.exception.InServiceException;
import com.didekin.serviceone.controller.ServiceOneEndPoints;
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
import static com.didekin.common.exception.DidekinExceptionMsg.isMessageToLogin;
import static com.didekindroid.DidekindroidApp.getBaseURL;
import static com.didekindroid.common.UiException.UiAction.LOGIN;
import static com.didekindroid.common.UiException.UiAction.SEARCH_COMU;
import static com.didekindroid.common.TokenHandler.TKhandler;

/**
 * User: pedro@didekin
 * Date: 07/06/15
 * Time: 15:06
 */
public enum ServiceOne implements ServiceOneEndPoints {

    ServOne(BUILDER.getService(ServiceOneEndPoints.class, getBaseURL())) {
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
        public Usuario getUserData(String accessToken)
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
        public int modifyUser(String accessToken, Usuario usuario)
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

    private static final String TAG = ServiceOne.class.getCanonicalName();

    private final ServiceOneEndPoints endPoint;

    ServiceOne(ServiceOneEndPoints retrofitEndPoint)
    {
        endPoint = retrofitEndPoint;
    }

// :::::::::::::::::::::::::::::::::::::::::::::::::::::
//                  CONVENIENCE METHODS
// :::::::::::::::::::::::::::::::::::::::::::::::::::::

    public boolean deleteAccessToken(String oldAccessToken)
    {
        Log.d(TAG, "deleteAccessToken()");
        return deleteAccessToken(TKhandler.doBearerAccessTkHeader(), oldAccessToken);
    }

    public boolean deleteUser()
    {
        Log.d(TAG, "deleteUser()");
        return deleteUser(TKhandler.doBearerAccessTkHeader());
    }


    public int deleteUserComu(long comunidadId)
    {
        Log.d(TAG, "deleteUserComu()");
        return deleteUserComu(TKhandler.doBearerAccessTkHeader(), comunidadId);
    }

    public Comunidad getComuData(long idComunidad)
    {
        Log.d(TAG, "getComuData()");
        return getComuData(TKhandler.doBearerAccessTkHeader(), idComunidad);
    }

    public List<Comunidad> getComusByUser()
    {
        Log.d(TAG, "getComusByUser()");
        String bearerAccessTkHeader = TKhandler.doBearerAccessTkHeader();
        return (bearerAccessTkHeader != null ? endPoint.getComusByUser(bearerAccessTkHeader) : null);
    }

    public UsuarioComunidad getUserComuByUserAndComu(long comunidadId)
    {
        Log.d(TAG,"getUserComuByUserAndComu()");
        return getUserComuByUserAndComu(TKhandler.doBearerAccessTkHeader(), comunidadId);
    }

    public Usuario getUserData()
    {
        Log.d(TAG, ("getUserData()"));
        return getUserData(TKhandler.doBearerAccessTkHeader());
    }

    public boolean isOldestUserComu(long comunidadId)
    {
        Log.d(TAG, "isOldestUserComu()");
        return isOldestUserComu(TKhandler.doBearerAccessTkHeader(), comunidadId);
    }

    public boolean loginInternal(String userName, String password) throws UiException
    {
        Log.d(TAG, "loginInternal()");

        boolean isLoginOk = false;
        try {
            isLoginOk = login(userName, password);
        } catch (InServiceException e) {
            if (e.getHttpMessage().equals(USER_NAME_NOT_FOUND.getHttpMessage())) {
                throw new UiException(SEARCH_COMU, R.string.user_without_signedUp);
            }
        }
        return isLoginOk;
    }

    public int modifyComuData(Comunidad comunidad)
    {
        Log.d(TAG, "modifyComuData()");
        return modifyComuData(TKhandler.doBearerAccessTkHeader(), comunidad);
    }

    public int modifyUser(Usuario usuario)
    {
        Log.d(TAG, "modifyUser()");
        return modifyUser(TKhandler.doBearerAccessTkHeader(), usuario);
    }

    public int modifyUserComu(UsuarioComunidad userComu)
    {
        Log.d(TAG, "modifyUserComu()");
        return modifyUserComu(TKhandler.doBearerAccessTkHeader(), userComu);
    }

    public int passwordChange(String newPassword)
    {
        Log.d(TAG, "passwordChange()");
        return passwordChange(TKhandler.doBearerAccessTkHeader(), newPassword);
    }

    public boolean regComuAndUserComu(UsuarioComunidad usuarioComunidad)
    {
        Log.d(TAG, "regComuAndUserComu()");
        return regComuAndUserComu(TKhandler.doBearerAccessTkHeader(), usuarioComunidad);
    }

    public int regUserComu(UsuarioComunidad usuarioComunidad)
    {
        Log.d(TAG, "regUserComu()");
        return regUserComu(TKhandler.doBearerAccessTkHeader(), usuarioComunidad);
    }

    public List<UsuarioComunidad> seeUserComusByComu(long idComunidad)
    {
        Log.d(TAG, "seeUserComusByComu()");
        return seeUserComusByComu(TKhandler.doBearerAccessTkHeader(), idComunidad);
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

    String checkBearerToken() throws UiException
    {
        String bearerAccessTkHeader = TKhandler.doBearerAccessTkHeader();

        if (bearerAccessTkHeader == null) { // No token in cache.
            throw new UiException(LOGIN, R.string.user_without_signedUp);
        }
        return bearerAccessTkHeader;
    }

    void catchAuthenticationException(InServiceException e) throws UiException
    {
        Log.e(TAG,"catchAuthenticationException():" + e.getHttpMessage());

        if (isMessageToLogin(e.getHttpMessage())) {  // Problema de identificación.
            throw new UiException(LOGIN, R.string.user_without_signedUp);
        }
    }
}