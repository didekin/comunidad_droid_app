package com.didekindroid.incidencia.webservices;

import android.util.Log;

import com.didekin.common.exception.InServiceException;
import com.didekin.incidservice.controller.IncidenciaServEndPoints;
import com.didekin.incidservice.dominio.IncidComment;
import com.didekin.incidservice.dominio.Incidencia;
import com.didekin.incidservice.dominio.IncidenciaUser;
import com.didekin.incidservice.dominio.Resolucion;
import com.didekin.usuario.dominio.Comunidad;
import com.didekindroid.common.activity.UiException;

import java.util.List;

import static com.didekin.common.RetrofitRestBuilder.BUILDER;
import static com.didekindroid.DidekindroidApp.getBaseURL;
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
        public int deleteIncidencia(String accessToken, long incidenciaId)
        {
            return IncidenciaServ.endPoint.deleteIncidencia(accessToken, incidenciaId);
        }

        @Override
        public IncidenciaUser getIncidenciaUserWithPowers(String accessToken, long incidenciaId)
        {
            return IncidenciaServ.endPoint.getIncidenciaUserWithPowers(accessToken, incidenciaId);
        }

        @Override
        public List<IncidComment> incidCommentsSee(String accessToken, Incidencia incidencia)
        {
            return IncidenciaServ.endPoint.incidCommentsSee(accessToken, incidencia);
        }

        @Override
        public List<IncidenciaUser> incidSeeByComu(String accessToken, long comunidadId)
        {
            return IncidenciaServ.endPoint.incidSeeByComu(accessToken, comunidadId);
        }

        @Override
        public List<IncidenciaUser> incidSeeClosedByComu(String accessToken, long comunidadId)
        {
            return IncidenciaServ.endPoint.incidSeeClosedByComu(accessToken, comunidadId);
        }

        @Override
        public int modifyIncidenciaUser(String accessToken, IncidenciaUser incidenciaUser)
        {
            return IncidenciaServ.endPoint.modifyIncidenciaUser(accessToken, incidenciaUser);
        }

        @Override
        public int modifyUser(String accessToken, IncidenciaUser incidenciaUser)
        {
            return IncidenciaServ.endPoint.modifyUser(accessToken, incidenciaUser);
        }

        @Override
        public int regIncidComment(String accessToken, IncidComment comment)
        {
            return IncidenciaServ.endPoint.regIncidComment(accessToken, comment);
        }

        @Override
        public int regIncidenciaUser(String accessToken, IncidenciaUser incidenciaUser)
        {
            return IncidenciaServ.endPoint.regIncidenciaUser(accessToken, incidenciaUser);
        }

        @Override
        public int regResolucion(String accessToken, Resolucion resolucion)
        {
            return IncidenciaServ.endPoint.regResolucion(accessToken, resolucion);
        }

        @Override
        public int regUserInIncidencia(String accessToken, IncidenciaUser incidenciaUser)
        {
            return IncidenciaServ.endPoint.regUserInIncidencia(accessToken, incidenciaUser);
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

    public int deleteIncidencia(long incidenciaId) throws UiException
    {
        Log.d(TAG, "deleteIncidencia()");

        int deleteIncidencias;

        try {
            deleteIncidencias = deleteIncidencia(checkBearerToken(), incidenciaId);
        } catch (InServiceException e) {
            throw new UiException(e);
        }
        return deleteIncidencias;
    }

    /**
     * This method encapsulates the call to the UsuarioService.ServOne method.
     */
    public List<Comunidad> getComusByUser() throws UiException
    {
        Log.d(TAG, "getComusByUser()");
        return ServOne.getComusByUser();
    }

    /**
     * The user has an IncidenciaUser relationship, this method returns an incidencia with the powers of the user on the incidence.
     * If not, return an IncidenciaUser instance with usuario == null.
     *
     * @param incidenciaId identifies the incidencia to be returned.
     */
    public IncidenciaUser getIncidenciaUserWithPowers(long incidenciaId) throws UiException
    {
        Log.d(TAG, "getIncidenciaUserWithPowers()");
        IncidenciaUser incidenciaUser;
        try {
            incidenciaUser = getIncidenciaUserWithPowers(checkBearerToken(), incidenciaId);
        } catch (InServiceException e) {throw new UiException(e);
        }
        return incidenciaUser;
    }

    public List<IncidComment> incidCommentsSee(Incidencia incidencia) throws UiException
    {
        Log.d(TAG, "incidCommentsSee()");
        List<IncidComment> comments;
        // Extract ids.
        Incidencia incidenciaIn = new Incidencia.IncidenciaBuilder()
                .incidenciaId(incidencia.getIncidenciaId())
                .comunidad(new Comunidad.ComunidadBuilder()
                        .c_id(incidencia.getComunidad()
                                .getC_Id())
                        .build())
                .build();
        try {
            comments = incidCommentsSee(checkBearerToken(), incidenciaIn);
        } catch (InServiceException ie) {
            throw new UiException(ie);
        }
        return comments;
    }

    public List<IncidenciaUser> incidSeeByComu(long comunidadId) throws UiException
    {
        Log.d(TAG, "incidSeeByComu()");
        List<IncidenciaUser> incidencias;
        try {
            incidencias = incidSeeByComu(checkBearerToken(), comunidadId);
        } catch (InServiceException e) {
            throw new UiException(e);
        }
        return incidencias;
    }

    public List<IncidenciaUser> incidSeeClosedByComu(long comunidadId) throws UiException
    {
        Log.d(TAG, "incidSeeClosedByComu()");
        List<IncidenciaUser> incidencias;
        try {
            incidencias = incidSeeClosedByComu(checkBearerToken(), comunidadId);
        } catch (InServiceException e) {
            throw new UiException(e);
        }
        return incidencias;
    }

    public int modifyIncidenciaUser(IncidenciaUser incidenciaUser) throws UiException
    {
        Log.d(TAG, "modifyIncidenciaUser()");

        int modifyIncidencias;

        try {
            modifyIncidencias = modifyIncidenciaUser(checkBearerToken(), incidenciaUser);
        } catch (InServiceException e) {
            throw new UiException(e);
        }
        return modifyIncidencias;
    }

    public int modifyUser(IncidenciaUser incidenciaUser) throws UiException
    {
        Log.d(TAG, "modifyUser()");
        int rowModified;
        try {
            rowModified = modifyUser(checkBearerToken(), incidenciaUser);
        } catch (InServiceException ue) {
            throw new UiException(ue);
        }
        return rowModified;
    }

    public int regIncidComment(IncidComment comment) throws UiException
    {
        Log.d(TAG, "regIncidComment()");
        int insertedRow;
        try {
            insertedRow = IncidenciaServ.endPoint.regIncidComment(checkBearerToken(), comment);
        } catch (InServiceException ie) {
            throw new UiException(ie);
        }
        return insertedRow;
    }

    public int regIncidenciaUser(IncidenciaUser incidenciaUser) throws UiException
    {
        Log.d(TAG, "regIncidenciaUser()");

        int regIncidencia;
        try {
            regIncidencia = regIncidenciaUser(checkBearerToken(), incidenciaUser);
        } catch (InServiceException e) {
            throw new UiException(e);
        }
        return regIncidencia;
    }

    public int regResolucion(Resolucion resolucion) throws UiException
    {
        Log.d(TAG, "regResolucion()");
        int regResolucion;
        try {
            regResolucion = regResolucion(checkBearerToken(), resolucion);
        } catch (InServiceException ie){
            throw new UiException(ie);
        }
        return regResolucion;
    }

    public int regUserInIncidencia(IncidenciaUser incidenciaUser) throws UiException
    {
        Log.d(TAG, "regUserInIncidencia()");
        int insertRow;
        try {
            insertRow = regUserInIncidencia(checkBearerToken(), incidenciaUser);
        } catch (InServiceException ie) {
            throw new UiException(ie);
        }
        return insertRow;
    }
}
