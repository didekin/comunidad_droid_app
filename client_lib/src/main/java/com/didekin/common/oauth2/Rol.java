package com.didekin.common.oauth2;

import java.lang.String;
import java.util.HashMap;
import java.util.Map;

/**
 * User: pedro@didekin
 * Date: 29/05/15
 * Time: 13:28
 */
@SuppressWarnings("unused")
public enum Rol {

    ADMINISTRADOR("adm", "admon"),
    PRESIDENTE("pre", ADMINISTRADOR.authority),
    PROPIETARIO("pro", "user"),
    INQUILINO("inq", PROPIETARIO.authority),;

    private static final Map<String, Rol> mapFuntionToRol = new HashMap<>();

    static {
        for (Rol rol : values()) {
            mapFuntionToRol.put(rol.function, rol);
        }
    }

    public final String function;
    public final String authority;

    Rol(String function, String authority)
    {
        this.function = function;
        this.authority = authority;
    }

    public static Rol getRolFromFunction(String rolFunction)
    {
        return mapFuntionToRol.get(rolFunction);
    }

    public static boolean hasFunctionAdmonPowers(String function){
        return getRolFromFunction(function).authority.equals("admon");
    }
}
