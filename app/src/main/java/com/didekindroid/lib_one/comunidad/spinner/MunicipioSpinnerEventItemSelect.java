package com.didekindroid.lib_one.comunidad.spinner;

import com.didekindroid.lib_one.api.SpinnerEventItemSelectIf;
import com.didekinlib.model.comunidad.Municipio;

import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 12/05/17
 * Time: 20:55
 */

public class MunicipioSpinnerEventItemSelect implements SpinnerEventItemSelectIf {

    private Municipio municipio;

    public MunicipioSpinnerEventItemSelect(Municipio municipio)
    {
        this.municipio = municipio;
    }

    @Override
    public long getSpinnerItemIdSelect()
    {
        Timber.d("getSpinnerItemIdSelect()");
        return municipio.getCodInProvincia();
    }

    public Municipio getMunicipio()
    {
        Timber.d("getMunicipio()");
        return municipio;
    }

    @Override
    public int hashCode()
    {
        Timber.d("hashCode()");
        return municipio.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        Timber.d("equals()");

        if (obj == null || getClass() != obj.getClass()) return false;
        MunicipioSpinnerEventItemSelect that = (MunicipioSpinnerEventItemSelect) obj;
        return municipio.equals(that.getMunicipio());
    }
}
