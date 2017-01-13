package com.didekindroid.incidencia;

import com.didekin.comunidad.Comunidad;
import com.didekin.incidencia.dominio.ImportanciaUser;
import com.didekin.incidencia.dominio.IncidAndResolBundle;
import com.didekin.incidencia.dominio.IncidComment;
import com.didekin.incidencia.dominio.IncidImportancia;
import com.didekin.incidencia.dominio.IncidenciaUser;
import com.didekin.incidencia.dominio.Resolucion;
import com.didekin.retrofit.IncidenciaServEndPoints;
import com.didekindroid.exception.UiException;

import java.io.EOFException;
import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

import static com.didekin.http.ErrorBean.GENERIC_ERROR;
import static com.didekindroid.AppInitializer.creator;
import static com.didekindroid.util.DaoUtil.getResponseBody;
import static com.didekindroid.util.UIutils.checkBearerToken;
import static com.didekindroid.usuariocomunidad.UserComuService.AppUserComuServ;

/**
 * User: pedro@didekin
 * Date: 18/11/15
 * Time: 13:11
 */
public final class IncidService implements IncidenciaServEndPoints {

    public static final IncidService IncidenciaServ = new IncidService();
    private final IncidenciaServEndPoints endPoint;

    private IncidService()
    {
        endPoint = creator.get().getRetrofitHandler().getService(IncidenciaServEndPoints.class);
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
            Response<Integer> response = closeIncidencia(checkBearerToken(), resolucion).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public int deleteIncidencia(long incidenciaId) throws UiException
    {
        Timber.d("deleteIncidencia()");
        try {
            Response<Integer> response = deleteIncidencia(checkBearerToken(), incidenciaId).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    /**
     * This method encapsulates the call to the UsuarioDaoRemote.AppUserComuServ method.
     */
    public List<Comunidad> getComusByUser() throws UiException
    {
        Timber.d("getComusByUser()");
        return AppUserComuServ.getComusByUser();
    }

    public int modifyIncidImportancia(IncidImportancia incidImportancia) throws UiException
    {
        Timber.d("modifyUser()");
        try {
            Response<Integer> response = modifyIncidImportancia(checkBearerToken(), incidImportancia).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public int modifyResolucion(Resolucion resolucion) throws UiException
    {
        Timber.d("modifyResolucion()");
        try {
            Response<Integer> response = modifyResolucion(checkBearerToken(), resolucion).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public int regIncidComment(IncidComment comment) throws UiException
    {
        Timber.d("regIncidComment()");
        try {
            Response<Integer> response = endPoint.regIncidComment(checkBearerToken(), comment).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public int regIncidImportancia(IncidImportancia incidImportancia) throws UiException
    {
        Timber.d("regIncidImportancia()");
        try {
            Response<Integer> response = regIncidImportancia(checkBearerToken(), incidImportancia).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public int regResolucion(Resolucion resolucion) throws UiException
    {
        Timber.d("regResolucion()");
        try {
            Response<Integer> response = regResolucion(checkBearerToken(), resolucion).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public List<IncidComment> seeCommentsByIncid(long incidenciaId) throws UiException
    {
        Timber.d("seeCommentsByIncid()");
        try {
            Response<List<IncidComment>> response = seeCommentsByIncid(checkBearerToken(), incidenciaId).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public IncidAndResolBundle seeIncidImportancia(long incidenciaId) throws UiException
    {
        Timber.d("seeIncidImportancia()");
        try {
            Response<IncidAndResolBundle> response = seeIncidImportancia(checkBearerToken(), incidenciaId).execute();
            return getResponseBody(response);
        } catch (EOFException eo) {
            return null;
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public List<IncidenciaUser> seeIncidsOpenByComu(long comunidadId) throws UiException
    {
        Timber.d("seeIncidsOpenByComu()");
        try {
            Response<List<IncidenciaUser>> response = seeIncidsOpenByComu(checkBearerToken(), comunidadId).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public List<IncidenciaUser> seeIncidsClosedByComu(long comunidadId) throws UiException
    {
        Timber.d("seeIncidsClosedByComu()");
        try {
            Response<List<IncidenciaUser>> response = seeIncidsClosedByComu(checkBearerToken(), comunidadId).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public Resolucion seeResolucion(long resolucionId) throws UiException
    {
        Timber.d("seeResolucion()");
        try {
            Response<Resolucion> response = seeResolucion(checkBearerToken(), resolucionId).execute();
            return getResponseBody(response);
        } catch (EOFException eo) {
            return null;
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public List<ImportanciaUser> seeUserComusImportancia(long incidenciaId) throws UiException
    {
        Timber.d("seeUserComusImportancia()");
        try {
            Response<List<ImportanciaUser>> response = seeUserComusImportancia(checkBearerToken(), incidenciaId).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }
}
