package com.didekindroid.incidencia.list.close;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.didekindroid.R;
import com.didekindroid.usuariocomunidad.spinner.ComuSpinnerEventItemSelect;
import com.didekinlib.model.comunidad.Comunidad;

import timber.log.Timber;

import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.list.close.ViewerIncidSeeClose.newViewerIncidSeeClose;

/**
 * Preconditions:
 * A list of IncidenciaUser instances, whose incidencias are closed, are shown.
 */
public class IncidSeeCloseByComuFr extends Fragment {

    View frView;
    ViewerIncidSeeClose viewer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState)
    {
        Timber.d("onCreateView()");
        frView = inflater.inflate(R.layout.incid_see_generic_fr_layout, container, false);
        return frView;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedState)
    {
        Timber.d("onViewCreated()");
        super.onViewCreated(view, savedState);
        /* Initialization of viewers.*/
        ComuSpinnerEventItemSelect spinnerEventItemSelect =
                new ComuSpinnerEventItemSelect(new Comunidad.ComunidadBuilder().c_id(getArguments().getLong(COMUNIDAD_ID.key)).build());
        viewer = newViewerIncidSeeClose(frView, getActivity());
        viewer.doViewInViewer(savedState, spinnerEventItemSelect);
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
        Timber.d("onStop()");
        super.onStop();
        viewer.clearSubscriptions();
    }
}
