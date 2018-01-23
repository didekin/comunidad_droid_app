package com.didekindroid.comunidad.utils;

import android.os.Bundle;

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
    COMUNIDAD_ID{
        @Override
        public Bundle getBundleForKey(Object comunidadId)
        {
            Bundle bundle = new Bundle(1);
            bundle.putLong(key, Long.class.cast(comunidadId));
            return bundle;
        }
    },
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
