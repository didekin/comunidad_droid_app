package com.didekindroid.incidencia.core.reg;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.didekindroid.R;

import timber.log.Timber;

/**
 *
 */
public class IncidRegAcFragment extends Fragment {

    View rootFrgView;

    /**
     *  Instantiated by the activity (ViewerIncidRegAc).
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedState)
    {
        Timber.d("onViewCreated()");
        super.onViewCreated(view, savedState);
    }

    @Override
    public void onSaveInstanceState(Bundle savedState)
    {
        Timber.d("onSaveInstanceState()");
        viewer.saveState(savedState);
        super.onSaveInstanceState(savedState);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        viewer.clearSubscriptions();
    }
}
