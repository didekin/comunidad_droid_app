package com.didekindroid.incidencia.list;

import com.didekindroid.incidencia.list.ManagerIncidSeeIf.ControllerIncidSeeIf;
import com.didekindroid.incidencia.list.ManagerIncidSeeIf.IncidListObserver;
import com.didekindroid.incidencia.list.ManagerIncidSeeIf.ReactorIncidSeeIf;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.didekindroid.incidencia.IncidDaoRemote.incidenciaDao;
import static io.reactivex.Single.fromCallable;
import static io.reactivex.android.schedulers.AndroidSchedulers.mainThread;
import static io.reactivex.schedulers.Schedulers.io;

/**
 * User: pedro@didekin
 * Date: 13/02/17
 * Time: 17:33
 */

final class ReactorIncidSee implements ReactorIncidSeeIf {

    static final ReactorIncidSeeIf incidSeeReactor = new ReactorIncidSee();

    private ReactorIncidSee()
    {
    }

    // .................................... OBSERVABLES .................................

    static Single<Resolucion> resolucion(final Incidencia incidencia)
    {
        return fromCallable(new Callable<Resolucion>() {
            @Override
            public Resolucion call() throws Exception
            {
                return incidenciaDao.seeResolucion(incidencia.getIncidenciaId());
            }
        });
    }

    static Maybe<List<IncidenciaUser>> incidCloseList(final long comunidadId)
    {
        return Maybe.fromCallable(new Callable<List<IncidenciaUser>>() {
            @Override
            public List<IncidenciaUser> call() throws Exception
            {
                return incidenciaDao.seeIncidsClosedByComu(comunidadId);
            }
        });
    }

    static Maybe<List<IncidenciaUser>> incidOpenList(final long comunidadId)
    {
        return Maybe.fromCallable(new Callable<List<IncidenciaUser>>() {
            @Override
            public List<IncidenciaUser> call() throws Exception
            {
                return incidenciaDao.seeIncidsOpenByComu(comunidadId);
            }
        });
    }

    static Single<IncidAndResolBundle> incidImportancia(final Incidencia incidencia){
        return fromCallable(new Callable<IncidAndResolBundle>() {
            @Override
            public IncidAndResolBundle call() throws Exception
            {
                return incidenciaDao.seeIncidImportancia(incidencia.getIncidenciaId());
            }
        });
    }

    // ............................ SUBSCRIPTIONS ..................................

    @Override
    public boolean seeResolucion(final ControllerIncidSeeIf<Resolucion> controller, final Incidencia incidencia)
    {
        return controller.getSubscriptions().add(
                resolucion(incidencia)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(new ResolucionObserver<>(controller))
        );
    }

    @Override
    public boolean seeIncidClosedList(final ControllerIncidSeeIf controller, final long comunidadId)
    {
        return controller.getSubscriptions().add(
                incidCloseList(comunidadId)
                        .subscribeOn(io())
                        .observeOn(mainThread())
                        .subscribeWith(new IncidListObserver(controller))
        );
    }

    @Override
    public boolean seeIncidOpenList(final ControllerIncidSeeIf controller, final long comunidadId)
    {
        return controller.getSubscriptions().add(
                ReactorIncidSee.incidOpenList(comunidadId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(mainThread())
                        .subscribeWith(new IncidListObserver(controller))
        );
    }

    @Override
    public boolean seeIncidImportancia(final ControllerIncidSeeIf<IncidAndResolBundle> controller, Incidencia incidencia){
        return controller.getSubscriptions().add(
                ReactorIncidSee.incidImportancia(incidencia)
                        .subscribeOn(Schedulers.io())
                        .observeOn(mainThread())
                        .subscribeWith(new IncidImportanciaObserver(controller))
        );
    }

    // .............................. SUBSCRIBERS ..................................

    private static final class ResolucionObserver<T extends Serializable> extends DisposableSingleObserver<T> {

        final ControllerIncidSeeIf<T> controller;

        ResolucionObserver(ControllerIncidSeeIf<T> controller)
        {
            this.controller = controller;
        }

        @Override
        public void onSuccess(T itemBack)
        {
            Timber.d("onSuccess(), Thread for subscriber: %s", Thread.currentThread().getName());
            controller.processBackDealWithIncidencia(itemBack);
        }

        @Override
        public void onError(Throwable e)
        {
            Timber.d("onError(), Thread for subscriber: %s", Thread.currentThread().getName());
            controller.processReactorError(e);
        }
    }

    private static final class IncidImportanciaObserver extends DisposableSingleObserver<IncidAndResolBundle>{

        final ControllerIncidSeeIf<IncidAndResolBundle> controller;

        IncidImportanciaObserver(ControllerIncidSeeIf<IncidAndResolBundle> controller)
        {
            this.controller = controller;
        }

        @Override
        public void onSuccess(IncidAndResolBundle incidAndResolBundle)
        {
            Timber.d("onSuccess()");
            controller.processBackDealWithIncidencia(incidAndResolBundle);
        }

        @Override
        public void onError(Throwable e)
        {
            Timber.d("onError()");
            controller.processReactorError(e);
        }
    }
}
