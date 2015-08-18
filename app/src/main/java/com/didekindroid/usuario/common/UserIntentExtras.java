package com.didekindroid.usuario.common;

/**
 * User: pedro
 * Date: 10/07/15
 * Time: 10:22
 */
public enum UserIntentExtras {

    USUARIO_COMUNIDAD_REG,
    COMUNIDAD_INDEX_LIST,
    COMUNIDAD_BEAN_LIST,
    IS_COMUNIDADES_BY_USER,
    COMUNIDAD_SEARCH,
    ;

    private static final String intentPackage =  "com.didekindroid.common.ui.";

    public final String extra;

    UserIntentExtras()
    {
        extra = intentPackage.concat(this.name());
    }
}
