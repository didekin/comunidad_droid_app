package com.didekindroid.incidencia.list;

import com.didekinlib.model.incidencia.dominio.IncidenciaUser;

import java.util.List;

import io.reactivex.observers.DisposableMaybeObserver;
import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 08/03/17
 * Time: 12:03
 */
class IncidListObserver extends DisposableMaybeObserver<List<IncidenciaUser>> {

    private final ManagerIncidSeeIf.ControllerIncidSeeIf controller;

    IncidListObserver(ManagerIncidSeeIf.ControllerIncidSeeIf controller)
    {
        this.controller = controller;
    }

    @Override
    public void onSuccess(List<IncidenciaUser> incidenciaUserList)
    {
        Timber.d("onSuccess(), Thread for subscriber: %s", Thread.currentThread().getName());
        controller.processBackLoadIncidsByComu(incidenciaUserList);
    }

    @Override
    public void onError(Throwable e)
    {
        Timber.d("onError(), Thread for subscriber: %s", Thread.currentThread().getName());
        controller.processReactorError(e);
    }

    @Override
    public void onComplete()
    {
        Timber.d("onComplete()");
        // Do nothing in the controller.
    }
}
