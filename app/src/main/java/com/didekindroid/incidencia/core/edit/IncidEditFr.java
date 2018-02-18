package com.didekindroid.incidencia.core.edit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.didekindroid.lib_one.api.InjectorOfParentViewerIf;
import com.didekindroid.incidencia.core.edit.importancia.ViewerIncidSeeUserComuImportancia;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;

import timber.log.Timber;

import static com.didekindroid.incidencia.core.edit.importancia.ViewerIncidSeeUserComuImportancia.newViewerIncidSeeUserComuImportancia;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_RESOLUCION_BUNDLE;
import static com.didekindroid.incidencia.IncidenciaAssertionMsg.incid_importancia_should_be_initialized;
import static com.didekindroid.lib_one.util.UiUtil.assertTrue;

/**
 * User: pedro@didekin
 * Date: 22/01/16
 * Time: 16:16
 */
@SuppressWarnings("AbstractClassExtendsConcreteClass")
public abstract class IncidEditFr extends Fragment {

    View frView;
    InjectorOfParentViewerIf viewerInjector;
    ViewerIncidSeeUserComuImportancia viewerIncidImportancia;
    IncidAndResolBundle resolBundle;

    protected abstract ViewerIncidEditFr getViewerIncidEdit();

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        Timber.d("onViewCreated()");
        super.onViewCreated(view, savedInstanceState);

        // Preconditions.
        resolBundle = (IncidAndResolBundle) getArguments().getSerializable(INCID_RESOLUCION_BUNDLE.key);
        IncidImportancia incidImportancia = resolBundle.getIncidImportancia();
        assertTrue(incidImportancia.getUserComu() != null
                && incidImportancia.getIncidencia().getIncidenciaId() > 0, incid_importancia_should_be_initialized);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        Timber.d("onSaveInstanceState()");
        super.onSaveInstanceState(outState);
        getViewerIncidEdit().saveState(outState);
        viewerIncidImportancia.saveState(outState);
    }

    @Override
    public void onStop()
    {
        Timber.d("onStop()");
        getViewerIncidEdit().clearSubscriptions();
        viewerIncidImportancia.clearSubscriptions();
        super.onStop();
    }

     /* =======================================  HELPERS  =======================================*/

    void initViewerImportancia(@Nullable Bundle savedInstanceState)
    {
        viewerIncidImportancia = newViewerIncidSeeUserComuImportancia(frView, viewerInjector.getInjectedParentViewer());
        viewerIncidImportancia.doViewInViewer(savedInstanceState, resolBundle.getIncidImportancia().getIncidencia());
        viewerInjector.setChildInParentViewer(viewerIncidImportancia);
    }
}
