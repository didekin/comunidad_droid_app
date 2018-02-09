package com.didekindroid.lib_one.api;

import android.support.annotation.NonNull;

/**
 * User: pedro@didekin
 * Date: 17/04/17
 * Time: 17:09
 * <p>
 * Implementations process spinner selection events in futher related actions.
 */

@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface SpinnerEventListener {
    void doOnClickItemId(@NonNull SpinnerEventItemSelectIf spinnerEventItemSelect);
}
