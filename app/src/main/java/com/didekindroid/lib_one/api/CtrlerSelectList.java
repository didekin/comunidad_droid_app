package com.didekindroid.lib_one.api;

import android.os.Bundle;

import java.io.Serializable;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 27/05/17
 * Time: 14:57
 */

@SuppressWarnings({"AbstractClassNeverImplemented", "AbstractClassExtendsConcreteClass"})
public abstract class CtrlerSelectList<E extends Serializable> extends Controller implements CtrlerSelectListIf<E> {

    @Override
    public boolean selectItem(DisposableSingleObserver<Bundle> observer, E item)
    {
        Timber.d("selectItem()");
        return false;
    }
}
