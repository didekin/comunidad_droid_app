package com.didekindroid.usuario.activity.utils;

/**
 * User: pedro
 * Date: 10/07/15
 * Time: 10:22
 */
public enum UserIntentExtras {

    USUARIO_COMUNIDAD_REG,
    COMUNIDAD_LIST_INDEX,
    COMUNIDAD_LIST_OBJECT,
    COMUNIDAD_ID,
    COMUNIDAD_SEARCH,
    ;

    private static final String intentPackage =  "com.didekindroid.usuario.activity.utils.UserIntentExtras.";

    public final String extra;

    UserIntentExtras()
    {
        extra = intentPackage.concat(this.name());
    }
}
