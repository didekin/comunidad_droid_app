package com.didekindroid.incidencia.list.close;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.didekindroid.R;
import com.didekindroid.incidencia.core.IncidenciaBean;
import com.didekindroid.usuariocomunidad.spinner.ComuSpinnerBean;

import timber.log.Timber;

import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.list.close.ViewerIncidSeeClose.newViewerIncidSeeClose;

/**
 * Preconditions:
 * A list of IncidenciaUser instances, whose incidencias are closed, are shown.
 */
public class IncidSeeCloseByComuFr extends Fragment  {

    View rootFrgView;
    ViewerIncidSeeClose viewerIncidClose;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState)
    {
        Timber.d("onCreateView()");
        rootFrgView = inflater.inflate(R.layout.incid_see_generic_fr_layout, container, false);
        return rootFrgView;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedState)
    {
        Timber.d("onViewCreated()");
        super.onViewCreated(view, savedState);
        /* Initialization of viewers.*/
        ComuSpinnerBean spinnerBean = new IncidenciaBean();
        spinnerBean.setComunidadId(getArguments().getLong(COMUNIDAD_ID.key));
        viewerIncidClose = newViewerIncidSeeClose(rootFrgView, getActivity());
        viewerIncidClose.doViewInViewer(savedState, spinnerBean);
    }

    @Override
    public void onSaveInstanceState(Bundle savedState)
    {
        Timber.d("onSaveInstanceState()");
        viewerIncidClose.saveState(savedState);
        super.onSaveInstanceState(savedState);
    }

    @Override
    public void onStop()
    {
        Timber.d("onStop()");
        super.onStop();
        viewerIncidClose.clearSubscriptions();
    }
}