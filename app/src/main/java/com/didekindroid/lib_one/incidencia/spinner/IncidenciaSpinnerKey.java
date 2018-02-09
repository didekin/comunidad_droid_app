package com.didekindroid.lib_one.incidencia.spinner;

import com.didekindroid.lib_one.util.BundleKey;

/**
 * User: pedro@didekin
 * Date: 01/02/2018
 * Time: 18:12
 */

public enum IncidenciaSpinnerKey implements BundleKey {

    AMBITO_INCIDENCIA_POSITION,;

    public static final String intentPackage = "IncidenciaSpinner.";

    public final String key;

    IncidenciaSpinnerKey()
    {
        key = intentPackage.concat(name());
    }

    @Override
    public String getKey()
    {
        return key;
    }
}
