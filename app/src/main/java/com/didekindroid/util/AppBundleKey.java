package com.didekindroid.util;

/**
 * User: pedro@didekin
 * Date: 18/11/16
 * Time: 09:54
 */

public enum AppBundleKey {

    IS_MENU_IN_FRAGMENT_FLAG,
    ;

    private static final String intentPackage =  "ComuBundleKey.";

    public final String key;

    AppBundleKey()
    {
        key = intentPackage.concat(this.name());
    }
}
