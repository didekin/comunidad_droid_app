package com.didekindroid.comunidad;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.InjectorOfParentViewerIf;
import com.didekinlib.model.comunidad.Comunidad;

import timber.log.Timber;

import static com.didekindroid.comunidad.ViewerRegComuFr.newViewerRegComuFr;
import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_SEARCH;
import static com.didekindroid.lib_one.util.CommonAssertionMsg.intent_extra_should_be_initialized;
import static com.didekindroid.lib_one.util.UiUtil.assertTrue;
import static java.util.Objects.requireNonNull;

/**
 * Preconditions:
 * 1. The user is registered.
 * 2. If the fragment is used for edition, an intent is passed with a comunidad ID.
 * 3. If the fragment is used after an unsuccesful search, an intent is passed with the comunidad to search:
 * -- tipoVia.
 * -- nombreVia.
 * -- numero.
 * -- sufijoNumero (it can be an empty string).
 * -- municipio with codInProvincia and provinciaId.
 * Postconditions:
 * 1. If a intent is passed, the data of the comunidad are shown in the fragment.
 * <p>
 */
public class RegComuFr extends Fragment {

    View frView;
    ViewerRegComuFr viewer;
    InjectorOfParentViewerIf viewerInjector;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Timber.d("onCreateView()");
        frView = inflater.inflate(R.layout.reg_comu_fr, container, false);
        return frView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        Timber.d("onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

        Comunidad comunidad;

        long comunidadId = requireNonNull(getActivity()).getIntent().getLongExtra(COMUNIDAD_ID.key, 0L);
        if (comunidadId > 0) {
            comunidad = new Comunidad.ComunidadBuilder().c_id(comunidadId).build();
        } else if (getActivity().getIntent().hasExtra(COMUNIDAD_SEARCH.key)) {
            comunidad = (Comunidad) getActivity().getIntent().getSerializableExtra(COMUNIDAD_SEARCH.key);
            assertTrue(comunidad.getMunicipio().getProvincia().getComunidadAutonoma() != null, intent_extra_should_be_initialized);
        } else {
            comunidad = null;
        }

        viewerInjector = (InjectorOfParentViewerIf) getActivity();
        viewer = newViewerRegComuFr(frView, viewerInjector.getInjectedParentViewer());
        viewer.doViewInViewer(savedInstanceState, comunidad);
        viewerInjector.setChildInParentViewer(viewer);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedState)
    {
        Timber.d("onSaveInstanceState()");
        super.onSaveInstanceState(savedState);
        viewer.saveState(savedState);
    }

    @Override
    public void onStop()
    {
        Timber.d("onStop()");
        super.onStop();
        viewer.clearSubscriptions();
    }
}
