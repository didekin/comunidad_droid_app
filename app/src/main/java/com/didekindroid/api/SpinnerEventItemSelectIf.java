package com.didekindroid.api;

import java.io.Serializable;

/**
 * User: pedro@didekin
 * Date: 12/05/17
 * Time: 17:31
 *
 * This class encapsulates spinner selection.
 */

public interface SpinnerEventItemSelectIf<E> extends Serializable {
    long getSpinnerItemIdSelect();
}