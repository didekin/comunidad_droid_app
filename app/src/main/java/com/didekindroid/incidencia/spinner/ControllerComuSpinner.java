package com.didekindroid.incidencia.spinner;

import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.didekindroid.ControllerAbs;
import com.didekindroid.R;
import com.didekindroid.ViewerWithSelectIf;
import com.didekindroid.incidencia.spinner.ManagerComuSpinnerIf.ControllerComuSpinnerIf;
import com.didekindroid.incidencia.spinner.ManagerComuSpinnerIf.ReactorComuSpinnerIf;
import com.didekindroid.incidencia.spinner.ManagerComuSpinnerIf.ViewerComuSpinnerIf;
import com.didekinlib.model.comunidad.Comunidad;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.usuariocomunidad.dao.UserComuReactor.comunidadesByUser;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 15/02/17
 * Time: 10:28
 */
class ControllerComuSpinner extends ControllerAbs implements ControllerComuSpinnerIf {

    final ArrayAdapter<Comunidad> spinnerAdapter;
    private final AtomicReference<ReactorComuSpinnerIf> reactor;
    final Spinner comuSpinner;
    private final ViewerComuSpinnerIf viewer;

    // Package local to allow for extending it in anonymous mock subclasses.
    ControllerComuSpinner(ViewerComuSpinnerIf viewerIn)
    {
        super();
        reactor = new AtomicReference<>(null);
        viewer = viewerIn;
        spinnerAdapter = new ArrayAdapter<>(
                viewer.getActivity(),
                R.layout.app_spinner_1_dropdown_item,
                R.id.app_spinner_1_dropdown_item);
        comuSpinner = viewer.getViewInViewer();
    }

    static ControllerComuSpinnerIf newControllerComuSpinner(ViewerComuSpinnerIf viewerIn)
    {
        ControllerComuSpinner controller = new ControllerComuSpinner(viewerIn);
        controller.reactor.compareAndSet(null, new ReactorComuSpinner(controller));
        return controller;
    }

    /**
     * Variant for injection in tests of a mock reactor.
     */
    static ControllerComuSpinnerIf newControllerComuSpinner(ViewerComuSpinnerIf viewerIn, ReactorComuSpinnerIf reactor)
    {
        ControllerComuSpinner controller = new ControllerComuSpinner(viewerIn);
        controller.reactor.compareAndSet(null, reactor);
        return controller;
    }

    @Override
    public ViewerWithSelectIf getViewer()
    {
        Timber.d("getViewer()");
        return viewer;
    }

    @Override
    public void loadDataInSpinner()
    {
        Timber.d("loadDataInSpinner()");
        reactor.get().loadComunidades();
    }

    @Override
    public void processBackLoadComusInSpinner(Collection<Comunidad> comunidades)
    {
        Timber.d("processBackLoadComusInSpinner()");
        spinnerAdapter.clear();
        spinnerAdapter.addAll(comunidades);
        comuSpinner.setAdapter(spinnerAdapter);
        comuSpinner.setSelection(viewer.getComunidadSelectedIndex());
    }

    // ............................ REACTOR ..................................

    @SuppressWarnings("AnonymousInnerClassMayBeStatic")
    static final class ReactorComuSpinner implements ReactorComuSpinnerIf {

        final DisposableSingleObserver<List<Comunidad>> observer;
        final ControllerComuSpinnerIf controller;

        ReactorComuSpinner(final ControllerComuSpinnerIf controller)
        {
            this(controller, new DisposableSingleObserver<List<Comunidad>>() {

                @Override
                public void onSuccess(List<Comunidad> comunidades)
                {
                    Timber.d("onSuccess()");
                    controller.processBackLoadComusInSpinner(comunidades);
                }

                @Override
                public void onError(Throwable e)
                {
                    Timber.d("onError()");
                    controller.processReactorError(e);
                }
            });
        }

        /**
         * Variant for injection in tests of a mock observer.
         */
        ReactorComuSpinner(ControllerComuSpinnerIf controller, DisposableSingleObserver<List<Comunidad>> observerIn)
        {
            observer = observerIn;
            this.controller = controller;
        }

        // ............................ SUBSCRIPTIONS ..................................

        @Override
        public boolean loadComunidades()
        {
            Timber.d("setDataInView()");
            return controller.getSubscriptions().add(comunidadesByUser()
                    .subscribeOn(io())
                    .observeOn(mainThread())
                    .subscribeWith(observer));
        }
    }
}
