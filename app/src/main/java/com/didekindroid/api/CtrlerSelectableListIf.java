package com.didekindroid.api;

/**
 * User: pedro@didekin
 * Date: 17/03/17
 * Time: 17:02
 */
public interface CtrlerSelectableListIf<E,B> extends CtrlerListIf<E> {

    boolean dealWithSelectedItem(E item);

    void onSuccessDealSelectedItem(B itemBack);
}
