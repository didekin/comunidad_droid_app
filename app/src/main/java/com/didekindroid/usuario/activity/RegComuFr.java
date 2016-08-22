package com.didekindroid.usuario.activity;


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
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.didekin.usuario.dominio.Municipio;
import com.didekin.usuario.dominio.Provincia;
import com.didekindroid.R;
import com.didekindroid.usuario.dominio.ComunidadBean;
import com.didekindroid.usuario.repository.UsuarioDataDbHelper;

import static com.didekindroid.usuario.repository.UsuarioDataDb.ComunidadAutonoma.cu_nombre;
import static com.didekindroid.usuario.repository.UsuarioDataDb.Municipio.mu_nombre;
import static com.didekindroid.usuario.repository.UsuarioDataDb.Provincia.pr_nombre;
import static com.didekindroid.usuario.repository.UsuarioDataDb.TipoVia.tipovia;

public class RegComuFr extends Fragment {

    private static final String TAG = RegComuFr.class.getCanonicalName();

    private UsuarioDataDbHelper dbHelper;

    private View mRegComunidadFrView;
    Spinner mTipoViaSpinner;
    Spinner mAutonomaComuSpinner;
    Spinner provinciaSpinner;
    Spinner municipioSpinner;

    int mCApointer;
    int mProvinciaPointer;
    int mMunicipioPointer;
    int mTipoViaPointer;

    RegComuFrListener mActivityListener;

    private ComunidadBean comunidadBean;

    public RegComuFr()
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
        mRegComunidadFrView = inflater.inflate(R.layout.reg_comu_fr, container, false);
        return mRegComunidadFrView;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated()");

        dbHelper = new UsuarioDataDbHelper(getActivity());
        // Asynchronous call: initialize database if necessary.
        new TipoViaComuAutoSpinnersSetter().execute();

        mTipoViaSpinner = (Spinner) getView().findViewById(R.id.tipo_via_spinner);
//        mTipoViaSpinner.setFocusable(true);
//        mTipoViaSpinner.setFocusableInTouchMode(true);
//        mTipoViaSpinner.requestFocus();
        mAutonomaComuSpinner = (Spinner) getView().findViewById(R.id.autonoma_comunidad_spinner);
        provinciaSpinner = (Spinner) getView().findViewById(R.id.provincia_spinner);
        municipioSpinner = (Spinner) getView().findViewById(R.id.municipio_spinner);
        comunidadBean = new ComunidadBean();

        // ................................ LISTENERS ............................................

        mTipoViaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Log.d(TAG, "In mTipoViaSpinner.setOnItemSelectedListener, onItemSelected()");
                comunidadBean.setTipoVia(((Cursor) parent.getItemAtPosition(position)).getString(1));
                mTipoViaPointer = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Log.d(TAG, "In mTipoViaSpinner.setOnItemSelectedListener, onNothingSelected()");
            }
        });

        mAutonomaComuSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Log.d(TAG, "In mAutonomaComuSpinner.setOnItemSelectedListener, onItemSelected()");

                short cu_id = (short) id;
                new SpinnerProvinciasLoader().execute(cu_id);
                mCApointer = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Log.d(TAG, "In mAutonomaComuSpinner.setOnItemSelectedListener, onNothingSelected()");
            }
        });

        provinciaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Log.d(TAG, "In provinciasSpinner.setOnItemSelectedListener, onItemSelected()");

                comunidadBean.setMunicipio(null);
                short prId = (short) id;
                new SpinnerMunicipioLoader().execute(prId);
                mProvinciaPointer = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Log.d(TAG, "In provinciasSpinner.setOnItemSelectedListener, onNothingSelected");
            }
        });

        municipioSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Log.d(TAG, "In municipiosSpinner.setOnItemSelectedListener, onItemSelected()");

                Cursor cursor = ((CursorAdapter) parent.getAdapter()).getCursor();
                cursor.moveToPosition(position);
                Municipio municipio = new Municipio(cursor.getShort(2), new Provincia(cursor.getShort(1)));
                comunidadBean.setMunicipio(municipio);
                mMunicipioPointer = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Log.d(TAG, "In municipiosSpinner.setOnItemSelectedListener, onNothingSelected()");
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        Log.d(TAG, "onSaveInstanceState()");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop()
    {
        Log.d(TAG, "onStop()");
        dbHelper.close();
        super.onStop();
    }

    @Override
    public void onDestroy()
    {
        Log.d(TAG, "onDestroy()");

        if (mActivityListener != null) {
            mActivityListener.onDestroyFragment();
        }
        mRegComunidadFrView = null;
        super.onDestroy();
    }

