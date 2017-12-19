package com.didekindroid.usuario;

import android.os.Bundle;

import com.didekindroid.util.BundleKey;

/**
 * User: pedro@didekin
 * Date: 03/02/17
 * Time: 10:22
 */

public enum UsuarioBundleKey implements BundleKey {

    login_counter_atomic_int,
    user_name {
        @Override
        public Bundle getBundleForKey(Object extraValue)
        {
            Bundle bundle = new Bundle(1);
            bundle.putString(key, String.class.cast(extraValue));
            return bundle;
        }
    },
    user_alias,
    usuario_object,;

    public final String key;

    UsuarioBundleKey()
    {
        key = UsuarioBundleKey.class.getName().concat(name());
    }

    @Override
    public String getKey()
    {
        return key;
    }
}
