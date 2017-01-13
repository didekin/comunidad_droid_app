package com.didekindroid.comunidad;


import com.didekin.comunidad.Comunidad;
import com.didekin.http.ErrorBean;
import com.didekin.retrofit.ComunidadEndPoints;
import com.didekindroid.exception.UiException;

import java.io.EOFException;
import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

import static com.didekindroid.AppInitializer.creator;
import static com.didekindroid.util.DaoUtil.getResponseBody;
import static com.didekindroid.util.UIutils.checkBearerToken;

/**
 * User: pedro@didekin
 * Date: 20/11/16
 * Time: 12:55
 */
@SuppressWarnings("WeakerAccess")
public final class ComunidadService implements ComunidadEndPoints {

    public static final ComunidadService AppComuServ = new ComunidadService();
    private final ComunidadEndPoints endPoint;

    private ComunidadService()
    {
        endPoint = creator.get().getRetrofitHandler().getService(ComunidadEndPoints.class);
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
            Response<Comunidad> response = getComuData(checkBearerToken(), idComunidad).execute();
            return getResponseBody(response);
        } catch (EOFException eo) {
            return null;
        } catch (IOException e) {
           throw new UiException(ErrorBean.GENERIC_ERROR);
        }
    }
}
