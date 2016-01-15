package com.didekindroid.incidencia.webservices;

import android.util.Log;

import com.didekin.common.exception.InServiceException;
import com.didekin.incidservice.controller.IncidenciaServEndPoints;
import com.didekin.incidservice.domain.IncidenciaUser;
import com.didekin.incidservice.domain.Incidencia;
import com.didekin.serviceone.domain.Comunidad;
import com.didekindroid.common.UiException;

import java.util.List;

import static com.didekin.common.RetrofitRestBuilder.BUILDER;
import static com.didekindroid.DidekindroidApp.getBaseURL;
import static com.didekindroid.common.utils.UIutils.catchAuthenticationException;
import static com.didekindroid.common.utils.UIutils.checkBearerToken;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;

/**
 * User: pedro@didekin
 * Date: 18/11/15
 * Time: 13:11
 */
public enum IncidService implements IncidenciaServEndPoints {

    IncidenciaServ(BUILDER.getService(IncidenciaServEndPoints.class, getBaseURL())) {
        @Override
        public List<Incidencia> incidSeeByComu(String accessToken, long comunidadId)
        {
            return IncidenciaServ.endPoint.incidSeeByComu(accessToken, comunidadId);
        }

        @Override
        public List<Incidencia> incidSeeClosedByComu(String accessToken, long comunidadId)
        {
            return IncidenciaServ.endPoint.incidSeeClosedByComu(accessToken, comunidadId);
        }

        @Override
        public int regIncidenciaUser(String accessToken, IncidenciaUser incidenciaUser)
        {
            return IncidenciaServ.endPoint.regIncidenciaUser(accessToken, incidenciaUser);
        }
    },;

    private static final String TAG = IncidService.class.getCanonicalName();

    private final IncidenciaServEndPoints endPoint;

    IncidService(IncidenciaServEndPoints retrofitEndPoint)
    {
        endPoint = retrofitEndPoint;
    }

// :::::::::::::::::::::::::::::::::::::::::::::::::::::
//                  CONVENIENCE METHODS
// :::::::::::::::::::::::::::::::::::::::::::::::::::::

    /**
     * This method encapsulates the call to the UsuarioService.ServOne method.
     */
    public List<Comunidad> getComusByUser() throws UiException
    {
        Log.d(TAG, "getComusByUser()");
        return ServOne.getComusByUser();
    }

    /**
     * This method encapsulates the call to the UsuarioService.ServOne method.
     *
     * @param mComunidadId identifies the comunidad wherein the user has the role returned.
     */
    public String getHighestRolFunction(long mComunidadId) throws UiException
    {
        Log.d(TAG, "getHighestRolFunction()");
        return ServOne.getHighestRoleFunction(mComunidadId);
    }

    public List<Incidencia> incidSeeByComu(long comunidadId) throws UiException
    {
        Log.d(TAG, "incidSeeByComu()");
        List<Incidencia> incidencias = null;
        try {
            incidencias = incidSeeByComu(checkBearerToken(), comunidadId);
        } catch (InServiceException e) {
            catchAuthenticationException(e);
        }
        return incidencias;
    }

    public List<Incidencia> incidSeeClosedByComu(long comunidadId) throws UiException
    {
        Log.d(TAG, "incidSeeClosedByComu()");
        List<Incidencia> incidencias = null;
        try {
            incidencias = incidSeeClosedByComu(checkBearerToken(), comunidadId);
        } catch (InServiceException e) {
            catchAuthenticationException(e);
        }
        return incidencias;
    }

    public int regIncidenciaUser(IncidenciaUser incidenciaUser) throws UiException
    {
        Log.d(TAG, "regIncidenciaUser()");

        int regIncidencia = 0;
        try {
            regIncidencia = regIncidenciaUser(checkBearerToken(), incidenciaUser);
        } catch (InServiceException e) {
            catchAuthenticationException(e);
        }
        return regIncidencia;
    }
}
