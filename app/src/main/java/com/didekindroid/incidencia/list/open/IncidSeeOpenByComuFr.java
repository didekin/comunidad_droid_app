package com.didekindroid.incidencia.list.open;

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
import static com.didekindroid.incidencia.list.open.ViewerIncidSeeOpen.newViewerIncidSeeOpen;

/**
 * Preconditions:
 * A list of IncidenciaUser instances is retrieved with the incidencia and the registering user data.
 * <p/>
 * Postconditions:
 */
public class IncidSeeOpenByComuFr extends Fragment  {

    View rootFrgView;
    ViewerIncidSeeOpen viewerIncidSeeOpen;

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
        viewerIncidSeeOpen = newViewerIncidSeeOpen(rootFrgView, getActivity());
        viewerIncidSeeOpen.doViewInViewer(savedState, spinnerBean);

    }

    @Override
    public void onSaveInstanceState(Bundle savedState)
    {
        Timber.d("onSaveInstanceState()");
        viewerIncidSeeOpen.saveState(savedState);
        super.onSaveInstanceState(savedState);
    }

    @Override
    public void onStop()
    {
        Timber.d("onStop()");
        super.onStop();
        viewerIncidSeeOpen.clearSubscriptions();
    }
}
