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
        public int deleteAccessToken(String accessToken)
        {
            return ServOne.endPoint.deleteAccessToken(accessToken);
        }

        @Override
        public boolean deleteComunidad(String accessToken, long comunidadId)
        {
            return ServOne.endPoint.deleteComunidad(accessToken, comunidadId);
        }

        @Override
        public boolean deleteUser(String accessToken)
        {
            return ServOne.endPoint.deleteUser(accessToken);
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
        public int modifyUser(String accessToken, Usuario usuario)
        {
            return ServOne.endPoint.modifyUser(accessToken, usuario);
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

    //    ::::::::::::::::::::::::::::::::::::::::::
    //                  CONVENIENCE METHODS
    //    ::::::::::::::::::::::::::::::::::::::::::

    public int deleteAccessToken()
    {
        Log.d(TAG, "deleteAccessToken()");
        return deleteAccessToken(TKhandler.doBearerAccessTkHeader());
    }

    public boolean deleteComunidad(long comunidadId)
    {
        Log.d(TAG, "deleteComunidad()");
        return deleteComunidad(TKhandler.doBearerAccessTkHeader(), comunidadId);
    }

    public boolean deleteUser()
    {
        Log.d(TAG, "deleteUser()");
        return deleteUser(TKhandler.doBearerAccessTkHeader());
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
        return endPoint.getUserData(TKhandler.doBearerAccessTkHeader());
    }

    public List<UsuarioComunidad> getUsuariosComunidad()
    {
        Log.d(TAG, "getUserComusByUser()");
        String bearerAccessTkHeader = TKhandler.doBearerAccessTkHeader();
        return (bearerAccessTkHeader != null ? endPoint.getUserComusByUser(bearerAccessTkHeader) : null);
    }

    public int modifyUser(Usuario usuario)
    {
        Log.d(TAG, "modifyUser()");
        return endPoint.modifyUser(TKhandler.doBearerAccessTkHeader(), usuario);
    }

    public boolean regComuAndUserComu(UsuarioComunidad usuarioComunidad)
    {
        Log.d(TAG, "regComuAndUserComu()");
        return endPoint.regComuAndUserComu(TKhandler.doBearerAccessTkHeader(), usuarioComunidad);
    }

    public int regUserComu(UsuarioComunidad usuarioComunidad)
    {
        Log.d(TAG, "regUserComu()");
        return endPoint.regUserComu(TKhandler.doBearerAccessTkHeader(), usuarioComunidad);
    }

    public List<UsuarioComunidad> seeUserComuByComu(long idComunidad)
    {
        Log.d(TAG, "seeUserComuByComu()");
        return endPoint.seeUserComuByComu(TKhandler.doBearerAccessTkHeader(), idComunidad);
    }
}