package com.didekindroid.comunidad.spinner;

import com.didekindroid.api.CtrlerSelectList;
import com.didekindroid.comunidad.repository.ComunidadDbHelper;
import com.didekinlib.model.comunidad.Provincia;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.util.UIutils.assertTrue;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 05/05/17
 * Time: 16:31
 */

public class CtrlerProvinciaSpinner extends CtrlerSelectList<Provincia> {

    // .................................... OBSERVABLE .......................................

    Single<List<Provincia>> provinciasByComAutonoma(final short comAutonomaId)
    {
        Timber.d("provinciasByComAutonoma()");
        return Single.fromCallable(new Callable<List<Provincia>>() {
            @Override
            public List<Provincia> call() throws Exception
            {
                ComunidadDbHelper dbHelper = new ComunidadDbHelper(getIdentityCacher().getContext());
                List<Provincia> provincias = dbHelper.getProvinciasByCA(comAutonomaId);
                dbHelper.close();
                return provincias;
            }
        });
    }

    // .................................... INSTANCE METHODS .....................................

    @Override
    public boolean loadItemsByEntitiyId(DisposableSingleObserver<List<Provincia>> observer, Long... entityId)
    {
        Timber.d("loadItemsByEntitiyId()");
        assertTrue(entityId.length > 0, "length should be greater than zero");
        return subscriptions.add(provinciasByComAutonoma(entityId[0].shortValue())
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribeWith(observer)
        );
    }
}
