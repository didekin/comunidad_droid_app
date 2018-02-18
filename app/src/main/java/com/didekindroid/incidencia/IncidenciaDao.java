package com.didekindroid.incidencia;

import com.didekindroid.lib_one.api.HttpInitializerIf;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.security.IdentityCacherIf;
import com.didekindroid.lib_one.security.SecInitializerIf;
import com.didekinlib.http.exception.ErrorBean;
import com.didekinlib.http.incidencia.IncidenciaServEndPoints;
import com.didekinlib.model.incidencia.dominio.ImportanciaUser;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.IncidComment;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import java.io.EOFException;
import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

import static com.didekindroid.lib_one.HttpInitializer.httpInitializer;
import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekinlib.http.exception.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;

/**
 * User: pedro@didekin
 * Date: 18/11/15
 * Time: 13:11
 */
public final class IncidenciaDao implements IncidenciaServEndPoints {

    public static final IncidenciaDao incidenciaDao = new IncidenciaDao(secInitializer.get(), httpInitializer.get());
    private final IncidenciaServEndPoints endPoint;
    private final IdentityCacherIf tkCacher;

    private IncidenciaDao(SecInitializerIf secInitializerIn, HttpInitializerIf httpInitializerIn)
    {
        tkCacher = secInitializerIn.getTkCacher();
        endPoint = httpInitializerIn.getHttpHandler().getService(IncidenciaServEndPoints.class);
    }

    public IncidenciaDao(IncidenciaServEndPoints endPoint, IdentityCacherIf tkCacher)
    {
        this.endPoint = endPoint;
        this.tkCacher = tkCacher;
    }

    /*  ================================== IncidenciaServEndPoints implementation ============================*/

    @Override
    public Call<Integer> closeIncidencia(String accessToken, Resolucion resolucion)
    {
        return endPoint.closeIncidencia(accessToken, resolucion);
    }

    @Override
    public Call<Integer> deleteIncidencia(String accessToken, long incidenciaId)
    {
        return endPoint.deleteIncidencia(accessToken, incidenciaId);
    }

    @Override
    public Call<Integer> modifyIncidImportancia(String accessToken, IncidImportancia incidImportancia)
    {
        return endPoint.modifyIncidImportancia(accessToken, incidImportancia);
    }

    @Override
    public Call<Integer> modifyResolucion(String accessToken, Resolucion resolucion)
    {
        return endPoint.modifyResolucion(accessToken, resolucion);
    }

    @Override
    public Call<Integer> regIncidComment(String accessToken, IncidComment comment)
    {
        return endPoint.regIncidComment(accessToken, comment);
    }

    @Override
    public Call<Integer> regIncidImportancia(String accessToken, IncidImportancia incidImportancia)
    {
        return endPoint.regIncidImportancia(accessToken, incidImportancia);
    }

    @Override
    public Call<Integer> regResolucion(String accessToken, Resolucion resolucion)
    {
        return endPoint.regResolucion(accessToken, resolucion);
    }

    @Override
    public Call<List<IncidComment>> seeCommentsByIncid(String accessToken, long incidenciaId)
    {
        return endPoint.seeCommentsByIncid(accessToken, incidenciaId);
    }

    @Override
    public Call<IncidAndResolBundle> seeIncidImportancia(String accessToken, long incidenciaId)
    {
        return endPoint.seeIncidImportancia(accessToken, incidenciaId);
    }

    @Override
    public Call<List<IncidenciaUser>> seeIncidsOpenByComu(String accessToken, long comunidadId)
    {
        return endPoint.seeIncidsOpenByComu(accessToken, comunidadId);
    }

    @Override
    public Call<List<IncidenciaUser>> seeIncidsClosedByComu(String accessToken, long comunidadId)
    {
        return endPoint.seeIncidsClosedByComu(accessToken, comunidadId);
    }

    @Override
    public Call<Resolucion> seeResolucion(String accessToken, long incidenciaId)
    {
        return endPoint.seeResolucion(accessToken, incidenciaId);
    }

    @Override
    public Call<List<ImportanciaUser>> seeUserComusImportancia(String accessToken, long incidenciaId)
    {
        return endPoint.seeUserComusImportancia(accessToken, incidenciaId);
    }

//  =============================================================================
//                          CONVENIENCE METHODS
//  =============================================================================

