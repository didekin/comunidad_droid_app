package com.didekindroid.incidencia.core.edit.importancia;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ListView;

import com.didekindroid.incidencia.core.CtrlerIncidenciaCore;
import com.didekindroid.lib_one.api.ObserverSingleList;
import com.didekindroid.lib_one.api.ParentViewerIf;
import com.didekindroid.lib_one.api.Viewer;
import com.didekindroid.lib_one.api.ViewerListIf;
import com.didekinlib.model.incidencia.dominio.ImportanciaUser;
import com.didekinlib.model.incidencia.dominio.Incidencia;

import java.io.Serializable;
import java.util.List;

import timber.log.Timber;

import static android.R.id.list;
//import static com.didekindroid.incidencia.IncidObservable.incidImportanciaByUsers;
import static com.didekindroid.lib_one.util.CommonAssertionMsg.intent_extra_should_be_initialized;
import static com.didekindroid.lib_one.util.UiUtil.assertTrue;

/**
 * User: pedro@didekin
 * Date: 22/03/17
 * Time: 14:40
 */
public final class ViewerIncidSeeUserComuImportancia extends Viewer<ListView, CtrlerIncidenciaCore> implements
        ViewerListIf<ListView, CtrlerIncidenciaCore, ImportanciaUser> {

    private ViewerIncidSeeUserComuImportancia(View frView, ParentViewerIf parentViewer)
    {
        super(frView.findViewById(list), parentViewer.getActivity(), parentViewer);
    }

    public static ViewerIncidSeeUserComuImportancia newViewerIncidSeeUserComuImportancia(@NonNull View frView,
                                                                                         @NonNull ParentViewerIf parentViewer)
    {
        Timber.d("newViewerUserComuByComu()");
        ViewerIncidSeeUserComuImportancia instance = new ViewerIncidSeeUserComuImportancia(frView, parentViewer);
        instance.setController(new CtrlerIncidenciaCore());
        return instance;
    }

    // ==================================  VIEWER  =================================

    @Override
    public void doViewInViewer(Bundle savedState, Serializable incidencia)
    {
        Timber.d("doViewInViewer()");
        // Precondition.
        long incidenciaId = Incidencia.class.cast(incidencia).getIncidenciaId();
        assertTrue(incidenciaId > 0L, intent_extra_should_be_initialized);
        controller.loadItemsByEntitiyId(new ObserverSingleList<>(this), incidenciaId);
    }

    @Override
    public void onSuccessLoadItemList(List<ImportanciaUser> itemsList)
    {
        Timber.d("onSuccessLoadItemList()");
        // Only list NOT empty, because to register an incidencia implies to register an incidImportancia record,
        // even with a default value of 0.
        AdapterIncidImportanciaSee adapter = new AdapterIncidImportanciaSee(activity);
        adapter.addAll(itemsList);
        view.setAdapter(adapter);
    }

    /* =================================== HELPERS ==========================================*/
}
