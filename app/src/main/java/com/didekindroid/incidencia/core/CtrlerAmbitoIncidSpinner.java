package com.didekindroid.incidencia.core;

import android.widget.Spinner;

import com.didekindroid.api.ControllerIf;
import com.didekindroid.api.CtrlerSpinner;
import com.didekindroid.api.CtrlerSpinnerIf;
import com.didekindroid.api.ObserverSpinner;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.api.ViewerSelectableIf;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Single;
import timber.log.Timber;

import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 30/03/17
 * Time: 13:20
 */

public final class CtrlerAmbitoIncidSpinner extends CtrlerSpinner<AmbitoIncidValueObj> {

    private final AtomicReference<ObserverSpinner<AmbitoIncidValueObj>> observerSpinner;

    private CtrlerAmbitoIncidSpinner(ViewerSelectableIf<Spinner, CtrlerSpinnerIf> viewerIn)
    {
        super(viewerIn);
        observerSpinner = new AtomicReference<>(null);
    }

    public static CtrlerAmbitoIncidSpinner newCtrlerAmbitoIncidSpinner(ViewerSelectableIf<Spinner, CtrlerSpinnerIf> viewerIn)
    {
        CtrlerAmbitoIncidSpinner controller = new CtrlerAmbitoIncidSpinner(viewerIn);
        controller.observerSpinner.compareAndSet(null, new ObserverSpinner<>(controller));
        return controller;
    }

    // .................................... OBSERVABLE .......................................

    public static Single<List<AmbitoIncidValueObj>> ambitoIncidList(final ViewerIf<Spinner, ? extends ControllerIf> viewer)
    {

        return Single.fromCallable(new Callable<List<AmbitoIncidValueObj>>() {
            @Override
            public List<AmbitoIncidValueObj> call() throws Exception
            {
                IncidenciaDataDbHelper dbHelper =  new IncidenciaDataDbHelper(viewer.getActivity());
                List<AmbitoIncidValueObj> list = dbHelper.getAmbitoIncidList();
                dbHelper.close();
                return list;
            }
        });
    }

    // .................................... INSTANCE METHODS .....................................

    @Override
    public boolean loadDataInSpinner()
    {
        Timber.d("loadDataInSpinner()");
        return subscriptions.add(ambitoIncidList(viewer)
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribeWith(observerSpinner.get())
        );
    }

    @Override
    public int getSelectedFromItemId(long itemId)
    {
        // Id == position.
        return (int) itemId;
    }
}
