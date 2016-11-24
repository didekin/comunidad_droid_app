package com.didekindroid.incidencia.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.didekin.incidencia.dominio.Incidencia;
import com.didekin.incidencia.dominio.IncidenciaUser;
import com.didekin.comunidad.Comunidad;
import com.didekindroid.R;
import com.didekindroid.incidencia.activity.utils.ComuSpinnerSettable;
import com.didekindroid.incidencia.activity.utils.ComunidadSpinnerSetter;
import com.didekindroid.incidencia.exception.UiAppException;

import java.util.List;
import java.util.Objects;

import timber.log.Timber;

import static com.didekinaar.comunidad.ComuBundleKey.COMUNIDAD_LIST_INDEX;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCIDENCIA_LIST_INDEX;

/**
 * Preconditions:
 * A list of IncidenciaUser instances is retrieved with the incidencia and the registering user data.
 * <p/>
 * Postconditions:
 */
public class IncidSeeByComuListFr extends Fragment implements ComuSpinnerSettable {

    ArrayAdapter<IncidenciaUser> mAdapter;
    IncidSeeListListener mListener;
    View mView;
    ListView mListView;
    Spinner mComunidadSpinner;
    int mIncidenciaIndex;

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
        Timber.d("onAttach()");
        super.onAttach(context);
    }

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

        mView = inflater.inflate(R.layout.incid_see_generic_fr_layout, container, false);
        if (savedInstanceState != null) {
            mComunidadSelectedIndex = savedInstanceState.getInt(COMUNIDAD_LIST_INDEX.key, 0);
            mIncidenciaIndex = savedInstanceState.getInt(INCIDENCIA_LIST_INDEX.key, 0);
        }
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Timber.d("onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

        mListener = (IncidSeeListListener) getActivity();
        mAdapter = mListener.getAdapter(getActivity());

        mListView = (ListView) mView.findViewById(android.R.id.list);
        // To get visible a divider on top of the list.
        mListView.addHeaderView(new View(getContext()), null, true);
        mListView.setEmptyView(mView.findViewById(android.R.id.empty));
        mListView.setSelection(mIncidenciaIndex);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Timber.d("onListItemClick()");
                mListView.setItemChecked(position, true);
                view.setSelected(true);
                if (mListener != null) {
                    Incidencia incidencia = ((IncidenciaUser) mListView.getItemAtPosition(position)).getIncidencia();
                    mListener.onIncidenciaSelected(incidencia, position);
                }
                mIncidenciaIndex = position;
            }
        });

        mComunidadSpinner = (Spinner) mView.findViewById(R.id.incid_reg_comunidad_spinner);
        new ComunidadSpinnerSetter<>(this).execute();

        mComunidadSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Timber.d("mComunidadSpinner.onItemSelected()");
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
                Timber.d("mComunidadSpinner.onNothingSelected()");
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        Timber.d("onSaveInstanceState()");
        savedInstanceState.putInt(COMUNIDAD_LIST_INDEX.key, mComunidadSelectedIndex);
        savedInstanceState.putInt(INCIDENCIA_LIST_INDEX.key, mIncidenciaIndex);
        super.onSaveInstanceState(savedInstanceState);
    }

//    ........................ INTERFACE METHODS ..........................

    @Override
    public void setComunidadSpinnerAdapter(ArrayAdapter<Comunidad> comunidades)
    {
        Timber.d("setComunidadSpinnerAdapter()");
        mComunidadSpinner.setAdapter(comunidades);
    }

    @Override
    public void onComunidadSpinnerLoaded()
    {
        Timber.d("onComunidadSpinnerLoaded()");

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

        UiAppException uiException;

        @Override
        protected List<IncidenciaUser> doInBackground(Long... comunidadId)
        {
            Timber.d("doInBackground()");
            List<IncidenciaUser> incidenciaList = null;
            try {
                incidenciaList = mListener.getListFromService(comunidadId[0]);
            } catch (UiAppException e) {
                uiException = e;
            }
            return incidenciaList;
        }

        @Override
        protected void onPostExecute(List<IncidenciaUser> incidencias)
        {
            Timber.d("onPostExecute()");
            if (incidencias != null && incidencias.size() > 0) {
                Timber.d("onPostExecute(): incidUserComuList != null");
                mAdapter.clear();
                mAdapter.addAll(incidencias);
                mListView.setAdapter(mAdapter);
            }
            if (uiException != null) {
                Timber.d("onPostExecute(): uiException != null");
                Objects.equals(incidencias == null, true);
                uiException.processMe(getActivity(), new Intent());
            }
        }
    }
}