    public int closeIncidencia(Resolucion resolucion) throws UiException
    {
        Timber.d("closeIncidencia()");
        try {
            Response<Integer> response = closeIncidencia(tkCacher.checkBearerTokenInCache(), resolucion).execute();
            return httpInitializer.get().getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    int deleteIncidencia(long incidenciaId) throws UiException
    {
        Timber.d("deleteIncidencia()");
        try {
            Response<Integer> response = deleteIncidencia(tkCacher.checkBearerTokenInCache(), incidenciaId).execute();
            return httpInitializer.get().getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    int modifyIncidImportancia(IncidImportancia incidImportancia) throws UiException
    {
        Timber.d("modifyIncidImportancia()");
        try {
            Response<Integer> response = modifyIncidImportancia(tkCacher.checkBearerTokenInCache(), incidImportancia).execute();
            return httpInitializer.get().getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    public int modifyResolucion(Resolucion resolucion) throws UiException
    {
        Timber.d("modifyResolucion()");
        try {
            Response<Integer> response = modifyResolucion(tkCacher.checkBearerTokenInCache(), resolucion).execute();
            return httpInitializer.get().getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    public int regIncidComment(IncidComment comment) throws UiException
    {
        Timber.d("regIncidComment()");
        try {
            Response<Integer> response = endPoint.regIncidComment(tkCacher.checkBearerTokenInCache(), comment).execute();
            return httpInitializer.get().getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    public int regIncidImportancia(IncidImportancia incidImportancia) throws UiException
    {
        Timber.d("regIncidImportancia()");
        try {
            Response<Integer> response = regIncidImportancia(tkCacher.checkBearerTokenInCache(), incidImportancia).execute();
            return httpInitializer.get().getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    public int regResolucion(Resolucion resolucion) throws UiException
    {
        Timber.d("regResolucion()");
        try {
            Response<Integer> response = regResolucion(tkCacher.checkBearerTokenInCache(), resolucion).execute();
            return httpInitializer.get().getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    public List<IncidComment> seeCommentsByIncid(long incidenciaId) throws UiException
    {
        Timber.d("seeCommentsByIncid()");
        try {
            Response<List<IncidComment>> response = seeCommentsByIncid(tkCacher.checkBearerTokenInCache(), incidenciaId).execute();
            return httpInitializer.get().getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    public IncidAndResolBundle seeIncidImportancia(long incidenciaId) throws UiException
    {
        Timber.d("seeIncidImportancia()");
        try {
            Response<IncidAndResolBundle> response = seeIncidImportancia(tkCacher.checkBearerTokenInCache(), incidenciaId).execute();
            return httpInitializer.get().getResponseBody(response);
        } catch (EOFException eo) {
            return null;
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    public List<IncidenciaUser> seeIncidsOpenByComu(long comunidadId) throws UiException
    {
        Timber.d("seeIncidsOpenByComu()");
        try {
            Response<List<IncidenciaUser>> response = seeIncidsOpenByComu(tkCacher.checkBearerTokenInCache(), comunidadId).execute();
            return httpInitializer.get().getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    public List<IncidenciaUser> seeIncidsClosedByComu(long comunidadId) throws UiException
    {
        Timber.d("seeIncidsClosedByComu()");
        try {
            Response<List<IncidenciaUser>> response = seeIncidsClosedByComu(tkCacher.checkBearerTokenInCache(), comunidadId).execute();
            return httpInitializer.get().getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    public Resolucion seeResolucion(long incidenciaId) throws UiException
    {
        Timber.d("checkResolucion()");
        try {
            Response<Resolucion> response = seeResolucion(tkCacher.checkBearerTokenInCache(), incidenciaId).execute();
            return httpInitializer.get().getResponseBody(response);
        } catch (EOFException eo) {
            return null;
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    List<ImportanciaUser> seeUserComusImportancia(long incidenciaId) throws UiException
    {
        Timber.d("seeUserComusImportancia()");
        try {
            Response<List<ImportanciaUser>> response = seeUserComusImportancia(tkCacher.checkBearerTokenInCache(), incidenciaId).execute();
            return httpInitializer.get().getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }
}
