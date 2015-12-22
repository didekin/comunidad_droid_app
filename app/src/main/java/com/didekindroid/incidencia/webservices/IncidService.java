package com.didekindroid.incidencia.webservices;

import android.util.Log;

import com.didekin.common.exception.InServiceException;
import com.didekin.incidservice.controller.IncidenciaServEndPoints;
import com.didekin.incidservice.domain.IncidUserComu;
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
        public int regIncidenciaUserComu(String accessToken, IncidUserComu incidUserComu)
        {
            return IncidenciaServ.endPoint.regIncidenciaUserComu(accessToken, incidUserComu);
        }

        @Override
        public List<IncidUserComu> incidSeeByUser(String accessToken)
        {
            return IncidenciaServ.endPoint.incidSeeByUser(accessToken);
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

    public int regIncidenciaUserComu(IncidUserComu incidUserComu) throws UiException
    {
        Log.d(TAG, "regIncidenciaUserComu()");

        int regIncidencia = 0;
        try {
            regIncidencia = regIncidenciaUserComu(checkBearerToken(), incidUserComu);
        } catch (InServiceException e) {
            catchAuthenticationException(e);
        }
        return regIncidencia;
    }

    /**
     * This method encapsulates the call to the UsuarioService.ServOne method.
     *
     * @param mComunidadId identifies the comunidad wherein the user has the role returned.*/
    public String getHighestRolFunction(long mComunidadId) throws UiException
    {
        Log.d(TAG, "getHighestRolFunction()");
        return ServOne.getHighestRoleFunction(mComunidadId);
    }

    public List<IncidUserComu> incidSeeByUser() throws UiException
    {
        Log.d(TAG, "incidSeeByUser()");
        List<IncidUserComu> incidUserComuList = null;
        try {
            incidUserComuList = incidSeeByUser(checkBearerToken());
        } catch (InServiceException e) {
            catchAuthenticationException(e);
        }
        return incidUserComuList;
    }
}
