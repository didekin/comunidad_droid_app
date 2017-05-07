package com.didekindroid.comunidad.spinner;

import android.app.Activity;
import android.widget.AdapterView;

import com.didekindroid.api.CtrlerSelectionList;
import com.didekindroid.api.ObserverSelectionList;
import com.didekindroid.api.ViewerSelectionList;
import com.didekindroid.comunidad.repository.ComunidadDbHelper;
import com.didekinlib.model.comunidad.Municipio;
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
 * Time: 16:32
 */

class CtrlerMunicipioSpinner extends CtrlerSelectionList<Municipio> {

    ObserverSelectionList<Municipio> observerSpinner;

    CtrlerMunicipioSpinner(ViewerSelectionList<? extends AdapterView, CtrlerSelectionList<Municipio>, Municipio> viewer)
    {
        super(viewer);
    }

    static CtrlerMunicipioSpinner newCtrlerMunicipioSpinner(ViewerMunicipioSpinner viewer){
        Timber.d("newCtrlerMunicipioSpinner()");
        CtrlerMunicipioSpinner controller = new CtrlerMunicipioSpinner(viewer);
        controller.observerSpinner = new ObserverSelectionList<>(controller);
        return controller;
    }

    // .................................... OBSERVABLE .......................................

    static Single<List<Municipio>> municipiosByProvincia(final Activity activity, final short provinciaId){
        Timber.d("municipiosByProvincia()");
        return Single.fromCallable(new Callable<List<Municipio>>() {
            @Override
            public List<Municipio> call() throws Exception
            {
                ComunidadDbHelper dbHelper = new ComunidadDbHelper(activity);
                List<Municipio> municipios = dbHelper.getMunicipioByProvincia(provinciaId);
                dbHelper.close();
                return municipios;
            }
        });
    }

    // .................................... INSTANCE METHODS .....................................

    @Override
    public boolean loadItemsByEntitiyId(Long... entityId)
    {
       Timber.d("loadItemsByEntitiyId()");
        assertTrue(entityId.length > 0, "length should be greater than zero");
        return subscriptions.add(municipiosByProvincia(viewer.getActivity(), entityId[0].shortValue())
                .subscribeOn(io())
                .observeOn(mainThread())
                .subscribeWith(observerSpinner)
        );
    }
}
