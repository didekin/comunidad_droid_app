package com.didekindroid.incidencia.list;

import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import com.didekindroid.ControllerAbs;
import com.didekindroid.incidencia.list.ManagerIncidSeeIf.ControllerIncidSeeIf;
import com.didekindroid.incidencia.list.ManagerIncidSeeIf.ReactorIncidSeeIf;
import com.didekindroid.incidencia.list.ManagerIncidSeeIf.ViewerIncidSeeIf;
import com.didekindroid.usuario.firebase.ViewerFirebaseTokenIf;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;

import java.util.List;

import timber.log.Timber;

import static com.didekindroid.incidencia.list.ReactorIncidSee.incidSeeReactor;
import static com.didekindroid.incidencia.utils.IncidenciaAssertionMsg.incidenciaId_should_be_initialized;
import static com.didekindroid.util.UIutils.assertTrue;

/**
 * User: pedro@didekin
 * Date: 16/02/17
 * Time: 17:56
 */
class ControllerIncidOpenSee extends ControllerAbs implements ControllerIncidSeeIf<IncidAndResolBundle> {

    final ViewerIncidSeeIf<IncidAndResolBundle> viewer;
    private final ReactorIncidSeeIf reactor;
    final ArrayAdapter<IncidenciaUser> adapter;

    ControllerIncidOpenSee(ViewerIncidSeeIf<IncidAndResolBundle> viewer)
    {
        this(viewer, incidSeeReactor);
    }

    ControllerIncidOpenSee(ViewerIncidSeeIf<IncidAndResolBundle> viewer, ReactorIncidSeeIf reactor)
    {
        super();
        this.viewer = viewer;
        this.reactor = reactor;
        adapter = new AdapterIncidSeeOpenByComu(viewer.getManager());
    }

    @Override
    public ViewerFirebaseTokenIf getViewer()
    {
        Timber.d("getViewer()");
        return viewer;
    }

    @Override
    public void loadIncidsByComu(long comunidadId)
    {
        Timber.d("loadIncidsByComu()");
        assertTrue(comunidadId > 0L, "Comunidad ID should be greater than 0");
        reactor.seeIncidOpenList(this, comunidadId);
    }

    @Override
    public void dealWithIncidSelected(@NonNull Incidencia incidencia)
    {
        Timber.d("dealWithIncidSelected()");
        assertTrue(incidencia.getIncidenciaId() > 0L, incidenciaId_should_be_initialized);
        reactor.seeIncidImportancia(this, incidencia);
    }

    @Override
    public void processBackLoadIncidsByComu(List<IncidenciaUser> incidCloseList)
    {
        Timber.d("processBackLoadIncidsByComu()");
        adapter.clear();
        adapter.addAll(incidCloseList);
        viewer.getViewInViewer().setAdapter(adapter);
    }

    @Override
    public void processBackDealWithIncidencia(@NonNull IncidAndResolBundle incidAndResolBundle)
    {
        Timber.d("processBackDealWithIncidencia()");
        viewer.replaceView(incidAndResolBundle);
    }
}
