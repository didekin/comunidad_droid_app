package com.didekindroid.comunidad;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.didekindroid.R;
import com.didekindroid.api.ViewerParentInjectorIf;
import com.didekinlib.model.comunidad.Comunidad;

import timber.log.Timber;

import static com.didekindroid.comunidad.ViewerRegComuFr.newViewerRegComuFr;
import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_ID;

/**
 * Preconditions:
 * 1. The user is registered.
 * 2. If the fragment is used for edition, an intent is passed with a comunidad ID.
 * Postconditions:
 * 1. If a intent is passed, the data of the comunidad are shown in the fragment.
 * <p>
 */
public class RegComuFr extends Fragment {

    View frView;
    ViewerRegComuFr viewer;
    ViewerParentInjectorIf viewerInjector;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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

        // Check for intent with comunidad ID.
        long comunidadId = getActivity().getIntent().getLongExtra(COMUNIDAD_ID.key, 0L);

        viewerInjector = (ViewerParentInjectorIf) getActivity();
        viewer = newViewerRegComuFr(frView, viewerInjector.getViewerAsParent());
        viewer.doViewInViewer(savedInstanceState, comunidadId > 0 ? new Comunidad.ComunidadBuilder().c_id(comunidadId).build() : null);
        viewerInjector.setChildInViewer(viewer);
    }

    @Override
    public void onSaveInstanceState(Bundle savedState)
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
