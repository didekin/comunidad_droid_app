package com.didekindroid.common.activity;

/**
 * User: pedro
 * Date: 10/07/15
 * Time: 10:22
 */
public enum SavedInstanceKey {

    INCID_RESOLUCION,
    INCID_IMPORTANCIA,
    ;

    private static final String keyPackage =  "com.didekindroid.common.activity.SavedInstanceKey.";

    public final String key;

    SavedInstanceKey()
    {
        key = keyPackage.concat(this.name());
    }
}
