package com.didekindroid.api;

import java.util.Collection;

/**
 * User: pedro@didekin
 * Date: 16/03/17
 * Time: 14:23
 */
public interface CtrlerSpinnerIf<E> extends ControllerIf {

    void onSuccessLoadDataInSpinner(Collection<E> comunidades);

    boolean loadDataInSpinner();

    int getSelectedFromItemId(long itemId);

}
