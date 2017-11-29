package com.didekindroid.comunidad;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.didekindroid.R;
import com.didekinlib.model.comunidad.Comunidad;

import timber.log.Timber;

import static com.didekindroid.comunidad.ViewerComuSearchResultsFr.newViewerComuSearchResultsFr;
import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_SEARCH;
import static com.didekindroid.util.CommonAssertionMsg.bean_fromView_should_be_initialized;
import static com.didekindroid.util.UIutils.assertTrue;

/**
 * Preconditions:
 * 1. An intent is passed with the comunidad to search for.
 * Postconditions for comunidad selection:
 * LIST OF COMUNIDADES FOUND
 * 1. Every comunidad in the list supplied to the adapter has the following fields:
 * -- comunidadId.
 * -- nombreComunidad (composed internally with tipoVia,nombreVia, numero and sufijoNumero).
 * -- municipio, with codInProvincia and nombre.
 * -- provincia, with provinciaId and nombre.
 * <p>
 * COMUNIDAD SELECTED
 * 2. An object comunidad is passed to the activity with the fields:
 * -- comunidadId.
 * -- nombreComunidad (with tipoVia,nombreVia, numero and sufijoNumero).
 * -- municipio, with codInProvincia and nombre.
 * -- provincia, with provinciaId and nombre.
 */
public class ComuSearchResultsListFr extends Fragment {

    View frView;
    ViewerComuSearchResultsFr viewer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Timber.d("onCreateView()");
        frView = inflater.inflate(R.layout.comu_search_results_fr_layout, container, false);
        return frView;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Timber.d("onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

        Comunidad comunidadToSearch = (Comunidad) getActivity().getIntent().getSerializableExtra(COMUNIDAD_SEARCH.key);
        assertTrue(comunidadToSearch != null, bean_fromView_should_be_initialized);

        viewer = newViewerComuSearchResultsFr(frView, (AppCompatActivity) getActivity());
        viewer.doViewInViewer(savedInstanceState, comunidadToSearch);
    }

    @Override
    public void onStop()
    {
        Timber.d("onStop()");
        super.onStop();
        viewer.clearSubscriptions();
    }
}
