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

    private ComunidadAutonoma comunidadAutonoma;

    public ComuAutonomaSpinnerEventItemSelect()
    {
    }

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

    @Override
    public void setSpinnerItemIdSelect(ComunidadAutonoma itemSelect)
    {
        Timber.d("setSpinnerItemIdSelect()");
        comunidadAutonoma = new ComunidadAutonoma(itemSelect.getCuId());
    }

    public ComunidadAutonoma getComunidadAutonoma()
    {
        Timber.d("getComunidadAutonoma()");
        return comunidadAutonoma;
    }
}
