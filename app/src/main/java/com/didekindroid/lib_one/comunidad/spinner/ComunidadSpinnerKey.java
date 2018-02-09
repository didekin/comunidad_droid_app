package com.didekindroid.lib_one.comunidad.spinner;

import com.didekindroid.lib_one.util.BundleKey;

/**
 * User: pedro@didekin
 * Date: 01/02/2018
 * Time: 18:12
 */

public enum ComunidadSpinnerKey implements BundleKey {

    COMUNIDAD_AUTONOMA_ID,
    MUNICIPIO_SPINNER_EVENT,
    PROVINCIA_ID,
    TIPO_VIA_ID,;

    public static final String intentPackage = "ComunidadSpinner.";

    public final String key;

    ComunidadSpinnerKey()
    {
        key = intentPackage.concat(name());
    }

    @Override
    public String getKey()
    {
        return key;
    }
}
