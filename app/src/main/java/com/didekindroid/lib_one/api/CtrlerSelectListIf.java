package com.didekindroid.lib_one.api;

import android.os.Bundle;

import java.io.Serializable;
import java.util.List;

import io.reactivex.observers.DisposableSingleObserver;

/**
 * User: pedro@didekin
 * Date: 22/03/17
 * Time: 12:52
 */
public interface CtrlerSelectListIf<E extends Serializable> extends ControllerIf {

    boolean loadItemsByEntitiyId(DisposableSingleObserver<List<E>> observer, Long... entityId);
    boolean selectItem(DisposableSingleObserver<Bundle> observer, E item);
}
