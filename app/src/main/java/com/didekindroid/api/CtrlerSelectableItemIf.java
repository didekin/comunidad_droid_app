package com.didekindroid.api;

import java.io.Serializable;

/**
 * User: pedro@didekin
 * Date: 17/03/17
 * Time: 17:02
 */
public interface CtrlerSelectableItemIf<E extends Serializable,B> extends CtrlerSelectionListIf<E> {

    boolean selectItem(E item);

    void onSuccessSelectedItem(B itemBack);
}
