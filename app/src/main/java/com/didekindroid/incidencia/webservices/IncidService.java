package com.didekindroid.incidencia.webservices;

import android.util.Log;

import com.didekin.common.exception.InServiceException;
import com.didekin.incidservice.controller.IncidenciaServEndPoints;
import com.didekin.incidservice.domain.IncidenciaUser;
import com.didekin.incidservice.domain.Incidencia;
import com.didekin.serviceone.domain.Comunidad;
import com.didekindroid.common.UiException;

import java.util.List;

import retrofit.http.Header;
import retrofit.http.Path;

import static com.didekin.common.RetrofitRestBuilder.BUILDER;
import static com.didekindroid.DidekindroidApp.getBaseURL;
import static com.didekindroid.common.utils.UIutils.catchAuthenticationException;
import static com.didekindroid.common.utils.UIutils.catchIncidenciaFkException;
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
        public IncidenciaUser getIncidenciaUserWithPowers(@Header("Authorization") String accessToken, @Path("comunidadId") long incidenciaId)
        {
            return IncidenciaServ.endPoint.getIncidenciaUserWithPowers(accessToken, incidenciaId);
        }

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
     * This method returns an incidencia with the powers of the user on the incidence.
     *
     * @param incidenciaId identifies the incidencia to be returned.
     */
    public IncidenciaUser getIncidenciaUserWithPowers(long incidenciaId) throws UiException
    {
        Log.d(TAG, "getIncidenciaUserWithPowers()");
        IncidenciaUser incidenciaUser = null;
        try {
            incidenciaUser = getIncidenciaUserWithPowers(checkBearerToken(), incidenciaId);
        } catch (InServiceException e) {
            catchAuthenticationException(e, TAG);
            catchIncidenciaFkException(e, TAG);
        }
        return incidenciaUser;
    }

    public List<Incidencia> incidSeeByComu(long comunidadId) throws UiException
    {
        Log.d(TAG, "incidSeeByComu()");
        List<Incidencia> incidencias = null;
        try {
            incidencias = incidSeeByComu(checkBearerToken(), comunidadId);
        } catch (InServiceException e) {
            catchAuthenticationException(e, TAG);
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
            catchAuthenticationException(e, TAG);
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
            catchAuthenticationException(e, TAG);
        }
        return regIncidencia;
    }
}
