package com.didekindroid.incidencia;

import com.didekindroid.exception.UiException;
import com.didekindroid.security.IdentityCacher;
import com.didekinlib.http.ErrorBean;
import com.didekinlib.http.retrofit.IncidenciaServEndPoints;
import com.didekinlib.model.comunidad.Comunidad;
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

import static com.didekindroid.AppInitializer.creator;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.usuariocomunidad.repository.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.util.DaoUtil.getResponseBody;
import static com.didekinlib.http.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;

/**
 * User: pedro@didekin
 * Date: 18/11/15
 * Time: 13:11
 */
public final class IncidDaoRemote implements IncidenciaServEndPoints {

    public static final IncidDaoRemote incidenciaDao = new IncidDaoRemote();
    private final IncidenciaServEndPoints endPoint;
    private final IdentityCacher identityCacher;

    private IncidDaoRemote()
    {
        this(creator.get().getRetrofitHandler().getService(IncidenciaServEndPoints.class), TKhandler);
    }

    public IncidDaoRemote(IncidenciaServEndPoints endPoint, IdentityCacher identityCacher)
    {
        this.endPoint = endPoint;
        this.identityCacher = identityCacher;
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
    public Call<Resolucion> seeResolucion(String accessToken, long resolucionId)
    {
        return endPoint.seeResolucion(accessToken, resolucionId);
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
            Response<Integer> response = closeIncidencia(identityCacher.checkBearerTokenInCache(), resolucion).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    int deleteIncidencia(long incidenciaId) throws UiException
    {
        Timber.d("deleteIncidencia()");
        try {
            Response<Integer> response = deleteIncidencia(identityCacher.checkBearerTokenInCache(), incidenciaId).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    /**
     * This method encapsulates the call to the UsuarioDaoRemote.userComuDaoRemote method.
     */
    public List<Comunidad> getComusByUser() throws UiException
    {
        Timber.d("getComusByUser()");
        return userComuDaoRemote.getComusByUser();
    }

    int modifyIncidImportancia(IncidImportancia incidImportancia) throws UiException
    {
        Timber.d("modifyIncidImportancia()");
        try {
            Response<Integer> response = modifyIncidImportancia(identityCacher.checkBearerTokenInCache(), incidImportancia).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    public int modifyResolucion(Resolucion resolucion) throws UiException
    {
        Timber.d("modifyResolucion()");
        try {
            Response<Integer> response = modifyResolucion(identityCacher.checkBearerTokenInCache(), resolucion).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    public int regIncidComment(IncidComment comment) throws UiException
    {
        Timber.d("regIncidComment()");
        try {
            Response<Integer> response = endPoint.regIncidComment(identityCacher.checkBearerTokenInCache(), comment).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    public int regIncidImportancia(IncidImportancia incidImportancia) throws UiException
    {
        Timber.d("regIncidImportancia()");
        try {
            Response<Integer> response = regIncidImportancia(identityCacher.checkBearerTokenInCache(), incidImportancia).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    public int regResolucion(Resolucion resolucion) throws UiException
    {
        Timber.d("regResolucion()");
        try {
            Response<Integer> response = regResolucion(identityCacher.checkBearerTokenInCache(), resolucion).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    public List<IncidComment> seeCommentsByIncid(long incidenciaId) throws UiException
    {
        Timber.d("seeCommentsByIncid()");
        try {
            Response<List<IncidComment>> response = seeCommentsByIncid(identityCacher.checkBearerTokenInCache(), incidenciaId).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    public IncidAndResolBundle seeIncidImportancia(long incidenciaId) throws UiException
    {
        Timber.d("seeIncidImportancia()");
        try {
            Response<IncidAndResolBundle> response = seeIncidImportancia(identityCacher.checkBearerTokenInCache(), incidenciaId).execute();
            return getResponseBody(response);
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
            Response<List<IncidenciaUser>> response = seeIncidsOpenByComu(identityCacher.checkBearerTokenInCache(), comunidadId).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    public List<IncidenciaUser> seeIncidsClosedByComu(long comunidadId) throws UiException
    {
        Timber.d("seeIncidsClosedByComu()");
        try {
            Response<List<IncidenciaUser>> response = seeIncidsClosedByComu(identityCacher.checkBearerTokenInCache(), comunidadId).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    public Resolucion seeResolucion(long resolucionId) throws UiException
    {
        Timber.d("checkResolucion()");
        try {
            Response<Resolucion> response = seeResolucion(identityCacher.checkBearerTokenInCache(), resolucionId).execute();
            return getResponseBody(response);
        } catch (EOFException eo) { // No resolucion in BD.
            return null;
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }

    public List<ImportanciaUser> seeUserComusImportancia(long incidenciaId) throws UiException
    {
        Timber.d("seeUserComusImportancia()");
        try {
            Response<List<ImportanciaUser>> response = seeUserComusImportancia(identityCacher.checkBearerTokenInCache(), incidenciaId).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }
}
