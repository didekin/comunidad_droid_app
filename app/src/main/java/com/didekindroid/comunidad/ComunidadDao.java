package com.didekindroid.comunidad;

import com.didekindroid.lib_one.api.HttpInitializerIf;
import com.didekindroid.lib_one.security.AuthTkCacherIf;
import com.didekindroid.lib_one.security.SecInitializerIf;
import com.didekindroid.lib_one.usuario.dao.AppIdHelper;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.comunidad.http.ComunidadEndPoints;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Response;
import timber.log.Timber;

import static com.didekindroid.lib_one.HttpInitializer.httpInitializer;
import static com.didekindroid.lib_one.api.exception.UiException.uiExceptionConsumer;
import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.lib_one.usuario.dao.AppIdHelper.appIdSingle;
import static com.didekindroid.lib_one.util.RxJavaUtil.getRespSingleListFunction;
import static com.didekindroid.lib_one.util.RxJavaUtil.getResponseSingleFunction;

/**
 * User: pedro@didekin
 * Date: 20/11/16
 * Time: 12:55
 */
public final class ComunidadDao implements ComunidadEndPoints {

    public static final ComunidadDao comunidadDao = new ComunidadDao(secInitializer.get(), httpInitializer.get());
    private final ComunidadEndPoints endPoint;
    private final AuthTkCacherIf tkCacher;
    private final AppIdHelper idHelper;

    private ComunidadDao(SecInitializerIf secInitializerIn, HttpInitializerIf httpInitializerIn)
    {
        tkCacher = secInitializerIn.getTkCacher();
        endPoint = httpInitializerIn.getHttpHandler().getService(ComunidadEndPoints.class);
        idHelper = appIdSingle;
    }

    public AuthTkCacherIf getTkCacher()
    {
        return tkCacher;
    }

    //  ================================== ComunidadEndPoints implementation ============================

    @Override
    public Single<Response<Comunidad>> getComuData(String accessToken, long idComunidad)
    {
        return endPoint.getComuData(accessToken, idComunidad);
    }

    /**
     * An object comunidad is used as search criterium, with the fields:
     * -- tipoVia.
     * -- nombreVia.
     * -- numero.
     * -- sufijoNumero (it can be an empty string).
     * -- municipio with codInProvincia and provinciaId.
     */
    @Override
    public Single<Response<List<Comunidad>>> searchComunidades(Comunidad comunidad)
    {
        Timber.d("searchComunidades()");
        return endPoint.searchComunidades(comunidad);
    }

//  =============================================================================
//                          CONVENIENCE METHODS
//  =============================================================================

    public Single<Comunidad> getComuData(long idComunidad)
    {
        Timber.d("getComuData()");
        return idHelper.getTokenSingle()
                .flatMap(gcmToken -> getComuData(tkCacher.doAuthHeaderStr(gcmToken), idComunidad))
                .flatMap(getResponseSingleFunction())
                .doOnError(uiExceptionConsumer);
    }

    public Single<List<Comunidad>> searchInComunidades(Comunidad comunidad)
    {
        Timber.d("getComuData()");
        return searchComunidades(comunidad)
                .flatMap(getRespSingleListFunction())
                .doOnError(uiExceptionConsumer);
    }
}
