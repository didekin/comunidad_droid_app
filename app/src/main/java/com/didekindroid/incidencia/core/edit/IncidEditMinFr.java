package com.didekindroid.incidencia.core.edit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.didekindroid.R;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.api.ViewerParentInjectorIf;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;

import timber.log.Timber;

import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidenciaAssertionMsg.incid_importancia_should_be_initialized;
import static com.didekindroid.util.UIutils.assertTrue;

/**
 * User: pedro@didekin
 * Date: 22/01/16
 * Time: 16:16
 */

@SuppressWarnings("ConstantConditions")
public class IncidEditMinFr extends Fragment {

    View frView;
    ViewerParentInjectorIf viewerInjector;
    /**
     * Instantiated by the activity (ViewerIncidEditAc).
     */
    ViewerIncidEditMinFr viewer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Timber.d("onCreateView()");
        frView = inflater.inflate(R.layout.incid_edit_nopower_fr, container, false);
        return frView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        Timber.d("onViewCreated()");

        super.onViewCreated(view, savedInstanceState);
        // TODO: ¿añadir? IncidAndResolBundle resolBundle = (IncidAndResolBundle) getArguments().getSerializable(INCID_RESOLUCION_BUNDLE.key); TEST.
        IncidImportancia incidImportancia = (IncidImportancia) getArguments().getSerializable(INCID_IMPORTANCIA_OBJECT.key);
        // Preconditions.
        assertTrue(incidImportancia.getUserComu() != null
                && incidImportancia.getIncidencia().getIncidenciaId() > 0, incid_importancia_should_be_initialized);

        viewerInjector = (ViewerParentInjectorIf) getActivity();
        ViewerIf parentViewer = viewerInjector.getViewerAsParent();
        viewer = ViewerIncidEditMinFr.newViewerIncidEditMinFr(frView, parentViewer);
        viewer.doViewInViewer(savedInstanceState, incidImportancia);
        viewerInjector.setChildInViewer(viewer);
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        Timber.d("onSaveInstanceState()");
        super.onSaveInstanceState(outState);
        viewer.saveState(outState);
    }

    @Override
    public void onStop()
    {
        Timber.d("onStop()");
        viewer.clearSubscriptions();
        super.onStop();
    }
}
