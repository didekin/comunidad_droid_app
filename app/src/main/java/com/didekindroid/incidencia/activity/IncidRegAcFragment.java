package com.didekindroid.incidencia.activity;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.didekin.serviceone.domain.Comunidad;
import com.didekindroid.R;
import com.didekindroid.common.UiException;
import com.didekindroid.incidencia.dominio.IncidenciaBean;
import com.didekindroid.incidencia.repository.IncidenciaDataDbHelper;
import com.didekindroid.incidencia.webservices.IncidService;

import java.util.List;

import static com.didekindroid.incidencia.repository.IncidenciaDataDb.AmbitoIncidencia.ambito;
import static com.google.common.base.Preconditions.checkState;

/**
 *
 */
public class IncidRegAcFragment extends Fragment {

    private static final String TAG = IncidRegAcFragment.class.getCanonicalName();

    IncidenciaBean mIncidenciaBean;
    Spinner mComunidadSpinner;
    Spinner mImportanciaSpinner;
    Spinner mTipoIncidenciaSpinner;
    IncidenciaDataDbHelper dbHelper;
    View mFragmentView;

    public IncidRegAcFragment()
    {
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        Log.d(TAG, "onAttach()");
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView()");
        mFragmentView = inflater.inflate(R.layout.incid_reg_frg, container, false);
        return mFragmentView;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated()");

        dbHelper = new IncidenciaDataDbHelper(getActivity());

        mComunidadSpinner = (Spinner) getView().findViewById(R.id.incid_reg_comunidad_spinner);
        new ComunidadSpinnerSetter().execute();

        mImportanciaSpinner = (Spinner) getView().findViewById(R.id.incid_reg_importancia_spinner);
        doImportanciaSpinner();

        mTipoIncidenciaSpinner = (Spinner) getView().findViewById(R.id.incid_reg_ambito_spinner);
        new AmbitoIncidenciaSpinnerSetter().execute();

        mIncidenciaBean = new IncidenciaBean();

        mComunidadSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Comunidad comunidad = (Comunidad) parent.getItemAtPosition(position);
                mIncidenciaBean.setComunidadId(comunidad.getC_Id());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Log.d(TAG, "mComunidadSpinner.onNothingSelected()");
            }
        });

        mImportanciaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Log.d(TAG, "mImportanciaSpinner.onItemSelected()");
                mIncidenciaBean.setImportanciaIncid((short) position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Log.d(TAG, "mImportanciaSpinner.onNothingSelected()");
            }
        });

        mTipoIncidenciaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Log.d(TAG, "onItemSelected()");
                Cursor cursor = ((CursorAdapter) parent.getAdapter()).getCursor();
                mIncidenciaBean.setCodAmbitoIncid(cursor.getShort(0)); // _ID.
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Log.d(TAG, "onNothingSelected()");
            }
        });
    }

    @Override
    public void onDestroy()
    {
        Log.d(TAG, "onDestroy()");
        /*dbHelper.close();
        mRegComunidadFrView = null;*/
        super.onDestroy();
    }

//    ============================================================
//              .......... HELPER METHDOS .......
//    ============================================================

    private void doImportanciaSpinner()
    {
        Log.d(TAG, "doImportanciaSpinner()");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(),
                R.array.IncidImportanciaArray,
                R.layout.app_spinner_1_dropdown_item);
        adapter.setDropDownViewResource(R.layout.app_spinner_1_dropdown_item);

        mImportanciaSpinner.setAdapter(adapter);
    }

//    ============================================================
//    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
//    ============================================================

    class ComunidadSpinnerSetter extends AsyncTask<Void, Void, List<Comunidad>> {

        UiException uiException;

        @Override
        protected List<Comunidad> doInBackground(Void... aVoid)
        {
            Log.d(TAG, "ComunidadSpinnerSetter.doInBackground()");
            List<Comunidad> comunidadesByUser = null;
            try {
                comunidadesByUser = IncidService.IncidenciaServ.getComusByUser();
            } catch (UiException e) {
                uiException = e;
            }
            return comunidadesByUser;
        }

        @Override
        protected void onPostExecute(List<Comunidad> comunidades)
        {
            if (comunidades != null) {
                Log.d(TAG, "ComunidadSpinnerSetter.onPostExecute(): comunidades != null");
                ArrayAdapter<Comunidad> comunidadesAdapter = new ArrayAdapter<>(
                        getActivity(),
                        R.layout.app_spinner_1_dropdown_item,
                        R.id.app_spinner_1_dropdown_item,
                        comunidades);
                mComunidadSpinner.setAdapter(comunidadesAdapter);
                /*tiposViaAdapter.setDropDownViewResource(R.layout.app_spinner_1_dropdown_item);*/
                /*if (mActivityListener != null) {
                    mActivityListener.onComunidadesSpinnerLoaded();
                }*/
            }
            if (uiException != null) {
                Log.d(TAG, "ComunidadSpinnerSetter.onPostExecute(): uiException != null");
                checkState(comunidades == null);
                uiException.getAction().doAction(getActivity(), uiException.getResourceId());
            }
        }
    }

    class AmbitoIncidenciaSpinnerSetter extends AsyncTask<Void, Void, Cursor> {

        private final String TAG = AmbitoIncidenciaSpinnerSetter.class.getCanonicalName();

        @Override
        protected Cursor doInBackground(Void... params)
        {
            Log.d(TAG, "doInBackground()");
            return dbHelper.doAmbitoIncidenciaCursor();
        }

        @Override
        protected void onPostExecute(Cursor cursor)
        {
            Log.d(TAG, "onPostExecute()");

            String[] fromColDB = new String[]{ambito};
            int[] toViews = new int[]{R.id.app_spinner_1_dropdown_item};
            CursorAdapter cursorAdapter = new SimpleCursorAdapter(
                    getActivity(),
                    R.layout.app_spinner_1_dropdown_item,
                    cursor,
                    fromColDB,
                    toViews,
                    0);
            mTipoIncidenciaSpinner.setAdapter(cursorAdapter);
            // TODO: pendiente listener como en RegComuFr (para edición incidencia).
        }
    }
}
