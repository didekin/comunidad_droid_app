package com.didekindroid.incidencia.list.open;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.didekindroid.R;
import com.didekindroid.router.ActivityInitiatorIf;
import com.didekinlib.model.comunidad.Comunidad;

import timber.log.Timber;

import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.list.open.ViewerIncidSeeOpen.newViewerIncidSeeOpen;
import static com.didekindroid.router.ActivityRouter.RouterToActivity.writeNewIncidencia;

/**
 * Preconditions:
 * A list of IncidenciaUser instances is retrieved with the incidencia and the registering user data.
 * <p/>
 * Postconditions:
 */
public class IncidSeeOpenByComuFr extends Fragment implements ActivityInitiatorIf {

    View frView;
    ViewerIncidSeeOpen viewer;

    static IncidSeeOpenByComuFr newInstance(long comunidadId)
    {
        Timber.d("newInstance()");
        IncidSeeOpenByComuFr incidSeeOpenByComuFr = new IncidSeeOpenByComuFr();
        Bundle args = new Bundle();
        args.putSerializable(COMUNIDAD_ID.key, comunidadId);
        incidSeeOpenByComuFr.setArguments(args);
        return incidSeeOpenByComuFr;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState)
    {
        Timber.d("onCreateView()");
        frView = inflater.inflate(R.layout.incid_see_generic_fr_layout, container, false);
        FloatingActionButton fab = frView.findViewById(R.id.incid_new_incid_fab);
        fab.setOnClickListener(v -> initAcFromListener(getArguments(), writeNewIncidencia));
        return frView;
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedState)
    {
        Timber.d("onViewCreated()");
        super.onViewCreated(view, savedState);
        viewer = newViewerIncidSeeOpen(frView, (AppCompatActivity) getActivity());
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
        viewer.clearSubscriptions();
        super.onStop();
    }
}
