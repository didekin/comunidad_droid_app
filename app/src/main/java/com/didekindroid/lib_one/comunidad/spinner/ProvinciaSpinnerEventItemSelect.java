package com.didekindroid.lib_one.comunidad.spinner;

import com.didekindroid.lib_one.api.SpinnerEventItemSelectIf;
import com.didekinlib.model.comunidad.Provincia;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 12/05/17
 * Time: 20:36
 */

public class ProvinciaSpinnerEventItemSelect implements SpinnerEventItemSelectIf {

    private Provincia provincia;

    public ProvinciaSpinnerEventItemSelect(Provincia provincia)
    {
        this.provincia = provincia;
    }

    @Override
    public long getSpinnerItemIdSelect()
    {
        Timber.d("getSpinnerItemIdSelect()");
        return provincia.getProvinciaId();
    }

    public Provincia getProvincia()
    {
        Timber.d("getProvincia()");
        return provincia;
    }

    @Override
    public int hashCode()
    {
        Timber.d("hashCode()");
        return provincia.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        Timber.d("equals()");

        if (obj == null || getClass() != obj.getClass()) return false;

        ProvinciaSpinnerEventItemSelect that = (ProvinciaSpinnerEventItemSelect) obj;

        return provincia.equals(that.getProvincia());
    }
}
