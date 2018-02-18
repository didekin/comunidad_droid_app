package com.didekindroid.comunidad.util;

import android.os.Bundle;

import com.didekindroid.lib_one.util.BundleKey;

import java.io.Serializable;

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
        public Bundle getBundleForKey(Serializable comunidadId)
        {
            Bundle bundle = new Bundle(1);
            bundle.putLong(key, Long.class.cast(comunidadId));
            return bundle;
        }
    },
    COMUNIDAD_SEARCH,;

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
