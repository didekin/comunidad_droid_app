package com.didekindroid.api;

import java.util.List;

/**
 * User: pedro@didekin
 * Date: 22/03/17
 * Time: 12:52
 */
public interface CtrlerListIf<E> extends CtrlerIdentityIf {

    boolean loadItemsByEntitiyId(long entityId);

    void onSuccessLoadItemsById(List<E> itemList);
}
