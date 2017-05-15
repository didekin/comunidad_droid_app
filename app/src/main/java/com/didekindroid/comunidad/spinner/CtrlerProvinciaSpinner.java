package com.didekindroid.comunidad.spinner;

import android.app.Activity;
import android.widget.AdapterView;

import com.didekindroid.api.CtrlerSelectionList;
import com.didekindroid.api.SingleObserverSelectionList;
import com.didekindroid.api.ViewerSelectionList;
import com.didekindroid.comunidad.repository.ComunidadDbHelper;
import com.didekinlib.model.comunidad.Provincia;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import timber.log.Timber;

import static com.didekindroid.util.UIutils.assertTrue;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 05/05/17
 * Time: 16:31
 */

class CtrlerProvinciaSpinner extends CtrlerSelectionList<Provincia> {

    CtrlerProvinciaSpinner(ViewerSelectionList<? extends AdapterView, CtrlerSelectionList<Provincia>, Provincia> viewer)
    {
        super(viewer);
    }

    static CtrlerProvinciaSpinner newCtrlerProvinciaSpinner(ViewerProvinciaSpinner viewer)
    {
        Timber.d("newCtrlerProvinciaSpinner()");
        return new CtrlerProvinciaSpinner(viewer);
    }

    // .................................... OBSERVABLE .......................................

    static Single<List<Provincia>> provinciasByComAutonoma(final Activity activity, final short comAutonomaId)
    {
        Timber.d("provinciasByComAutonoma()");
        return Single.fromCallable(new Callable<List<Provincia>>() {
            @Override
            public List<Provincia> call() throws Exception
            {
                ComunidadDbHelper dbHelper = new ComunidadDbHelper(activity);
                List<Provincia> provincias = dbHelper.getProvinciasByCA(comAutonomaId);
                dbHelper.close();
                return provincias;
            }
        });
    }

    // .................................... INSTANCE METHODS .....................................

    @Override
    public boolean loadItemsByEntitiyId(Long... entityId)
    {
        Timber.d("loadItemsByEntitiyId()");
        assertTrue(entityId.length > 0, "length should be greater than zero");
        return subscriptions.add(provinciasByComAutonoma(viewer.getActivity(), entityId[0].shortValue())
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribeWith(new SingleObserverSelectionList<>(this))
        );
    }
}
