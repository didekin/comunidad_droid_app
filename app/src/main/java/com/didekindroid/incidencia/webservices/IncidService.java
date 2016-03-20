package com.didekindroid.incidencia.webservices;

import android.util.Log;

import com.didekin.common.exception.InServiceException;
import com.didekin.incidservice.controller.IncidenciaServEndPoints;
import com.didekin.incidservice.dominio.IncidAndResolBundle;
import com.didekin.incidservice.dominio.IncidComment;
import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekin.incidservice.dominio.IncidenciaUser;
import com.didekin.incidservice.dominio.Resolucion;
import com.didekin.usuario.dominio.Comunidad;
import com.didekindroid.common.activity.UiException;

import java.util.List;

import static com.didekin.common.RetrofitRestBuilder.BUILDER;
import static com.didekindroid.DidekindroidApp.getBaseURL;
import static com.didekindroid.common.utils.UIutils.checkBearerToken;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static com.google.common.base.Preconditions.checkArgument;

/**
 * User: pedro@didekin
 * Date: 18/11/15
 * Time: 13:11
 */
public enum IncidService implements IncidenciaServEndPoints {

    IncidenciaServ(BUILDER.getService(IncidenciaServEndPoints.class, getBaseURL())) {
        @Override
        public int closeIncidencia(String accessToken, Resolucion resolucion)
        {
            return IncidenciaServ.endPoint.closeIncidencia(accessToken, resolucion);
        }

        @Override
        public int deleteIncidencia(String accessToken, long incidenciaId)
        {
            return IncidenciaServ.endPoint.deleteIncidencia(accessToken, incidenciaId);
        }

        @Override
        public int modifyIncidImportancia(String accessToken, IncidImportancia incidImportancia)
        {
            return IncidenciaServ.endPoint.modifyIncidImportancia(accessToken, incidImportancia);
        }

        @Override
        public int modifyResolucion(String accessToken, Resolucion resolucion)
        {
            return IncidenciaServ.endPoint.modifyResolucion(accessToken, resolucion);
        }

        @Override
        public int regIncidComment(String accessToken, IncidComment comment)
        {
            return IncidenciaServ.endPoint.regIncidComment(accessToken, comment);
        }

        @Override
        public int regIncidImportancia(String accessToken, IncidImportancia incidImportancia)
        {
            return IncidenciaServ.endPoint.regIncidImportancia(accessToken, incidImportancia);
        }

        @Override
        public int regResolucion(String accessToken, Resolucion resolucion)
        {
            return IncidenciaServ.endPoint.regResolucion(accessToken, resolucion);
        }

        @Override
        public List<IncidComment> seeCommentsByIncid(String accessToken, long incidenciaId)
        {
            return IncidenciaServ.endPoint.seeCommentsByIncid(accessToken, incidenciaId);
        }

        @Override
        public IncidAndResolBundle seeIncidImportancia(String accessToken, long incidenciaId)
        {
            return IncidenciaServ.endPoint.seeIncidImportancia(accessToken, incidenciaId);
        }

        @Override
        public List<IncidenciaUser> seeIncidsOpenByComu(String accessToken, long comunidadId)
        {
            return IncidenciaServ.endPoint.seeIncidsOpenByComu(accessToken, comunidadId);
        }

        @Override
        public List<IncidenciaUser> seeIncidsClosedByComu(String accessToken, long comunidadId)
        {
            return IncidenciaServ.endPoint.seeIncidsClosedByComu(accessToken, comunidadId);
        }

        @Override
        public Resolucion seeResolucion(String accessToken, long resolucionId)
        {
            return IncidenciaServ.endPoint.seeResolucion(accessToken, resolucionId);
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

    public int closeIncidencia(Resolucion resolucion) throws UiException
    {
        Log.d(TAG, "closeIncidencia()");
        int incidenciasClosed;
        try {
            incidenciasClosed = closeIncidencia(checkBearerToken(), resolucion);
        }catch (InServiceException ie){
            throw new UiException(ie);
        }
        return incidenciasClosed;
    }

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

    public int modifyIncidImportancia(IncidImportancia incidImportancia) throws UiException
    {
        Log.d(TAG, "modifyUser()");
        checkArgument(incidImportancia.getIncidencia().getUserName() != null);
        int rowModified;
        try {
            rowModified = modifyIncidImportancia(checkBearerToken(), incidImportancia);
        } catch (InServiceException ue) {
            throw new UiException(ue);
        }
        return rowModified;
    }

    public int modifyResolucion(Resolucion resolucion) throws UiException
    {
        Log.d(TAG, "modifyResolucion()");
        int resolucionModifyed;
        try {
            resolucionModifyed = modifyResolucion(checkBearerToken(), resolucion);
        }catch(InServiceException ie){
            throw new UiException(ie);
        }
        return resolucionModifyed;
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

    public int regIncidImportancia(IncidImportancia incidImportancia) throws UiException
    {
        Log.d(TAG, "regIncidImportancia()");

        int regIncidencia;
        try {
            regIncidencia = regIncidImportancia(checkBearerToken(), incidImportancia);
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
        } catch (InServiceException ie) {
            throw new UiException(ie);
        }
        return regResolucion;
    }

    public List<IncidComment> seeCommentsByIncid(long incidenciaId) throws UiException
    {
        Log.d(TAG, "seeCommentsByIncid()");
        List<IncidComment> comments;
        try {
            comments = seeCommentsByIncid(checkBearerToken(), incidenciaId);
        } catch (InServiceException ie) {
            throw new UiException(ie);
        }
        return comments;
    }

    public IncidAndResolBundle seeIncidImportancia(long incidenciaId) throws UiException
    {
        Log.d(TAG, "seeIncidImportancia()");
        IncidAndResolBundle incidencia;
        try {
            incidencia = seeIncidImportancia(checkBearerToken(), incidenciaId);
        } catch (InServiceException e) {
            throw new UiException(e);
        }
        return incidencia;
    }

    public List<IncidenciaUser> seeIncidsOpenByComu(long comunidadId) throws UiException
    {
        Log.d(TAG, "seeIncidsOpenByComu()");
        List<IncidenciaUser> incidencias;
        try {
            incidencias = seeIncidsOpenByComu(checkBearerToken(), comunidadId);
        } catch (InServiceException e) {
            throw new UiException(e);
        }
        return incidencias;
    }

    public List<IncidenciaUser> seeIncidsClosedByComu(long comunidadId) throws UiException
    {
        Log.d(TAG, "seeIncidsClosedByComu()");
        List<IncidenciaUser> incidencias;
        try {
            incidencias = seeIncidsClosedByComu(checkBearerToken(), comunidadId);
        } catch (InServiceException e) {
            throw new UiException(e);
        }
        return incidencias;
    }

    public Resolucion seeResolucion(long resolucionId) throws UiException
    {
        Log.d(TAG, "seeResolucion()");
        Resolucion resolucion;
        try {
            resolucion = seeResolucion(checkBearerToken(), resolucionId);
        } catch (InServiceException ie){
            throw new UiException(ie);
        }
        return resolucion;
    }
}
