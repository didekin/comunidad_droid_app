package com.didekindroid.lib_one.comunidad.spinner;

import com.didekindroid.lib_one.api.CtrlerSelectList;
import com.didekindroid.lib_one.comunidad.repository.ComunidadDbHelper;
import com.didekinlib.model.comunidad.Provincia;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.lib_one.util.UiUtil.assertTrue;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 05/05/17
 * Time: 16:31
 */

@SuppressWarnings("WeakerAccess")
public class CtrlerProvinciaSpinner extends CtrlerSelectList<Provincia> {

    // .................................... OBSERVABLE .......................................

    Single<List<Provincia>> provinciasByComAutonoma(final short comAutonomaId)
    {
        Timber.d("provinciasByComAutonoma()");
        return Single.fromCallable(() -> {
            ComunidadDbHelper dbHelper = new ComunidadDbHelper(getTkCacher().getContext());
            List<Provincia> provincias = dbHelper.getProvinciasByCA(comAutonomaId);
            dbHelper.close();
            return provincias;
        });
    }

    // .................................... INSTANCE METHODS .....................................

    @Override
    public boolean loadItemsByEntitiyId(DisposableSingleObserver<List<Provincia>> observer, Long... entityId)
    {
        Timber.d("loadItemsByEntitiyId()");
        assertTrue(entityId.length > 0, "length should be greater than zero");
        return getSubscriptions().add(provinciasByComAutonoma(entityId[0].shortValue())
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribeWith(observer)
        );
    }
}
