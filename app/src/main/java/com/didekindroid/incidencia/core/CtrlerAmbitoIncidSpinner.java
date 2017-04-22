package com.didekindroid.incidencia.core;

import android.app.Activity;

import com.didekindroid.api.CtrlerSelectionList;
import com.didekindroid.api.ObserverSelectionList;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import timber.log.Timber;

import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 30/03/17
 * Time: 13:20
 */

class CtrlerAmbitoIncidSpinner extends CtrlerSelectionList<AmbitoIncidValueObj> {

    private ObserverSelectionList<AmbitoIncidValueObj> observerSpinner;

    CtrlerAmbitoIncidSpinner(ViewerAmbitoIncidSpinner viewerIn)
    {
        super(viewerIn);
    }

    static CtrlerAmbitoIncidSpinner newCtrlerAmbitoIncidSpinner(ViewerAmbitoIncidSpinner viewerIn)
    {
        CtrlerAmbitoIncidSpinner controller = new CtrlerAmbitoIncidSpinner(viewerIn);
        controller.observerSpinner = new ObserverSelectionList<>(controller);
        return controller;
    }

    // .................................... OBSERVABLE .......................................

    static Single<List<AmbitoIncidValueObj>> ambitoIncidList(final Activity activity)
    {

        return Single.fromCallable(new Callable<List<AmbitoIncidValueObj>>() {
            @Override
            public List<AmbitoIncidValueObj> call() throws Exception
            {
                IncidenciaDataDbHelper dbHelper = new IncidenciaDataDbHelper(activity);
                List<AmbitoIncidValueObj> list = dbHelper.getAmbitoIncidList();
                dbHelper.close();
                return list;
            }
        });
    }

    // .................................... INSTANCE METHODS .....................................

    @Override
    public boolean loadItemsByEntitiyId(Long... entityId)
    {
        Timber.d("loadItemsByEntitiyId()");
        return subscriptions.add(ambitoIncidList(viewer.getActivity())
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribeWith(observerSpinner)
        );
    }
}
