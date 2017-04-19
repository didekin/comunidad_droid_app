package com.didekindroid.api;

import java.io.Serializable;
import java.util.List;

/**
 * User: pedro@didekin
 * Date: 22/03/17
 * Time: 12:52
 */
public interface CtrlerSelectionListIf<E extends Serializable> extends ControllerIf {

    boolean loadItemsByEntitiyId(Long... entityId);

    void onSuccessLoadItemsInList(List<E> itemList);
}
