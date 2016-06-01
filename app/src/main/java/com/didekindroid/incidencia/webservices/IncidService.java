package com.didekindroid.incidencia.webservices;

import android.util.Log;

import com.didekin.common.controller.RetrofitHandler;
import com.didekin.incidservice.controller.IncidenciaServEndPoints;
import com.didekin.incidservice.dominio.ImportanciaUser;
import com.didekin.incidservice.dominio.IncidAndResolBundle;
import com.didekin.incidservice.dominio.IncidComment;
import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekin.incidservice.dominio.IncidenciaUser;
import com.didekin.incidservice.dominio.Resolucion;
import com.didekin.usuario.dominio.Comunidad;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.common.webservices.JksInAndroidApp;

import java.io.EOFException;
import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

import static com.didekin.common.exception.ErrorBean.GENERIC_ERROR;
import static com.didekindroid.DidekindroidApp.getBaseURL;
import static com.didekindroid.DidekindroidApp.getHttpTimeOut;
import static com.didekindroid.DidekindroidApp.getJksPassword;
import static com.didekindroid.DidekindroidApp.getJksResourceId;
import static com.didekindroid.common.utils.UIutils.checkBearerToken;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;

/**
 * User: pedro@didekin
 * Date: 18/11/15
 * Time: 13:11
 */
public class IncidService implements IncidenciaServEndPoints {

    private static final String TAG = IncidService.class.getCanonicalName();

    public static final RetrofitHandler retrofitHandler =
            new RetrofitHandler(getBaseURL(),new JksInAndroidApp(getJksPassword(), getJksResourceId()),getHttpTimeOut());
    public static final IncidService IncidenciaServ = new IncidService();
    private final IncidenciaServEndPoints endPoint;

    private IncidService()
    {
        endPoint = retrofitHandler.getService(IncidenciaServEndPoints.class);
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
        Log.d(TAG, "closeIncidencia()");
        try {
            Response<Integer> response = closeIncidencia(checkBearerToken(), resolucion).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public int deleteIncidencia(long incidenciaId) throws UiException
    {
        Log.d(TAG, "deleteIncidencia()");
        try {
            Response<Integer> response = deleteIncidencia(checkBearerToken(), incidenciaId).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
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
        try {
            Response<Integer> response = modifyIncidImportancia(checkBearerToken(), incidImportancia).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public int modifyResolucion(Resolucion resolucion) throws UiException
    {
        Log.d(TAG, "modifyResolucion()");
        try {
            Response<Integer> response = modifyResolucion(checkBearerToken(), resolucion).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public int regIncidComment(IncidComment comment) throws UiException
    {
        Log.d(TAG, "regIncidComment()");
        try {
            Response<Integer> response = endPoint.regIncidComment(checkBearerToken(), comment).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public int regIncidImportancia(IncidImportancia incidImportancia) throws UiException
    {
        Log.d(TAG, "regIncidImportancia()");
        try {
            Response<Integer> response = regIncidImportancia(checkBearerToken(), incidImportancia).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public int regResolucion(Resolucion resolucion) throws UiException
    {
        Log.d(TAG, "regResolucion()");
        try {
            Response<Integer> response = regResolucion(checkBearerToken(), resolucion).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public List<IncidComment> seeCommentsByIncid(long incidenciaId) throws UiException
    {
        Log.d(TAG, "seeCommentsByIncid()");
        try {
            Response<List<IncidComment>> response = seeCommentsByIncid(checkBearerToken(), incidenciaId).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public IncidAndResolBundle seeIncidImportancia(long incidenciaId) throws UiException
    {
        Log.d(TAG, "seeIncidImportancia()");
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
        Log.d(TAG, "seeIncidsOpenByComu()");
        try {
            Response<List<IncidenciaUser>> response = seeIncidsOpenByComu(checkBearerToken(), comunidadId).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public List<IncidenciaUser> seeIncidsClosedByComu(long comunidadId) throws UiException
    {
        Log.d(TAG, "seeIncidsClosedByComu()");
        try {
            Response<List<IncidenciaUser>> response = seeIncidsClosedByComu(checkBearerToken(), comunidadId).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

    public Resolucion seeResolucion(long resolucionId) throws UiException
    {
        Log.d(TAG, "seeResolucion()");
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
        Log.d(TAG, "seeUserComusImportancia()");
        try {
            Response<List<ImportanciaUser>> response = seeUserComusImportancia(checkBearerToken(), incidenciaId).execute();
            return getResponseBody(response);
        } catch (IOException e) {
            throw new UiException(GENERIC_ERROR);
        }
    }

//  =============================================================================
//                          HELPER METHODS
//  =============================================================================

    private static  <T> T getResponseBody(Response<T> response) throws IOException, UiException
    {
        if (response.isSuccessful()){
            return response.body();
        } else {
            throw new UiException(retrofitHandler.getErrorBean(response));
        }
    }
}
