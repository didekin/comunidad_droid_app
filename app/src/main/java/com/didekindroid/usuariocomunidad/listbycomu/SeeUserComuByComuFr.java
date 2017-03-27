package com.didekindroid.usuariocomunidad.listbycomu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.didekindroid.R;

import timber.log.Timber;


/**
 * User: pedro@didekin
 * Date: 25/08/15
 * Time: 16:38
 *
 * <p>
 * Preconditions:
 * 1. the user is registered.
 * 2. a long comunidadId is passed as an intent key.
 */
public class SeeUserComuByComuFr extends Fragment {

    ViewerSeeUserComuByComu viewer;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Timber.d("onCreateView()");
        View mainView = inflater.inflate(R.layout.see_usercomu_by_comu_list_fr, container, false);
        viewer = ViewerSeeUserComuByComu.newViewerUserComuByComu(mainView, getActivity());
        viewer.doViewInViewer(savedInstanceState);
        return mainView;
    }

    @Override
    public void onStop()
    {
        Timber.d("onStop()");
        super.onStop();
        viewer.clearSubscriptions();
    }
}
