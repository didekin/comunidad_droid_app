package com.didekindroid.usuariocomunidad.spinner;

import android.widget.Spinner;

import com.didekindroid.api.CtrlerSpinner;
import com.didekindroid.api.CtrlerSpinnerIf;
import com.didekindroid.api.ObserverSpinner;
import com.didekindroid.api.ViewerSelectableIf;
import com.didekinlib.model.comunidad.Comunidad;

import java.util.concurrent.atomic.AtomicReference;

import timber.log.Timber;

import static com.didekindroid.usuariocomunidad.dao.UserComuObservable.comunidadesByUser;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 16/03/17
 * Time: 15:11
 */

public final class CtrlerComuSpinner extends CtrlerSpinner<Comunidad> {

    private final AtomicReference<ObserverSpinner<Comunidad>> observerSpinner;

    private CtrlerComuSpinner(ViewerSelectableIf<Spinner, CtrlerSpinnerIf> viewerIn)
    {
        super(viewerIn);
        observerSpinner = new AtomicReference<>(null);
    }

    static CtrlerSpinnerIf newControllerComuSpinner(ViewerSelectableIf<Spinner, CtrlerSpinnerIf> viewerIn)
    {
        CtrlerComuSpinner controller = new CtrlerComuSpinner(viewerIn);
        controller.observerSpinner.compareAndSet(null, new ObserverSpinner<>(controller));
        return controller;
    }

    @Override
    public boolean loadDataInSpinner()
    {
        Timber.d("loadDataInSpinner()");
        return subscriptions.add(comunidadesByUser()
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribeWith(observerSpinner.get()));
    }

    @Override
    public int getSelectedFromItemId(final long itemId)
    {
        int position = 0;
        boolean isFound = false;
        if (itemId > 0L) {
            long comunidadIdIn;
            do {
                comunidadIdIn = ((Comunidad) getSpinnerView().getItemAtPosition(position)).getC_Id();
                if (comunidadIdIn == itemId) {
                    isFound = true;
                    break;
                }
            } while (++position < getSpinnerView().getCount());
        }
        // Si no encontramos la comuidad, index = 0.
        return isFound ? position : 0;
    }
}
