package com.didekindroid.usuario;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.didekindroid.R;
import com.didekindroid.api.ViewerParentInjectorIf;

import timber.log.Timber;

public class RegUserFr extends Fragment {

    ViewerParentInjectorIf viewerInjector;
    ViewerRegUserFr viewer;
    private View regUserFrView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Timber.d("onCreateView()");
        regUserFrView = inflater.inflate(R.layout.reg_user_fr, container, false);
        return regUserFrView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        Timber.d("onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

        viewerInjector = (ViewerParentInjectorIf) getActivity();
        viewer = new ViewerRegUserFr(regUserFrView, getActivity(), viewerInjector.getViewerAsParent());
        viewer.doViewInViewer(savedInstanceState, null);
        viewerInjector.setChildInViewer(viewer);
    }
}
