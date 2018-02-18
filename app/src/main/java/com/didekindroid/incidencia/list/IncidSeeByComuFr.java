package com.didekindroid.incidencia.list;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.didekindroid.R;
import com.didekinlib.model.comunidad.Comunidad;

import timber.log.Timber;

import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_CLOSED_LIST_FLAG;
import static com.didekindroid.incidencia.IncidContextualName.to_register_new_incidencia;
import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;

/**
 * Preconditions:
 * A list of IncidenciaUser instances, whose incidencias are closed, are shown.
 */
public class IncidSeeByComuFr extends Fragment {

    View frView;
    ViewerIncidSeeCloseFr viewer;

    static IncidSeeByComuFr newInstance(long comunidadId, boolean booleanExtra)
    {
        Timber.d("newInstance()");
        IncidSeeByComuFr fr = new IncidSeeByComuFr();
        Bundle args = new Bundle(1);
        args.putLong(COMUNIDAD_ID.key, comunidadId);
        args.putBoolean(INCID_CLOSED_LIST_FLAG.key, booleanExtra);
        fr.setArguments(args);
        return fr;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState)
    {
        Timber.d("onCreateView()");
        frView = inflater.inflate(R.layout.incid_see_generic_fr_layout, container, false);
        FloatingActionButton fab = frView.findViewById(R.id.incid_new_incid_fab);
        fab.setOnClickListener(
                v -> routerInitializer.get().getContextRouter().getActionFromContextNm(to_register_new_incidencia)
                        .initActivity(
                                getActivity(),
                                COMUNIDAD_ID.getBundleForKey(viewer.getComuSpinner().getSelectedItemId())
                        )
        );
        return frView;
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedState)
    {
        Timber.d("onViewCreated()");
        super.onViewCreated(view, savedState);
        viewer = ViewerIncidSeeCloseFr.newViewerIncidSeeClose(frView, (AppCompatActivity) getActivity(), getArguments().getBoolean(INCID_CLOSED_LIST_FLAG.key));
        final long comunidadId = getArguments().getLong(COMUNIDAD_ID.key);
        viewer.doViewInViewer(savedState, comunidadId > 0 ? new Comunidad.ComunidadBuilder().c_id(comunidadId).build() : null);
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
