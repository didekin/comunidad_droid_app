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

import com.didekindroid.R;
import com.didekindroid.incidencia.dominio.IncidenciaBean;
import com.didekindroid.incidencia.repository.IncidenciaDataDbHelper;

import static com.didekindroid.incidencia.repository.IncidenciaDataDb.AmbitoIncidencia.ambito;

/**
 *
 */
public class IncidRegAcFragment extends Fragment {

    private static final String TAG = IncidRegAcFragment.class.getCanonicalName();

    IncidenciaBean mIncidenciaBean;
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

        mImportanciaSpinner = (Spinner) getView().findViewById(R.id.incid_reg_importancia_spinner);
        doImportanciaSpinner();

        mTipoIncidenciaSpinner = (Spinner) getView().findViewById(R.id.incid_reg_ambito_spinner);
        new AmbitoIncidenciaSpinnerSetter().execute();

        mIncidenciaBean = new IncidenciaBean();

        mImportanciaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Log.d(TAG, "onItemSelected()");
                mIncidenciaBean.setImportanciaIncid((short) position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Log.d(TAG, "onNothingSelected()");
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
        Log.d(TAG,"doImportanciaSpinner()");

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

    class AmbitoIncidenciaSpinnerSetter extends AsyncTask<Void,Void, Cursor> {

        private final String TAG = AmbitoIncidenciaSpinnerSetter.class.getCanonicalName();

        @Override
        protected Cursor doInBackground(Void... params)
        {
            Log.d(TAG,"doInBackground()");
            return dbHelper.doAmbitoIncidenciaCursor();
        }

        @Override
        protected void onPostExecute(Cursor cursor)
        {
            Log.d(TAG,"onPostExecute()");

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
