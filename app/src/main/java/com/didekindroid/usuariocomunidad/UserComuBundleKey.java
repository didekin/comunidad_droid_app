package com.didekindroid.usuariocomunidad;

/**
 * User: pedro@didekin
 * Date: 18/11/16
 * Time: 09:54
 */

public enum UserComuBundleKey {

    USERCOMU_LIST_OBJECT,
    ;

    public static final String intentPackage =  "UserComuBundleKey.";

    public final String key;

    UserComuBundleKey()
    {
        key = intentPackage.concat(this.name());
    }
}
