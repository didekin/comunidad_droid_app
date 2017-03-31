package com.didekindroid.usuario;

import com.didekindroid.util.BundleKey;

/**
 * User: pedro@didekin
 * Date: 03/02/17
 * Time: 10:22
 */

public enum UsuarioBundleKey implements BundleKey {

    login_counter_atomic_int,
    user_name,
    usuario_object,
    ;

    public final String key;

    UsuarioBundleKey()
    {
        key = UsuarioBundleKey.class.getName().concat(this.name());
    }

    @Override
    public String getKey()
    {
        return key;
    }
}
