package com.didekindroid.incidencia.core;

import com.didekindroid.api.Controller;
import com.didekindroid.incidencia.IncidObservable;
import com.didekindroid.security.IdentityCacher;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;

import timber.log.Timber;

import static com.didekindroid.incidencia.IncidObservable.incidImportanciaModified;
import static com.didekindroid.incidencia.IncidObservable.incidenciaDeleted;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 04/04/17
 * Time: 15:08
 */
public class CtrlerIncidRegEditFr extends Controller {

    @SuppressWarnings("WeakerAccess")
    ViewerIncidRegEdit viewerIncidRegEdit;

    public CtrlerIncidRegEditFr(ViewerIncidRegEdit viewer)
    {
        this(viewer, TKhandler);
    }

    private CtrlerIncidRegEditFr(ViewerIncidRegEdit viewer, IdentityCacher identityCacher)
    {
        super(viewer, identityCacher);
        viewerIncidRegEdit = viewer;
    }

    // .................................... INSTANCE METHODS .................................

    public boolean registerIncidImportancia(IncidImportancia incidImportancia)
    {
        Timber.d("registerIncidImportancia()");
        return subscriptions.add(
                IncidObservable.incidImportanciaRegistered(incidImportancia)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(new IncidRegEditObserver(this) {
                            @Override
                            public void onSuccess(Integer rowInserted)
                            {
                                Timber.d("onSuccess()");
                                viewerIncidRegEdit.onSuccessRegisterIncidImportancia(rowInserted);
                            }
                        })
        );
    }

    public boolean modifyIncidImportancia(IncidImportancia newIncidImportancia)
    {
        Timber.d("modifyIncidImportancia()");
        return subscriptions.add(
                incidImportanciaModified(newIncidImportancia)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(new IncidRegEditObserver(this) {
                            @Override
                            public void onSuccess(Integer rowsModified)
                            {
                                Timber.d("onSuccess()");
                                viewerIncidRegEdit.onSuccessModifyIncidImportancia(rowsModified);
                            }
                        })
        );
    }

    public boolean eraseIncidencia(Incidencia incidencia)
    {
        Timber.d("eraseIncidencia()");
        return subscriptions.add(
                incidenciaDeleted(incidencia)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(new IncidRegEditObserver(this) {
                            @Override
                            public void onSuccess(Integer rowsDeleted)
                            {
                                Timber.d("onSuccess()");
                                viewerIncidRegEdit.onSuccessEraseIncidencia(rowsDeleted);
                            }
                        })
        );
    }

    public String getAmbitoIncidDesc(short ambitoId)
    {
        Timber.d("getAmbitoIncidDesc()");
        IncidenciaDataDbHelper dbHelper = new IncidenciaDataDbHelper(viewer.getActivity());
        String ambitoDesc = dbHelper.getAmbitoDescByPk(ambitoId);
        dbHelper.close();
        return ambitoDesc;
    }
}
