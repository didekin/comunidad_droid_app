package com.didekindroid.comunidad.spinner;

import com.didekindroid.api.SpinnerEventItemSelectIf;
import com.didekinlib.model.comunidad.Municipio;

import timber.log.Timber;

import static com.didekindroid.util.CommonAssertionMsg.bean_fromView_should_be_initialized;
import static com.didekindroid.util.UIutils.assertTrue;

/**
 * User: pedro@didekin
 * Date: 12/05/17
 * Time: 20:55
 */

public class MunicipioSpinnerEventItemSelect implements SpinnerEventItemSelectIf<Municipio> {

    private Municipio municipio;

    public MunicipioSpinnerEventItemSelect()
    {
    }

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

    @Override
    public void setSpinnerItemIdSelect(Municipio itemSelect)
    {
        Timber.d("setSpinnerItemIdSelect()");
        assertTrue(itemSelect.getProvincia() != null, bean_fromView_should_be_initialized);
        municipio = new Municipio(itemSelect.getCodInProvincia(), itemSelect.getProvincia());
    }

    public Municipio getMunicipio()
    {
        Timber.d("getMunicipio()");
        return municipio;
    }
}
