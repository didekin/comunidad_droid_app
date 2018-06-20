package com.didekindroid.incidencia.core.reg;

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

import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.core.reg.ViewerIncidRegFr.newViewerIncidRegFr;
import static java.util.Objects.requireNonNull;

/**
 * Preconditions:
 * 1. The fragment has an argument for the comunidadId where the incidencia will be open.
 */
public class IncidRegFr extends Fragment {

    View rootFrgView;
    InjectorOfParentViewerIf viewerInjector;
    ViewerIncidRegFr viewer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedState)
    {
        Timber.d("onCreateView()");
        rootFrgView = inflater.inflate(R.layout.incid_reg_frg, container, false);
        return rootFrgView;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        Timber.d("onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

        /* Initialization of viewers.*/
        viewerInjector = (InjectorOfParentViewerIf) getActivity();
        viewer = newViewerIncidRegFr(rootFrgView, requireNonNull(viewerInjector).getInjectedParentViewer());
        long comunidadId = requireNonNull(getArguments()).getLong(COMUNIDAD_ID.key);
        viewer.doViewInViewer(savedInstanceState, comunidadId > 0 ? new Comunidad.ComunidadBuilder().c_id(comunidadId).build() : null);
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
