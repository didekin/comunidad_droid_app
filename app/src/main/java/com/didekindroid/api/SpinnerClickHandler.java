package com.didekindroid.api;

import android.support.annotation.Nullable;

/**
 * User: pedro@didekin
 * Date: 17/04/17
 * Time: 17:09
 * <p>
 * Implementations processes spinner selection events in futher related actions.
 */
public interface SpinnerClickHandler {
    long doOnClickItemId(long itemId, @Nullable Class<? extends ViewerSelectionList> viewerSourceEvent);
}
