package com.didekindroid.incidencia.list;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import com.didekindroid.ControllerIdentityAbs;
import com.didekindroid.incidencia.list.ManagerIncidSeeIf.ControllerIncidSeeIf;
import com.didekindroid.incidencia.list.ManagerIncidSeeIf.ReactorIncidSeeIf;
import com.didekindroid.incidencia.list.ManagerIncidSeeIf.ViewerIncidSeeIf;
import com.didekindroid.security.IdentityCacher;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import timber.log.Timber;

import static com.didekindroid.incidencia.list.ReactorIncidSee.incidSeeReactor;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.util.AppBundleKey.IS_MENU_IN_FRAGMENT_FLAG;
import static com.didekindroid.util.UIutils.assertTrue;

/**
 * User: pedro@didekin
 * Date: 13/02/17
 * Time: 17:15
 */

class ControllerIncidCloseSee extends ControllerIdentityAbs implements ControllerIncidSeeIf<Resolucion> {

    private final ReactorIncidSeeIf reactor;
    final AtomicReference<Incidencia> atomicIncidencia;
    final ArrayAdapter<IncidenciaUser> adapter;
    final ViewerIncidSeeIf<Bundle> viewer;

    ControllerIncidCloseSee(ViewerIncidSeeIf<Bundle> incidViewer)
    {
        this(incidViewer, incidSeeReactor, TKhandler);
    }

    @SuppressWarnings("WeakerAccess")
    ControllerIncidCloseSee(ViewerIncidSeeIf<Bundle> viewer, ReactorIncidSeeIf reactor, IdentityCacher identityCacher)
    {
        super(identityCacher);
        this.viewer = viewer;
        this.reactor = reactor;
        atomicIncidencia = new AtomicReference<>();
        adapter = new AdapterIncidSeeClosedByComu(viewer.getManager().getActivity());
    }

    @Override
    public ViewerIncidSeeIf getViewer()
    {
        Timber.d("getViewer()");
        return viewer;
    }

    @Override
    public void loadIncidsByComu(long comunidadId)
    {
        Timber.d("loadIncidsByComu()");
        assertTrue(comunidadId > 0L, "Comunidad ID should be greater than 0");
        reactor.seeIncidClosedList(this, comunidadId);
    }

    @Override
    public void dealWithIncidSelected(@NonNull Incidencia incidencia)
    {
        Timber.d("dealWithIncidSelected()");
        atomicIncidencia.set(incidencia);
        reactor.seeResolucion(this, incidencia);
    }

    @Override    // We control the non-null in the reactor.
    public void processBackLoadIncidsByComu(@NonNull List<IncidenciaUser> incidCloseList)
    {
        Timber.d("onPostExecute()");
        adapter.clear();
        adapter.addAll(incidCloseList);
        viewer.getViewInViewer().setAdapter(adapter);
    }

    @Override
    public void processBackDealWithIncidencia(@NonNull Resolucion resolucion)
    {
        Timber.d("processBackDealWithIncidencia()");
        Bundle bundle = new Bundle();
        bundle.putBoolean(IS_MENU_IN_FRAGMENT_FLAG.key, true);
        bundle.putSerializable(INCIDENCIA_OBJECT.key, atomicIncidencia.get());
        bundle.putSerializable(INCID_RESOLUCION_OBJECT.key, resolucion);
        // Switch fragment here.
        viewer.getManager().replaceRootView(bundle);
    }
}
