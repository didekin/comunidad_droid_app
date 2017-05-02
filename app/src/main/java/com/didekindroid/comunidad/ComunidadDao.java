package com.didekindroid.comunidad;

import com.didekindroid.exception.UiException;
import com.didekindroid.security.IdentityCacher;
import com.didekinlib.http.ErrorBean;
import com.didekinlib.http.retrofit.ComunidadEndPoints;
import com.didekinlib.model.comunidad.Comunidad;

import java.io.EOFException;
import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

import static com.didekindroid.AppInitializer.creator;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.util.DaoUtil.getResponseBody;
import static com.didekinlib.http.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;

/**
 * User: pedro@didekin
 * Date: 20/11/16
 * Time: 12:55
 */
@SuppressWarnings("WeakerAccess")
public final class ComunidadDao implements ComunidadEndPoints {

    public static final ComunidadDao comunidadDao = new ComunidadDao();
    private final ComunidadEndPoints endPoint;
    private final IdentityCacher identityCacher;

    private ComunidadDao()
    {
        this(creator.get().getRetrofitHandler().getService(ComunidadEndPoints.class), TKhandler);
    }

    public ComunidadDao(ComunidadEndPoints endPoint, IdentityCacher identityCacher)
    {
        this.endPoint = endPoint;
        this.identityCacher = identityCacher;
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
            return getResponseBody(response);
        } catch (EOFException eo) {
            return null;
        } catch (IOException e) {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }
}
