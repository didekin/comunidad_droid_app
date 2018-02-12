package com.didekindroid.comunidad;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.security.IdentityCacherIf;
import com.didekinlib.http.HttpHandler;
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
import static com.didekindroid.lib_one.security.TokenIdentityCacher.TKhandler;
import static com.didekinlib.http.exception.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;

/**
 * User: pedro@didekin
 * Date: 20/11/16
 * Time: 12:55
 */
@SuppressWarnings("WeakerAccess")
public final class ComunidadDao implements ComunidadEndPoints {

    public static final ComunidadDao comunidadDao = new ComunidadDao(TKhandler, httpInitializer.get().getHttpHandler());
    private final ComunidadEndPoints endPoint;
    private final IdentityCacherIf identityCacher;

    private ComunidadDao(IdentityCacherIf identityCacherIn, HttpHandler httpHandlerIn)
    {
        identityCacher = identityCacherIn;
        endPoint = httpHandlerIn.getService(ComunidadEndPoints.class);
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
            Response<Comunidad> response = getComuData(identityCacher.checkBearerTokenInCache(), idComunidad).execute();
            return httpInitializer.get().getResponseBody(response);
        } catch (EOFException eo) {
            return null;
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }
}
