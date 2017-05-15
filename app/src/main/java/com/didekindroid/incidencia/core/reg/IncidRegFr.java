package com.didekindroid.incidencia.core.reg;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.didekindroid.R;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.api.ViewerParentInjectorIf;

import timber.log.Timber;

import static com.didekindroid.incidencia.core.reg.ViewerIncidRegFr.newViewerIncidRegFr;

/**
 *
 */
public class IncidRegFr extends Fragment {

    View rootFrgView;
    ViewerParentInjectorIf viewerInjector;
    /**
     * Instantiated by the activity (ViewerIncidRegAc).
     */
    ViewerIncidRegFr viewer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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

        viewerInjector = (ViewerParentInjectorIf) getActivity();
        ViewerIf parentViewer = viewerInjector.getViewerAsParent();
        viewer = newViewerIncidRegFr(rootFrgView, parentViewer);
        viewer.doViewInViewer(savedInstanceState, null);
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
