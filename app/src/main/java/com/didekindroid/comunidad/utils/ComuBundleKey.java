package com.didekindroid.comunidad.utils;

import com.didekindroid.util.BundleKey;

/**
 * User: pedro@didekin
 * Date: 18/11/16
 * Time: 09:54
 */

public enum ComuBundleKey implements BundleKey {

    COMUNIDAD_LIST,
    COMUNIDAD_LIST_INDEX,
    COMUNIDAD_LIST_OBJECT,
    COMUNIDAD_ID,
    COMUNIDAD_SEARCH,
    COMUNIDAD_AUTONOMA_ID,
    MUNICIPIO_SPINNER_EVENT,
    PROVINCIA_ID,
    TIPO_VIA_ID,;

    public static final String intentPackage = "ComuBundleKey.";

    public final String key;

    ComuBundleKey()
    {
        key = intentPackage.concat(name());
    }

    @Override
    public String getKey()
    {
        return key;
    }
}