// ================ Interface to communicate with the Activity ================

    public interface RegComuFrListener {

        void onTipoViaSpinnerLoaded();

        void onCAutonomaSpinnerLoaded();

        void onProvinciaSpinnerLoaded();

        void onMunicipioSpinnerLoaded();

        void onDestroyFragment();
    }

    void setmActivityListener(RegComuFrListener mActivityListener)
    {
        this.mActivityListener = mActivityListener;
    }

//    ============================================================
//    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
//    ============================================================

    public View getFragmentView()
    {
        return mRegComunidadFrView;
    }

    public ComunidadBean getComunidadBean()
    {
        return comunidadBean;
    }

//  --------------------------------------------------------------------
//                               SPINNERS
//  --------------------------------------------------------------------

    private SpinnerAdapter doAdapterSpinner(Cursor cursor, String[] fromColDB)
    {
        Log.d(TAG, "In doAdapterSpinner()");

        int[] toViews = new int[]{R.id.app_spinner_1_dropdown_item};
        return new SimpleCursorAdapter(
                getActivity(),
                R.layout.app_spinner_1_dropdown_item,
                cursor,
                fromColDB,
                toViews,
                0);
    }

///    ::::::::::::::: TIPO DE VÍA - COMUNIDAD AUTONOMA ::::::::::::::::

    class TipoViaComuAutoSpinnersSetter extends AsyncTask<Void, Void, Void> {

        Cursor tipoViaCursor;
        Cursor comunidadAutonomaCursor;

        @Override
        protected Void doInBackground(Void... params)
        {
            Log.d(TAG, "In TipoViaComuAutoSpinnersSetter.doInBackground()");
            tipoViaCursor = dbHelper.doTipoViaCursor();
            comunidadAutonomaCursor = dbHelper.doComunidadesCursor();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            Log.d(TAG, "In TipoViaComuAutoSpinnersSetter.onPostExecute()");

            String[] fromColumnDb = new String[]{tipovia};
            mTipoViaSpinner.setAdapter(doAdapterSpinner(tipoViaCursor, fromColumnDb));

            fromColumnDb = new String[]{cu_nombre};
            mAutonomaComuSpinner.setAdapter(doAdapterSpinner(comunidadAutonomaCursor, fromColumnDb));

            if (mActivityListener != null) {
                mActivityListener.onTipoViaSpinnerLoaded();
                mActivityListener.onCAutonomaSpinnerLoaded();
            }
        }
    }

///    :::::::::::::::::: PROVINCIA ::::::::::::::::::::::

    class SpinnerProvinciasLoader extends AsyncTask<Short, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Short... params)
        {
            Log.d(TAG, "In SpinnerProvinciasLoader.doInBackground()");

            Cursor provinciasCAcursor = dbHelper.getProvinciasByCA(params[0]);
            Log.d(TAG, "In SpinnerProvinciasLoader.doInBackground() : cursor count = " + provinciasCAcursor.getCount());
            return provinciasCAcursor;
        }

        @Override
        protected void onPostExecute(Cursor provinciasCursor)
        {
            Log.d(TAG, "In SpinnerProvinciasLoader.onPostExecute()");

            String[] fromColDb = new String[]{pr_nombre};
            provinciaSpinner.setAdapter(doAdapterSpinner(provinciasCursor, fromColDb));
            if (mActivityListener != null) {
                mActivityListener.onProvinciaSpinnerLoaded();
            }
        }
    }

///   :::::::::::::::::  MUNICIPIO ::::::::::::::::::::

    class SpinnerMunicipioLoader extends AsyncTask<Short, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Short... params)
        {
            Log.d(TAG, "In SpinnerMunicipioLoader.doInBackground()");
            Cursor municipiosCursor = dbHelper.getMunicipiosByPrId(params[0]);
            Log.d(TAG, "In SpinnerMunicipiosLoader.doInBackground() : cursor count = " + municipiosCursor.getCount());
            return municipiosCursor;
        }

        @Override
        protected void onPostExecute(Cursor municipiosCursor)
        {
            Log.d(TAG, "In SpinnerMunicipioLoader.onPostExecute()");

            String[] fromColDb = new String[]{mu_nombre};
            municipioSpinner.setAdapter(doAdapterSpinner(municipiosCursor, fromColDb));

            if (mActivityListener != null) {
                mActivityListener.onMunicipioSpinnerLoaded();
            }
        }
    }
}
