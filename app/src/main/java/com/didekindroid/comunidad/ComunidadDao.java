package com.didekindroid.comunidad;

import com.didekindroid.lib_one.api.HttpInitializerIf;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.security.IdentityCacherIf;
import com.didekindroid.lib_one.security.SecInitializerIf;
import com.didekinlib.http.comunidad.ComunidadEndPoints;
import com.didekinlib.http.exception.ErrorBean;
import com.didekinlib.model.comunidad.Comunidad;

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
 * Date: 20/11/16
 * Time: 12:55
 */
@SuppressWarnings("WeakerAccess")
public final class ComunidadDao implements ComunidadEndPoints {

    public static final ComunidadDao comunidadDao = new ComunidadDao(secInitializer.get(), httpInitializer.get());
    private final ComunidadEndPoints endPoint;
    private final IdentityCacherIf tkCacher;

    private ComunidadDao(SecInitializerIf secInitializerIn, HttpInitializerIf httpInitializerIn)
    {
        tkCacher = secInitializerIn.getTkCacher();
        endPoint = httpInitializerIn.getHttpHandler().getService(ComunidadEndPoints.class);
    }

    //  ================================== ComunidadEndPoints implementation ============================

    @Override
    public Call<Comunidad> getComuData(String accessToken, long idComunidad)
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
    public Call<List<Comunidad>> searchComunidades(Comunidad comunidad)
    {
        Timber.d("searchComunidades()");
        return endPoint.searchComunidades(comunidad);
    }

//  =============================================================================
//                          CONVENIENCE METHODS
//  =============================================================================

    public Comunidad getComuData(long idComunidad) throws UiException
    {
        Timber.d("getComuData()");

        try {
            Response<Comunidad> response = getComuData(tkCacher.checkBearerTokenInCache(), idComunidad).execute();
            return httpInitializer.get().getResponseBody(response);
        } catch (EOFException eo) {
            return null;
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }
}
