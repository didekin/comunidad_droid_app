package com.didekindroid.incidencia.list;

import android.os.Bundle;
import android.widget.ListView;

import com.didekindroid.ManagerIf;
import com.didekindroid.ViewerWithSelectIf;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import java.io.Serializable;
import java.util.List;

import io.reactivex.observers.DisposableMaybeObserver;
import timber.log.Timber;

/**
 * User: pedro@didekin
 * Date: 12/01/16
 * Time: 12:16
 */
interface ManagerIncidSeeIf<B> extends ManagerIf<B> {

    /**
     * To allow for controller injection in the viewer from the manager.
     */
    ControllerIncidSeeIf getController();

    // .................... VIEWER .....................

    interface ViewerIncidSeeIf<B> extends ViewerWithSelectIf<ListView, B> {

        void doIncidListView(Bundle savedState);
    }

    // ................. CONTROLLER ....................

    interface ControllerIncidSeeIf<S extends Serializable> extends ControllerIf {

        void loadIncidsByComu(long comunidadId);

        void processBackLoadIncidsByComu(List<IncidenciaUser> incidList);

        void dealWithIncidSelected(Incidencia incidencia);

        void processBackDealWithIncidencia(S itemBack);
    }

    // .............................. REACTORS ..................................

    interface ReactorIncidSeeIf {

        boolean seeResolucion(ControllerIncidSeeIf<Resolucion> controller, Incidencia incidencia);

        boolean seeIncidClosedList(ControllerIncidSeeIf controller, long comunidadId);

        boolean seeIncidOpenList(ControllerIncidSeeIf controller, long comunidadId);

        boolean seeIncidImportancia(ControllerIncidSeeIf<IncidAndResolBundle> controller, Incidencia incidencia);
    }

    // .............................. SUBSCRIBERS ..................................

    class IncidListObserver extends DisposableMaybeObserver<List<IncidenciaUser>> {

        final ControllerIncidSeeIf controller;

        IncidListObserver(ControllerIncidSeeIf controller)
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
}
