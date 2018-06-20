package com.didekindroid.comunidad;

import com.didekindroid.lib_one.api.HttpInitializerIf;
import com.didekindroid.lib_one.security.AuthTkCacherIf;
import com.didekindroid.lib_one.security.SecInitializerIf;
import com.didekinlib.http.comunidad.ComunidadEndPoints;
import com.didekinlib.model.comunidad.Comunidad;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Response;
import timber.log.Timber;

import static com.didekindroid.lib_one.HttpInitializer.httpInitializer;
import static com.didekindroid.lib_one.api.exception.UiExceptionIf.uiExceptionConsumer;
import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static io.reactivex.Single.just;

/**
 * User: pedro@didekin
 * Date: 20/11/16
 * Time: 12:55
 */
public final class ComunidadDao implements ComunidadEndPoints {

    public static final ComunidadDao comunidadDao = new ComunidadDao(secInitializer.get(), httpInitializer.get());
    private final ComunidadEndPoints endPoint;
    private final AuthTkCacherIf tkCacher;

    private ComunidadDao(SecInitializerIf secInitializerIn, HttpInitializerIf httpInitializerIn)
    {
        tkCacher = secInitializerIn.getTkCacher();
        endPoint = httpInitializerIn.getHttpHandler().getService(ComunidadEndPoints.class);
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
        return just(idComunidad)
                .flatMap(idCom -> getComuData(tkCacher.doAuthHeaderStr(), idCom))
                .map(httpInitializer.get()::getResponseBody)
                .doOnError(uiExceptionConsumer);
    }

    public Single<List<Comunidad>> searchInComunidades(Comunidad comunidad)    // TODO: test cuando devuelve lista vacía.
    {
        Timber.d("getComuData()");
        return searchComunidades(comunidad).map(httpInitializer.get()::getResponseBody)
                .doOnError(uiExceptionConsumer);
    }
}
