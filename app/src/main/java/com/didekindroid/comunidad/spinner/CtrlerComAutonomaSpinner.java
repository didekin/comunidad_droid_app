package com.didekindroid.comunidad.spinner;

import android.app.Activity;

import com.didekindroid.api.CtrlerSelectionList;
import com.didekindroid.api.SingleObserverSelectionList;
import com.didekindroid.comunidad.repository.ComunidadDbHelper;
import com.didekinlib.model.comunidad.ComunidadAutonoma;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 03/05/17
 * Time: 18:51
 */

class CtrlerComAutonomaSpinner extends CtrlerSelectionList<ComunidadAutonoma> {

    CtrlerComAutonomaSpinner(ViewerComuAutonomaSpinner viewer)
    {
        super(viewer);
    }

    static CtrlerComAutonomaSpinner newCtrlerComAutonomaSpinner(ViewerComuAutonomaSpinner viewer)
    {
        Timber.d("newCtrlerComAutonomaSpinner()");
        return new CtrlerComAutonomaSpinner(viewer);
    }

    // .................................... OBSERVABLE .......................................

    static Single<List<ComunidadAutonoma>> comunidadesAutonomasList(final Activity activity)
    {
        Timber.d("comunidadesAutonomasList()");
        return Single.fromCallable(new Callable<List<ComunidadAutonoma>>() {
            @Override
            public List<ComunidadAutonoma> call() throws Exception
            {
                ComunidadDbHelper dbHelper = new ComunidadDbHelper(activity);
                List<ComunidadAutonoma> comunidades = dbHelper.getComunidadesAu();
                dbHelper.close();
                return comunidades;
            }
        });
    }

    // .................................... INSTANCE METHODS .....................................

    @Override
    public boolean loadItemsByEntitiyId(Long... entityId)
    {
        Timber.d("loadItemsByEntitiyId()");

        return subscriptions.add(comunidadesAutonomasList(viewer.getActivity())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SingleObserverSelectionList<>(this))
        );
    }
}
