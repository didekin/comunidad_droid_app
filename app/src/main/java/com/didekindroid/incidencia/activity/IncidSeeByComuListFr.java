package com.didekindroid.incidencia.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.didekin.incidservice.dominio.Incidencia;
import com.didekin.incidservice.dominio.IncidenciaUser;
import com.didekin.usuario.dominio.Comunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.incidencia.activity.utils.ComuSpinnerSettable;
import com.didekindroid.incidencia.activity.utils.ComunidadSpinnerSetter;

import java.util.List;

import static com.didekindroid.common.activity.BundleKey.COMUNIDAD_LIST_INDEX;
import static com.google.common.base.Preconditions.checkState;

/**
 * Preconditions:
 * A list of IncidenciaUser instances is retrieved with the incidencia and the registering user data.
 * <p/>
 * Postconditions:
 */
public class IncidSeeByComuListFr extends Fragment implements ComuSpinnerSettable {

    public static final String TAG = IncidSeeByComuListFr.class.getCanonicalName();

    ArrayAdapter<IncidenciaUser> mAdapter;
    IncidSeeListListener mListener;
    View mView;
    ListView mListView;
    Spinner mComunidadSpinner;

    /**
     * This index can be set in three ways:
     * 1. The user selects one item in the spinner.
     * 2. The index is retrieved from savedInstanceState.
     * 3. The index is passed from the activity (in FCM notifications).
     */
    int mComunidadSelectedIndex;

    @Override
    public void onAttach(Context context)
    {
        Log.d(TAG, "onAttach()");
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView()");

        mView = inflater.inflate(R.layout.incid_see_generic_fr_layout, container, false);
        if (savedInstanceState != null) {
            mComunidadSelectedIndex = savedInstanceState.getInt(COMUNIDAD_LIST_INDEX.key, 0);
        }
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Log.d(TAG, "onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

        mListener = (IncidSeeListListener) getActivity();
        mAdapter = mListener.getAdapter(getActivity());

        mListView = (ListView) mView.findViewById(android.R.id.list);
        // To get visible a divider on top of the list.
        mListView.addHeaderView(new View(getContext()), null, true);
        mListView.setEmptyView(mView.findViewById(android.R.id.empty));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Log.d(TAG, "onListItemClick()");
                mListView.setItemChecked(position, true);
                view.setSelected(true);
                if (mListener != null) {
                    Incidencia incidencia = ((IncidenciaUser) mListView.getItemAtPosition(position)).getIncidencia();
                    mListener.onIncidenciaSelected(incidencia, position);
                }
            }
        });

        mComunidadSpinner = (Spinner) mView.findViewById(R.id.incid_reg_comunidad_spinner);
        new ComunidadSpinnerSetter<>(this).execute();

        mComunidadSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Log.d(TAG, "mComunidadSpinner.onItemSelected()");
                mComunidadSelectedIndex = position;
                Comunidad comunidad = (Comunidad) parent.getItemAtPosition(position);
                // Loading data for the comunidad selected.
                new IncidByComuLoader().execute(comunidad.getC_Id());
                // Informamos a la actividad.
                mListener.onComunidadSpinnerSelected(comunidad);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Log.d(TAG, "mComunidadSpinner.onNothingSelected()");
            }
        });
    }

    @Override
    public void onStart()
    {
        Log.d(TAG, "Enters onStart()");
        super.onStart();
    }

    @Override
    public void onResume()
    {
        Log.d(TAG, "Enters onResume()");
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        Log.d(TAG, "onSaveInstanceState()");
        savedInstanceState.putInt(COMUNIDAD_LIST_INDEX.key, mComunidadSelectedIndex);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onPause()
    {
        Log.d(TAG, "onPause()");
        super.onPause();
    }

    @Override
    public void onStop()
    {
        Log.d(TAG, "onStop()");
        super.onStop();
    }

    @Override
    public void onDestroyView()
    {
        Log.d(TAG, "onDestroyView()");
        super.onDestroyView();
    }

    @Override
    public void onDestroy()
    {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }

    @Override
    public void onDetach()
    {
        Log.d(TAG, "onDetach()");
        super.onDetach();
    }

//    ........................ INTERFACE METHODS ..........................

    @Override
    public void setComunidadSpinnerAdapter(ArrayAdapter<Comunidad> comunidades)
    {
        Log.d(TAG, "setComunidadSpinnerAdapter()");
        mComunidadSpinner.setAdapter(comunidades);
    }

    @Override
    public void onComunidadSpinnerLoaded()
    {
        Log.d(TAG, "onComunidadSpinnerLoaded()");

        // We check if there is a comunidadId passed from the activity.
        long comunidadIntent = mListener.getComunidadSelected();
        if (comunidadIntent > 0) {
            int position = 0;
            do {
                if (((Comunidad) mComunidadSpinner.getItemAtPosition(position)).getC_Id() == comunidadIntent) {
                    mComunidadSelectedIndex = position;
                    break;
                }
            } while (++position < mComunidadSpinner.getCount());
        }
        mComunidadSpinner.setSelection(mComunidadSelectedIndex);
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    public ListView getListView()
    {
        return mListView;
    }

    class IncidByComuLoader extends AsyncTask<Long, Void, List<IncidenciaUser>> {

        private final String TAG = IncidByComuLoader.class.getCanonicalName();
        UiException uiException;

        @Override
        protected List<IncidenciaUser> doInBackground(Long... comunidadId)
        {
            Log.d(TAG, "doInBackground()");
            List<IncidenciaUser> incidenciaList = null;
            try {
                incidenciaList = mListener.getListFromService(comunidadId[0]);
            } catch (UiException e) {
                uiException = e;
            }
            return incidenciaList;
        }

        @Override
        protected void onPostExecute(List<IncidenciaUser> incidencias)
        {
            Log.d(TAG, "onPostExecute()");
            if (incidencias != null && incidencias.size() > 0) {
                Log.d(TAG, "onPostExecute(): incidUserComuList != null");
                mAdapter.clear();
                mAdapter.addAll(incidencias);
                mListView.setAdapter(mAdapter);
            }
            if (uiException != null) {
                Log.d(TAG, "onPostExecute(): uiException != null");
                checkState(incidencias == null);
                uiException.processMe(getActivity(), new Intent());
            }
        }
    }
}
