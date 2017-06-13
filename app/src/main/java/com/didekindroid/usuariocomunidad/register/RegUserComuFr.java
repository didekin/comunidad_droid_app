package com.didekindroid.usuariocomunidad.register;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.didekindroid.R;
import com.didekindroid.api.ViewerParentInjectorIf;

import timber.log.Timber;

import static com.didekindroid.usuariocomunidad.util.UserComuBundleKey.USERCOMU_LIST_OBJECT;

public class RegUserComuFr extends Fragment {

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

        ViewerParentInjectorIf viewerInjector = (ViewerParentInjectorIf) getActivity();
        viewer = ViewerRegUserComuFr.newViewerRegUserComuFr(regUserComuFrView, viewerInjector.getViewerAsParent());
        viewer.doViewInViewer(savedInstanceState, getActivity().getIntent().getSerializableExtra(USERCOMU_LIST_OBJECT.key));
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


