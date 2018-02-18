package com.didekindroid.lib_one.util;

import android.os.Bundle;

import java.io.Serializable;

/**
 * User: pedro@didekin
 * Date: 30/03/17
 * Time: 10:50
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface BundleKey {

    String getKey();

    default Bundle getBundleForKey(Serializable extraValue)
    {
        Bundle bundle = new Bundle(1);
        bundle.putSerializable(getKey(), extraValue);
        return bundle;
    }
}
