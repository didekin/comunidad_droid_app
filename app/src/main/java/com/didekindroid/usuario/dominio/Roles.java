package com.didekindroid.usuario.dominio;

/**
 * User: pedro@didekin
 * Date: 08/06/15
 * Time: 18:03
 */
/* TODO: a incluir en el .jar común.*/
public enum Roles {

    ADMINISTRADOR("adm"),
    PRESIDENTE("pre"),
    PROPIETARIO("pro"),
    INQUILINO("inq"),;

    private String function;

    Roles(String function)
    {
        this.function = function;
    }

    public String getFunction()
    {
        return function;
    }
}
