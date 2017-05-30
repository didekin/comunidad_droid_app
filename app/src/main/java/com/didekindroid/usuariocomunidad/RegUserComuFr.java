package com.didekindroid.usuariocomunidad;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.didekindroid.R;
import com.didekindroid.api.ViewerParentInjectorIf;

import timber.log.Timber;

import static com.didekindroid.usuariocomunidad.ViewerRegUserComuFr.newViewerRegUserComuFr;

public class RegUserComuFr extends Fragment {

    ViewerParentInjectorIf viewerInjector;
    ViewerRegUserComuFr viewer;
    private View regUserComuFrView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Timber.d("onCreateView()");
        regUserComuFrView = inflater.inflate(R.layout.reg_usercomu_fr, container, false);
        return regUserComuFrView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        Timber.d("onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

        viewerInjector = (ViewerParentInjectorIf) getActivity();
        viewer = newViewerRegUserComuFr(regUserComuFrView, viewerInjector.getViewerAsParent());
        viewer.doViewInViewer(savedInstanceState, null);
        viewerInjector.setChildInViewer(viewer);
    }

    @Override
    public void onStop()
    {
        Timber.d("onStop()");
        super.onStop();
        viewer.clearSubscriptions();
    }
}


