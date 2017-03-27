package com.didekindroid.comunidad.spinner;

import android.widget.Spinner;

import com.didekindroid.api.CtrlerSpinner;
import com.didekindroid.api.CtrlerSpinnerIf;
import com.didekindroid.api.ObserverSpinner;
import com.didekindroid.api.ViewerSelectableIf;
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

final class CtrlerComuSpinner extends CtrlerSpinner<Comunidad> {

    private CtrlerComuSpinner(ViewerSelectableIf<Spinner, CtrlerSpinnerIf> viewerIn)
    {
        super(viewerIn);
    }

    static CtrlerSpinnerIf newControllerComuSpinner(ViewerSelectableIf<Spinner, CtrlerSpinnerIf> viewerIn)
    {
        CtrlerComuSpinner controller = new CtrlerComuSpinner(viewerIn);
        controller.atomicObserver.compareAndSet(null, new ObserverSpinner<>(controller));
        return controller;
    }

    @Override
    public boolean loadDataInSpinner()
    {
        Timber.d("loadDataInSpinner()");
        return subscriptions.add(comunidadesByUser()
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribeWith(atomicObserver.get()));
    }

    @Override
    public int getSelectedFromItemId(final long itemId)
    {
        int position = 0;
        boolean isFound = false;
        if (itemId > 0L) {
            long comunidadIdIn;
            do {
                comunidadIdIn = ((Comunidad) spinnerView.getItemAtPosition(position)).getC_Id();
                if (comunidadIdIn == itemId) {
                    isFound = true;
                    break;
                }
            } while (++position < spinnerView.getCount());
        }
        // Si no encontramos la comuidad, index = 0.
        return isFound ? position : 0;
    }
}
