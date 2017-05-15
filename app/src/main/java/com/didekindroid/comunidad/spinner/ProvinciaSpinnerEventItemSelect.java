package com.didekindroid.comunidad.spinner;

import com.didekindroid.api.SpinnerEventItemSelectIf;
import com.didekinlib.model.comunidad.Provincia;

import timber.log.Timber;

import static com.didekindroid.util.CommonAssertionMsg.bean_fromView_should_be_initialized;
import static com.didekindroid.util.UIutils.assertTrue;

/**
 * User: pedro@didekin
 * Date: 12/05/17
 * Time: 20:36
 */

public class ProvinciaSpinnerEventItemSelect implements SpinnerEventItemSelectIf<Provincia> {

    private Provincia provincia;

    public ProvinciaSpinnerEventItemSelect()
    {
    }

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

    @Override
    public void setSpinnerItemIdSelect(Provincia itemSelect)
    {
        Timber.d("setSpinnerItemIdSelect()");
        assertTrue(itemSelect.getComunidadAutonoma() != null, bean_fromView_should_be_initialized);
        provincia = new Provincia(itemSelect.getComunidadAutonoma(), itemSelect.getProvinciaId(), itemSelect.getNombre());
    }

    public Provincia getProvincia()
    {
        Timber.d("getProvincia()");
        return provincia;
    }
}
