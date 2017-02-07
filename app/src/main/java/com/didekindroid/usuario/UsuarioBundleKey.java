package com.didekindroid.usuario;

/**
 * User: pedro@didekin
 * Date: 03/02/17
 * Time: 10:22
 */

public enum UsuarioBundleKey {

    login_counter_atomic_int,
    user_name;

    public final String key;

    UsuarioBundleKey()
    {
        key = UsuarioBundleKey.class.getName().concat(this.name());
    }
}
