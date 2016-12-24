package com.didekindroid.comunidad;

import com.didekin.comunidad.Comunidad;
import com.didekin.comunidad.ComunidadEndPoints;
import com.didekinaar.exception.UiException;
import com.didekindroid.exception.UiAppException;

import java.io.EOFException;
import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

import static com.didekin.common.exception.ErrorBean.GENERIC_ERROR;
import static com.didekinaar.AppInitializer.creator;
import static com.didekinaar.utils.AarDaoUtil.getResponseBody;
import static com.didekinaar.utils.UIutils.checkBearerToken;

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

    public Comunidad getComuData(long idComunidad) throws UiAppException
    {
        Timber.d("getComuData()");

        try {
            Response<Comunidad> response = getComuData(checkBearerToken(), idComunidad).execute();
            return getResponseBody(response);
        } catch (EOFException eo) {
            return null;
        } catch (IOException | UiException e) {
            throw new UiAppException(GENERIC_ERROR);
        }
    }
}
