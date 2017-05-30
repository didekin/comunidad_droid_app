package com.didekindroid.comunidad.spinner;

import com.didekindroid.api.SpinnerEventItemSelectIf;
import com.didekinlib.model.comunidad.ComunidadAutonoma;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 12/05/17
 * Time: 19:09
 */

public class ComuAutonomaSpinnerEventItemSelect implements SpinnerEventItemSelectIf<ComunidadAutonoma> {

    private final ComunidadAutonoma comunidadAutonoma;

    public ComuAutonomaSpinnerEventItemSelect(ComunidadAutonoma comunidadAutonoma)
    {
        this.comunidadAutonoma = comunidadAutonoma;
    }

    @Override
    public long getSpinnerItemIdSelect()
    {
        Timber.d("getSpinnerItemIdSelect()");
        return comunidadAutonoma.getCuId();
    }

    public ComunidadAutonoma getComunidadAutonoma()
    {
        Timber.d("getComunidadAutonoma()");
        return comunidadAutonoma;
    }

    @Override
    public int hashCode()
    {
        Timber.d("hashCode()");
        return comunidadAutonoma.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        Timber.d("equals()");

        if (obj == null || getClass() != obj.getClass()) return false;

        ComuAutonomaSpinnerEventItemSelect that = (ComuAutonomaSpinnerEventItemSelect) obj;

        return comunidadAutonoma.equals(that.getComunidadAutonoma());
    }
}
