package com.didekindroid.incidencia.webservices;

import android.util.Log;

import com.didekin.common.exception.InServiceException;
import com.didekin.incidservice.controller.IncidenciaServEndPoints;
import com.didekin.incidservice.domain.IncidUserComu;
import com.didekindroid.common.UiException;

import static com.didekin.common.RetrofitRestBuilder.BUILDER;
import static com.didekindroid.DidekindroidApp.getBaseURL;
import static com.didekindroid.common.utils.UIutils.catchAuthenticationException;
import static com.didekindroid.common.utils.UIutils.checkBearerToken;

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


}
