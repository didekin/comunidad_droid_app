package com.didekindroid.incidencia.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.didekin.incidservice.dominio.Resolucion;
import com.didekindroid.R;

import static com.didekindroid.common.utils.UIutils.formatTimeStampToString;
import static com.didekindroid.common.utils.UIutils.getStringFromInteger;
import static com.google.common.base.Preconditions.checkState;

/**
 * User: pedro@didekin
 * Date: 13/11/15
 * Time: 15:52
 */
public class IncidResolucionSeeFr extends Fragment {

    private static final String TAG = IncidResolucionSeeFr.class.getCanonicalName();

    View mFragmentView;
    IncidenciaDataSupplier mActivitySupplier;
    Resolucion mResolucion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView()");
        mFragmentView = inflater.inflate(R.layout.incid_resolucion_see_fr, container, false);
        return mFragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Log.d(TAG, "onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
        mActivitySupplier = (IncidenciaDataSupplier) getActivity();

        mResolucion = mActivitySupplier.getResolucion();
        checkState(mResolucion != null);

        IncidAvanceSeeAdapter mAdapter = new IncidAvanceSeeAdapter(getActivity());
        mAdapter.clear();
        mAdapter.addAll(mResolucion.getAvances());

        // Fecha estimada.
        ((TextView) mFragmentView.findViewById(R.id.incid_resolucion_fecha_view)).setText(formatTimeStampToString(mResolucion.getFechaPrev()));
        // Coste estimado.
        ((TextView) mFragmentView.findViewById(R.id.incid_resolucion_coste_prev_view)).setText(getStringFromInteger(mResolucion.getCosteEstimado()));
        // Plan.
        ((TextView) mFragmentView.findViewById(R.id.incid_resolucion_txt)).setText(mResolucion.getDescripcion());
        // Lista de avances.
        ListView mListView = (ListView) mFragmentView.findViewById(android.R.id.list);
        mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
        mListView.setEmptyView(mFragmentView.findViewById(android.R.id.empty));
        mListView.setAdapter(mAdapter);
    }
}
