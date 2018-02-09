package com.didekindroid.lib_one.util;

import android.os.Bundle;

/**
 * User: pedro@didekin
 * Date: 30/03/17
 * Time: 10:50
 */

@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface BundleKey {

    String getKey();

    @SuppressWarnings("unused")
    default Bundle getBundleForKey(Object extraValue)
    {
        return new Bundle(0);
    }
}
