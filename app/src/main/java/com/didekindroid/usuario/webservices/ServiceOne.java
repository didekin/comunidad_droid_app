package com.didekindroid.usuario.webservices;

import android.util.Log;
import com.didekin.retrofitcl.ServiceOneEndPoints;
import com.didekin.serviceone.controllers.ServiceOneEndPointsIf;
import com.didekin.serviceone.domain.Comunidad;
import com.didekin.serviceone.domain.Usuario;
import com.didekin.serviceone.domain.UsuarioComunidad;

import java.util.List;

import static com.didekin.retrofitcl.RetrofitRestBuilder.BUILDER;
import static com.didekindroid.DidekindroidApp.getBaseURL;
import static com.didekindroid.usuario.security.TokenHandler.TKhandler;

/**
 * User: pedro@didekin
 * Date: 07/06/15
 * Time: 15:06
 */
public enum ServiceOne implements ServiceOneEndPointsIf {

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
        public List<Comunidad> getComusByUser(String accessToken)
        {
            return ServOne.endPoint.getComusByUser(accessToken);
        }

        @Override
        public List<UsuarioComunidad> getUserComusByUser(String accessToken)
        {
            return ServOne.endPoint.getUserComusByUser(accessToken);
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
        public List<UsuarioComunidad> seeUserComuByComu(String accessToken, long comunidadId)
        {
            return ServOne.endPoint.seeUserComuByComu(accessToken, comunidadId);
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

    public List<Comunidad> getComunidadesByUser()  // TODO: ¿a desaparecer?
    {
        Log.d(TAG, "getComusByUser()");
        String bearerAccessTkHeader = TKhandler.doBearerAccessTkHeader();
        return (bearerAccessTkHeader != null ? endPoint.getComusByUser(bearerAccessTkHeader) : null);
    }

    public Usuario getUserData()
    {
        Log.d(TAG, ("getUserData()"));
        return getUserData(TKhandler.doBearerAccessTkHeader());
    }

    public List<UsuarioComunidad> getUserComusByUser()
    {
        Log.d(TAG, "getUserComusByUser()");
        String bearerAccessTkHeader = TKhandler.doBearerAccessTkHeader();
        return (bearerAccessTkHeader != null ? getUserComusByUser(bearerAccessTkHeader) : null);
    }

    public boolean isOldestUserComu(long comunidadId)
    {
        Log.d(TAG, "isOldestUserComu()");
        return isOldestUserComu(TKhandler.doBearerAccessTkHeader(), comunidadId);
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

    public List<UsuarioComunidad> seeUserComuByComu(long idComunidad)
    {
        Log.d(TAG, "seeUserComuByComu()");
        return seeUserComuByComu(TKhandler.doBearerAccessTkHeader(), idComunidad);
    }
}