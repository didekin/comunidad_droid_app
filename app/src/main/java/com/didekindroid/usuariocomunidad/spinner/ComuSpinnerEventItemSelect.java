package com.didekindroid.usuariocomunidad.spinner;

import com.didekindroid.lib_one.api.SpinnerEventItemSelectIf;
import com.didekinlib.model.comunidad.Comunidad;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 03/04/17
 * Time: 14:17
 */

public class ComuSpinnerEventItemSelect implements SpinnerEventItemSelectIf {

    private Comunidad comunidadSelect;

    public ComuSpinnerEventItemSelect()
    {
    }

    public ComuSpinnerEventItemSelect(final Comunidad comunidad)
    {
        comunidadSelect = comunidad;
    }

    @Override
    public long getSpinnerItemIdSelect()
    {
        Timber.d("getSpinnerItemIdSelect()");
        return comunidadSelect != null ? comunidadSelect.getC_Id() : 0L;
    }
}
