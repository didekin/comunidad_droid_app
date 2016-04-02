package com.didekindroid.incidencia.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
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
public class IncidSeeByComuListFr extends ListFragment implements ComuSpinnerSettable {

    public static final String TAG = IncidSeeByComuListFr.class.getCanonicalName();

    ArrayAdapter<IncidenciaUser> mAdapter;
    IncidSeeListListener mListener;
    View mView;
    ListView mListView;
    Spinner mComunidadSpinner;
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
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        //TextView for no result.
        mListView.setEmptyView(mView.findViewById(android.R.id.empty));

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
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        Log.d(TAG, "onListItemClick()");

        mListView.setItemChecked(position, true);
        v.setSelected(true);
        if (mListener != null) {
            Incidencia incidencia = ((IncidenciaUser) mListView.getItemAtPosition(position)).getIncidencia();
            mListener.onIncidenciaSelected(incidencia, position);
        }
    }

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
        mComunidadSpinner.setSelection(mComunidadSelectedIndex);
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

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
                setListAdapter(mAdapter);
            }
            if (uiException != null) {
                Log.d(TAG, "onPostExecute(): uiException != null");
                checkState(incidencias == null);
                uiException.processMe(getActivity(), new Intent());
            }
        }
    }
}
