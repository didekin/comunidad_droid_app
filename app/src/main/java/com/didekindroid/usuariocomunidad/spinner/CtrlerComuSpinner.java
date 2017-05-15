package com.didekindroid.usuariocomunidad.spinner;

import com.didekindroid.api.CtrlerSelectionList;
import com.didekindroid.api.SingleObserverSelectionList;
import com.didekinlib.model.comunidad.Comunidad;

import timber.log.Timber;

import static com.didekindroid.usuariocomunidad.dao.UserComuObservable.comunidadesByUser;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 16/03/17
 * Time: 15:11
 */

class CtrlerComuSpinner extends CtrlerSelectionList<Comunidad> {

    private SingleObserverSelectionList<Comunidad> observerSpinner;

    CtrlerComuSpinner(ViewerComuSpinner viewer)
    {
        super(viewer);
    }

    static CtrlerComuSpinner newControllerComuSpinner(ViewerComuSpinner viewerIn)
    {
        CtrlerComuSpinner controller = new CtrlerComuSpinner(viewerIn);
        controller.observerSpinner = new SingleObserverSelectionList<>(controller);
        return controller;
    }

    @Override
    public boolean loadItemsByEntitiyId(Long... entityId)
    {
        Timber.d("loadItemsByEntitiyId()");
        return subscriptions.add(comunidadesByUser()
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribeWith(observerSpinner));
    }
}
