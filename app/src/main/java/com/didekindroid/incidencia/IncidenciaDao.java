package com.didekindroid.incidencia;

import android.os.Bundle;

import com.didekindroid.lib_one.api.HttpInitializerIf;
import com.didekindroid.lib_one.security.AuthTkCacherIf;
import com.didekindroid.lib_one.security.SecInitializerIf;
import com.didekinlib.model.incidencia.dominio.ImportanciaUser;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.IncidComment;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;
import com.didekinlib.model.incidencia.dominio.Resolucion;
import com.didekinlib.model.incidencia.http.IncidenciaServEndPoints;

import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.Single;
import retrofit2.Response;
import timber.log.Timber;

import static com.didekindroid.incidencia.IncidBundleKey.INCID_RESOLUCION_BUNDLE;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.lib_one.HttpInitializer.httpInitializer;
import static com.didekindroid.lib_one.api.exception.UiException.uiExceptionConsumer;
import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.lib_one.util.RxJavaUtil.getRespSingleListFunction;
import static com.didekindroid.lib_one.util.RxJavaUtil.getResponseMaybeFunction;
import static com.didekindroid.lib_one.util.RxJavaUtil.getResponseSingleFunction;

/**
 * User: pedro@didekin
 * Date: 18/11/15
 * Time: 13:11
 */
public final class IncidenciaDao implements IncidenciaServEndPoints {

    public static final IncidenciaDao incidenciaDao = new IncidenciaDao(secInitializer.get(), httpInitializer.get());
    private final IncidenciaServEndPoints endPoint;
    private final AuthTkCacherIf tkCacher;

    private IncidenciaDao(SecInitializerIf secInitializerIn, HttpInitializerIf httpInitializerIn)
    {
        this(httpInitializerIn.getHttpHandler().getService(IncidenciaServEndPoints.class), secInitializerIn.getTkCacher());
    }

    public IncidenciaDao(IncidenciaServEndPoints endPoint, AuthTkCacherIf tkCacher)
    {
        this.endPoint = endPoint;
        this.tkCacher = tkCacher;
    }

    /*  ================================== IncidenciaServEndPoints implementation ============================*/

    @Override
    public Single<Response<Integer>> closeIncidencia(String authHeader, Resolucion resolucion)
    {
        return endPoint.closeIncidencia(authHeader, resolucion);
    }

    @Override
    public Single<Response<Integer>> deleteIncidencia(String authHeader, long incidenciaId)
    {
        return endPoint.deleteIncidencia(authHeader, incidenciaId);
    }

    @Override
    public Single<Response<Integer>> modifyIncidImportancia(String authHeader, IncidImportancia incidImportancia)
    {
        return endPoint.modifyIncidImportancia(authHeader, incidImportancia);
    }

    @Override
    public Single<Response<Integer>> modifyResolucion(String authHeader, Resolucion resolucion)
    {
        return endPoint.modifyResolucion(authHeader, resolucion);
    }

    @Override
    public Single<Response<Integer>> regIncidComment(String authHeader, IncidComment comment)
    {
        return endPoint.regIncidComment(authHeader, comment);
    }

    @Override
    public Single<Response<Integer>> regIncidImportancia(String authHeader, IncidImportancia incidImportancia)
    {
        return endPoint.regIncidImportancia(authHeader, incidImportancia);
    }

    @Override
    public Single<Response<Integer>> regResolucion(String authHeader, Resolucion resolucion)
    {
        return endPoint.regResolucion(authHeader, resolucion);
    }

    @Override
    public Single<Response<List<IncidComment>>> seeCommentsByIncid(String authHeader, long incidenciaId)
    {
        return endPoint.seeCommentsByIncid(authHeader, incidenciaId);
    }

    @Override
    public Single<Response<IncidAndResolBundle>> seeIncidImportancia(String authHeader, long incidenciaId)
    {
        return endPoint.seeIncidImportancia(authHeader, incidenciaId);
    }

    @Override
    public Single<Response<List<IncidenciaUser>>> seeIncidsOpenByComu(String authHeader, long comunidadId)
    {
        return endPoint.seeIncidsOpenByComu(authHeader, comunidadId);
    }

    @Override
    public Single<Response<List<IncidenciaUser>>> seeIncidsClosedByComu(String authHeader, long comunidadId)
    {
        return endPoint.seeIncidsClosedByComu(authHeader, comunidadId);
    }

    @Override
    public Maybe<Response<Resolucion>> seeResolucion(String authHeader, long incidenciaId)
    {
        Timber.d("seeResolucion(), authHeader = %s, incidenciaId = %d", authHeader, incidenciaId);
        return endPoint.seeResolucion(authHeader, incidenciaId);
    }

    @Override
    public Single<Response<List<ImportanciaUser>>> seeUserComusImportancia(String authHeader, long incidenciaId)
    {
        return endPoint.seeUserComusImportancia(authHeader, incidenciaId);
    }

//  =============================================================================
//                          CONVENIENCE METHODS
//  =============================================================================

    public Single<Integer> closeIncidencia(Resolucion resolucion)
    {
        Timber.d("closeIncidencia()");
        return tkCacher.getSingleAuthToken()
                .flatMap(authToken -> closeIncidencia(authToken, resolucion))
                .flatMap(getResponseSingleFunction())
                .doOnError(uiExceptionConsumer);
    }

