package com.didekindroid.usuariocomunidad;

import com.didekindroid.lib_one.util.BundleKey;

/**
 * User: pedro@didekin
 * Date: 18/11/16
 * Time: 09:54
 */

public enum UserComuBundleKey implements BundleKey {

    USERCOMU_LIST_OBJECT,
    ;

    public static final String intentPackage =  "UserComuBundleKey.";

    public final String key;

    UserComuBundleKey()
    {
        key = intentPackage.concat(name());
    }

    @Override
    public String getKey()
    {
        return key;
    }
}
