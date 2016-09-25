package com.didekindroid.usuario.activity;


import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
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

import timber.log.Timber;

import static com.didekindroid.common.utils.UIutils.closeCursor;
import static com.didekindroid.usuario.repository.UsuarioDataDb.ComunidadAutonoma.cu_nombre;
import static com.didekindroid.usuario.repository.UsuarioDataDb.Municipio.mu_nombre;
import static com.didekindroid.usuario.repository.UsuarioDataDb.Provincia.pr_nombre;
import static com.didekindroid.usuario.repository.UsuarioDataDb.TipoVia.tipovia;

public class RegComuFr extends Fragment {

    UsuarioDataDbHelper dbHelper;

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

    ComunidadBean comunidadBean;

    public RegComuFr()
    {
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        Timber.d("onAttach()");
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Timber.d("onCreateView()");
        mRegComunidadFrView = inflater.inflate(R.layout.reg_comu_fr, container, false);
        return mRegComunidadFrView;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        Timber.d("onActivityCreated()");

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
                Timber.d("In mTipoViaSpinner.setOnItemSelectedListener, onItemSelected()");
                comunidadBean.setTipoVia(((Cursor) parent.getItemAtPosition(position)).getString(1));
                mTipoViaPointer = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Timber.d("In mTipoViaSpinner.setOnItemSelectedListener, onNothingSelected()");
            }
        });

        mAutonomaComuSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Timber.d("In mAutonomaComuSpinner.setOnItemSelectedListener, onItemSelected()");

                short cu_id = (short) id;
                new SpinnerProvinciasLoader().execute(cu_id);
                mCApointer = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Timber.d("In mAutonomaComuSpinner.setOnItemSelectedListener, onNothingSelected()");
            }
        });

        provinciaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Timber.d("In provinciasSpinner.setOnItemSelectedListener, onItemSelected()");

                comunidadBean.setMunicipio(null);
                short prId = (short) id;
                new SpinnerMunicipioLoader().execute(prId);
                mProvinciaPointer = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Timber.d("In provinciasSpinner.setOnItemSelectedListener, onNothingSelected");
            }
        });

        municipioSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Timber.d("In municipiosSpinner.setOnItemSelectedListener, onItemSelected()");

                Cursor cursor = ((CursorAdapter) parent.getAdapter()).getCursor();
                cursor.moveToPosition(position);
                Municipio municipio = new Municipio(cursor.getShort(2), new Provincia(cursor.getShort(1)));
                comunidadBean.setMunicipio(municipio);
                mMunicipioPointer = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Timber.d("In municipiosSpinner.setOnItemSelectedListener, onNothingSelected()");
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        Timber.d("onSaveInstanceState()");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop()
    {
        Timber.d("onStop()");
        super.onStop();
    }

    @Override
    public void onDestroy()
    {
        Timber.d("onDestroy()");

        if (mActivityListener != null) {
            mActivityListener.onDestroyFragment();
        }
        mRegComunidadFrView = null;
        closeCursor(mTipoViaSpinner.getAdapter());
        closeCursor(mAutonomaComuSpinner.getAdapter());
        closeCursor(provinciaSpinner.getAdapter());
        closeCursor(municipioSpinner.getAdapter());
        dbHelper.close();
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

    SpinnerAdapter doAdapterSpinner(Cursor cursor, String[] fromColDB)
    {
        Timber.d("In doAdapterSpinner()");

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
            Timber.d("In TipoViaComuAutoSpinnersSetter.doInBackground()");
            tipoViaCursor = dbHelper.doTipoViaCursor();
            comunidadAutonomaCursor = dbHelper.doComunidadesCursor();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            Timber.d("In TipoViaComuAutoSpinnersSetter.onPostExecute()");

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
            Timber.d("In SpinnerProvinciasLoader.doInBackground()");

            final Cursor provinciasCAcursor = dbHelper.getProvinciasByCA(params[0]);
            Timber.d("In SpinnerProvinciasLoader.doInBackground() : cursor count = %d%n", provinciasCAcursor.getCount());
            return provinciasCAcursor;
        }

        @Override
        protected void onPostExecute(final Cursor provinciasCursor)
        {
            Timber.d("In SpinnerProvinciasLoader.onPostExecute()");

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
            Timber.d("In SpinnerMunicipioLoader.doInBackground()");
            final Cursor municipiosCursor = dbHelper.getMunicipiosByPrId(params[0]);
            Timber.d("In SpinnerMunicipiosLoader.doInBackground() : cursor count = %d%n", municipiosCursor.getCount());
            return municipiosCursor;
        }

        @Override
        protected void onPostExecute(final Cursor municipiosCursor)
        {
            Timber.d("In SpinnerMunicipioLoader.onPostExecute()");

            String[] fromColDb = new String[]{mu_nombre};
            municipioSpinner.setAdapter(doAdapterSpinner(municipiosCursor, fromColDb));

            if (mActivityListener != null) {
                mActivityListener.onMunicipioSpinnerLoaded();
            }
        }
    }
}
