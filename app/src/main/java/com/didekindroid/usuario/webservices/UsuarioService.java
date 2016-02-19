package com.didekindroid.usuario.webservices;

import android.util.Log;

import com.didekin.common.exception.InServiceException;
import com.didekin.usuario.controller.UsuarioEndPoints;
import com.didekin.usuario.dominio.Comunidad;
import com.didekin.usuario.dominio.Usuario;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.common.activity.UiException;

import java.util.List;

import static com.didekin.common.RetrofitRestBuilder.BUILDER;
import static com.didekindroid.DidekindroidApp.getBaseURL;
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
        public String getGcmToken(String accessToken)
        {
            return ServOne.endPoint.getGcmToken(accessToken);
        }

        @Override
        public UsuarioComunidad getUserComuByUserAndComu(String accessToken, long comunidadId)
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
        public int modifyUserGcmToken(String accessToken, String gcmToken)
        {
            return ServOne.endPoint.modifyUserGcmToken(accessToken, gcmToken);
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
    },
    ;

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
            throw new UiException(e);
        }
        return isDeleted;
    }

    public boolean deleteUser() throws UiException
    {
        Log.d(TAG, "deleteUser()");
        boolean isDeleted;
        try {
            isDeleted = deleteUser(checkBearerToken());
        } catch (InServiceException e) {
            throw new UiException(e);
        }
        return isDeleted;
    }


    public int deleteUserComu(long comunidadId) throws UiException
    {
        Log.d(TAG, "deleteUserComu()");

        int deleted;
        try {
            deleted = deleteUserComu(checkBearerToken(), comunidadId);
        } catch (InServiceException e) {
            throw new UiException(e);
        }
        return deleted;
    }

    public Comunidad getComuData(long idComunidad) throws UiException
    {
        Log.d(TAG, "getComuData()");

        Comunidad comunidad;
        try {
            comunidad = getComuData(checkBearerToken(), idComunidad);
        } catch (InServiceException e) {
            throw new UiException(e);
        }
        return comunidad;
    }

    public List<Comunidad> getComusByUser() throws UiException
    {
        Log.d(TAG, "getComusByUser()");

        List<Comunidad> comusByUser;
        try {
            comusByUser = getComusByUser(checkBearerToken());
        } catch (InServiceException e) {
            throw new UiException(e);
        }
        return comusByUser;
    }

    public String getGcmToken() throws UiException
    {
        Log.d(TAG, "getGcmToken()");
        String gcmToken;
        try {
            gcmToken = getGcmToken(checkBearerToken());
        } catch (InServiceException e) {
            throw new UiException(e);
        }
        return gcmToken;
    }

    public UsuarioComunidad getUserComuByUserAndComu(long comunidadId) throws UiException
    {
        Log.d(TAG, "getUserComuByUserAndComu()");

        UsuarioComunidad userComuByUserAndComu;
        try {
            userComuByUserAndComu = getUserComuByUserAndComu(checkBearerToken(), comunidadId);
        } catch (InServiceException e) {
            throw new UiException(e);
        }
        return userComuByUserAndComu;
    }

    public Usuario getUserData() throws UiException
    {
        Log.d(TAG, ("getUserData()"));

        Usuario userData;
        try {
            userData = getUserData(checkBearerToken());
        } catch (InServiceException e) {
            throw new UiException(e);
        }
        return userData;
    }

    public boolean isOldestUserComu(long comunidadId) throws UiException
    {
        Log.d(TAG, "isOldestUserComu()");

        boolean isOldestUserComu;
        try {
            isOldestUserComu = isOldestUserComu(checkBearerToken(), comunidadId);
        } catch (InServiceException e) {
            throw new UiException(e);
        }
        return isOldestUserComu;
    }

    public boolean loginInternal(String userName, String password) throws UiException
    {
        Log.d(TAG, "loginInternal()");

        boolean isLoginOk;
        try {
            isLoginOk = login(userName, password);
        } catch (InServiceException e) {
            throw new UiException(e);
        }
        return isLoginOk;
    }

    public int modifyComuData(Comunidad comunidad) throws UiException
    {
        Log.d(TAG, "modifyComuData()");

        int modifyComuData;
        try {
            modifyComuData = modifyComuData(checkBearerToken(), comunidad);
        } catch (InServiceException e) {
            throw new UiException(e);
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

        int modifyUser;
        try {
            modifyUser = modifyUser(checkBearerToken(), usuario);
        } catch (InServiceException e) {
            throw new UiException(e);
        }
        return modifyUser;
    }

    public int modifyUserComu(UsuarioComunidad userComu) throws UiException
    {
        Log.d(TAG, "modifyUserComu()");

        int modifyUserComu;
        try {
            modifyUserComu = modifyUserComu(checkBearerToken(), userComu);
        } catch (InServiceException e) {
            throw new UiException(e);
        }
        return modifyUserComu;
    }

    public int passwordChange(String newPassword) throws UiException
    {
        Log.d(TAG, "passwordChange()");

        int passwordChange;
        try {
            passwordChange = passwordChange(checkBearerToken(), newPassword);
        } catch (InServiceException e) {
            throw new UiException(e);
        }
        return passwordChange;
    }

    public boolean regComuAndUserComu(UsuarioComunidad usuarioComunidad) throws UiException
    {
        Log.d(TAG, "regComuAndUserComu()");

        boolean isRegistered;
        try {
            isRegistered = regComuAndUserComu(checkBearerToken(), usuarioComunidad);
        } catch (InServiceException e) {
            throw new UiException(e);
        }
        return isRegistered;
    }

    public int regUserComu(UsuarioComunidad usuarioComunidad) throws UiException
    {
        Log.d(TAG, "regUserComu()");

        int regUserComu;
        try {
            regUserComu = regUserComu(checkBearerToken(), usuarioComunidad);
        } catch (InServiceException e) {
            throw new UiException(e);
        }
        return regUserComu;
    }

    public List<UsuarioComunidad> seeUserComusByComu(long idComunidad) throws UiException
    {
        Log.d(TAG, "seeUserComusByComu()");

        List<UsuarioComunidad> usuarioComunidadList;
        try {
            usuarioComunidadList = seeUserComusByComu(checkBearerToken(), idComunidad);
        } catch (InServiceException e) {
            throw new UiException(e);
        }
        return usuarioComunidadList;
    }

    public List<UsuarioComunidad> seeUserComusByUser() throws UiException
    {
        Log.d(TAG, "seeUserComusByUser()");

        List<UsuarioComunidad> userComuList;

        try {
            userComuList = seeUserComusByUser(checkBearerToken());
        } catch (InServiceException e) {
            throw new UiException(e);
        }
        return userComuList;
    }

//    ============================ HELPER METHODS ============================

}