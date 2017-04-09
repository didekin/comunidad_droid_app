package com.didekindroid.incidencia.core.edit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.didekindroid.R;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.api.ViewerParentInjectorIf;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;

import timber.log.Timber;

import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_FLAG;
import static com.didekindroid.incidencia.utils.IncidenciaAssertionMsg.incid_importancia_should_be_initialized;
import static com.didekindroid.util.UIutils.assertTrue;

/**
 * User: pedro@didekin
 * Date: 22/01/16
 * Time: 16:16
 */
public class IncidEditMaxPowerFr extends Fragment {

    Button mButtonModify;
    Button mButtonErase;

    View frView;
    ViewerParentInjectorIf viewerInjector;
    /**
     * Instantiated by the activity (ViewerIncidRegAc).
     */
    ViewerIncidEditMaxFr viewer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Timber.d("onCreateView()");
        frView = inflater.inflate(R.layout.incid_edit_maxpower_fr, container, false);
        return frView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        Timber.d("onViewCreated()");
        super.onViewCreated(view, savedInstanceState);

        IncidImportancia incidImportancia = (IncidImportancia) getArguments().getSerializable(INCID_IMPORTANCIA_OBJECT.key);
        // Preconditions.
        assertTrue(incidImportancia != null
                && incidImportancia.getUserComu() != null
                && incidImportancia.getIncidencia() != null
                && incidImportancia.getIncidencia().getIncidenciaId() > 0, incid_importancia_should_be_initialized);

        boolean flagResolucion = getArguments().getBoolean(INCID_RESOLUCION_FLAG.key);
        boolean flagHasAdmAuthority = incidImportancia.getUserComu().hasAdministradorAuthority();

        viewerInjector = (ViewerParentInjectorIf) getActivity();
        ViewerIf parentViewer = viewerInjector.getViewerAsParent();
        viewer = ViewerIncidEditMaxFr.newViewerIncidEditMaxFr(frView, parentViewer, flagResolucion);
        viewer.doViewInViewer(savedInstanceState, incidImportancia);
    }
}
