package com.didekindroid.lib_one.api;

import java.io.Serializable;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.observers.DisposableSingleObserver;

/**
 * User: pedro@didekin
 * Date: 24/10/2017
 * Time: 10:39
 */

public interface ControllerListIf extends ControllerIf {
    <E extends Serializable> boolean loadItemsByEntitiyId(Single<List<E>> singleObservable, DisposableSingleObserver<List<E>> observer, long entityId);
}
