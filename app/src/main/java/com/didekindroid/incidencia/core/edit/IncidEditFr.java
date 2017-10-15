package com.didekindroid.incidencia.core.edit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.didekindroid.api.ChildViewersInjectorIf;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;

import timber.log.Timber;

import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_BUNDLE;
import static com.didekindroid.incidencia.utils.IncidenciaAssertionMsg.incid_importancia_should_be_initialized;
import static com.didekindroid.util.UIutils.assertTrue;

/**
 * User: pedro@didekin
 * Date: 22/01/16
 * Time: 16:16
 */
@SuppressWarnings("AbstractClassExtendsConcreteClass")
public abstract class IncidEditFr extends Fragment {

    View frView;
    ChildViewersInjectorIf viewerInjector;
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
    }

    @Override
    public void onStop()
    {
        Timber.d("onStop()");
        getViewerIncidEdit().clearSubscriptions();
        super.onStop();
    }
}