    public Single<Integer> deleteIncidencia(long incidenciaId)
    {
        Timber.d("deleteIncidencia()");
        return tkCacher.getSingleAuthToken()
                .flatMap(authToken -> deleteIncidencia(authToken, incidenciaId))
                .flatMap(getResponseSingleFunction())
                .doOnError(uiExceptionConsumer);
    }

    public Single<Integer> modifyIncidImportancia(IncidImportancia incidImportancia)
    {
        Timber.d("modifyIncidImportancia()");
        return tkCacher.getSingleAuthToken()
                .flatMap(authToken -> modifyIncidImportancia(authToken, incidImportancia))
                .flatMap(getResponseSingleFunction())
                .doOnError(uiExceptionConsumer);
    }

    public Single<Integer> modifyResolucion(Resolucion resolucion)
    {
        Timber.d("modifyResolucion()");
        return tkCacher.getSingleAuthToken()
                .flatMap(authToken -> modifyResolucion(authToken, resolucion))
                .flatMap(getResponseSingleFunction())
                .doOnError(uiExceptionConsumer);
    }

    public Single<Integer> regIncidComment(IncidComment comment)
    {
        Timber.d("regIncidComment()");
        return tkCacher.getSingleAuthToken()
                .flatMap(authToken -> regIncidComment(authToken, comment))
                .flatMap(getResponseSingleFunction())
                .doOnError(uiExceptionConsumer);
    }

    public Single<Integer> regIncidImportancia(IncidImportancia incidImportancia)
    {
        Timber.d("regIncidImportancia()");
        return tkCacher.getSingleAuthToken()
                .flatMap(authToken -> regIncidImportancia(authToken, incidImportancia))
                .flatMap(getResponseSingleFunction())
                .doOnError(uiExceptionConsumer);
    }

    public Single<Integer> regResolucion(Resolucion resolucion)
    {
        Timber.d("regResolucion()");
        return tkCacher.getSingleAuthToken()
                .flatMap(authToken -> regResolucion(authToken, resolucion))
                .flatMap(getResponseSingleFunction())
                .doOnError(uiExceptionConsumer);
    }

    public Single<List<IncidComment>> seeCommentsByIncid(long incidenciaId)
    {
        Timber.d("seeCommentsByIncid()");
        return tkCacher.getSingleAuthToken()
                .flatMap(authToken -> seeCommentsByIncid(authToken, incidenciaId))
                .flatMap(getRespSingleListFunction())
                .doOnError(uiExceptionConsumer);
    }

    public Single<IncidAndResolBundle> seeIncidImportanciaRaw(long incidenciaId)
    {
        Timber.d("seeIncidImportanciaRaw()");
        return tkCacher.getSingleAuthToken()
                .flatMap(authToken -> seeIncidImportancia(authToken, incidenciaId))
                .flatMap(getResponseSingleFunction())
                .doOnError(uiExceptionConsumer);
    }

    public Single<Bundle> seeIncidImportancia(long incidenciaId)
    {
        Timber.d("seeIncidImportancia()");
        return seeIncidImportanciaRaw(incidenciaId)
                .map(INCID_RESOLUCION_BUNDLE::getBundleForKey);
    }

    public Single<List<IncidenciaUser>> seeIncidsOpenByComu(long comunidadId)
    {
        Timber.d("seeIncidsOpenByComu()");
        return tkCacher.getSingleAuthToken()
                .flatMap(authToken -> seeIncidsOpenByComu(authToken, comunidadId))
                .flatMap(getRespSingleListFunction())
                .doOnError(uiExceptionConsumer);
    }

    public Single<List<IncidenciaUser>> seeIncidsClosedByComu(long comunidadId)
    {
        Timber.d("seeIncidsClosedByComu()");
        return tkCacher.getSingleAuthToken()
                .flatMap(authToken -> seeIncidsClosedByComu(authToken, comunidadId))
                .flatMap(getRespSingleListFunction())
                .doOnError(uiExceptionConsumer);
    }

    public Maybe<Resolucion> seeResolucionRaw(long incidenciaId)
    {
        Timber.d("seeResolucionRaw()");
        return tkCacher.getSingleAuthToken()
                .flatMapMaybe(authToken -> seeResolucion(authToken, incidenciaId))
                .flatMap(getResponseMaybeFunction())
                .doOnError(uiExceptionConsumer);
    }

    /**
     * A variant for closed incidencias, which must always had a resolucion.
     */
    public Single<Bundle> seeResolucionInBundle(long incidenciaId)
    {
        Timber.d("seeResolucionInBundle()");
        return seeResolucionRaw(incidenciaId)
                .map(INCID_RESOLUCION_OBJECT::getBundleForKey)
                .toSingle();
    }

    public Single<List<ImportanciaUser>> seeUserComusImportancia(long incidenciaId)
    {
        Timber.d("seeUserComusImportancia()");
        return tkCacher.getSingleAuthToken()
                .flatMap(authToken -> seeUserComusImportancia(authToken, incidenciaId))
                .flatMap(getRespSingleListFunction())
                .doOnError(uiExceptionConsumer);
    }
}
