package com.didekindroid.comunidad.spinner;

import com.didekindroid.api.CtrlerSelectList;
import com.didekindroid.comunidad.repository.ComunidadDbHelper;
import com.didekinlib.model.comunidad.Municipio;

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
 * Time: 16:32
 */

@SuppressWarnings("WeakerAccess")
public class CtrlerMunicipioSpinner extends CtrlerSelectList<Municipio> {


    // .................................... OBSERVABLE .......................................

    Single<List<Municipio>> municipiosByProvincia(final short provinciaId)
    {
        Timber.d("municipiosByProvincia()");
        return Single.fromCallable((new Callable<List<Municipio>>() {
            @Override
            public List<Municipio> call() throws Exception
            {
                ComunidadDbHelper dbHelper = new ComunidadDbHelper(getIdentityCacher().getContext());
                List<Municipio> municipios = dbHelper.getMunicipioByProvincia(provinciaId);
                dbHelper.close();
                return municipios;
            }
        }));
    }

    @Override
    public boolean loadItemsByEntitiyId(DisposableSingleObserver<List<Municipio>> observer, Long... entityId)
    {
        Timber.d("loadItemsByEntitiyId()");
        assertTrue(entityId.length > 0, "length should be greater than zero");
        return subscriptions.add(municipiosByProvincia(entityId[0].shortValue())
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribeWith(observer)
        );
    }

    // .................................... INSTANCE METHODS .....................................


}
