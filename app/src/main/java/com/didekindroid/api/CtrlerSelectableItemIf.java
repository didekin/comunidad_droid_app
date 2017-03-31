package com.didekindroid.api;

/**
 * User: pedro@didekin
 * Date: 17/03/17
 * Time: 17:02
 */
public interface CtrlerSelectableItemIf<E,B> extends CtrlerListIf<E> {

    boolean selectItem(E item);

    void onSuccessSelectedItem(B itemBack);
}
