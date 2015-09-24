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
import android.widget.*;
import com.didekin.serviceone.domain.Municipio;
import com.didekin.serviceone.domain.Provincia;
import com.didekindroid.R;
import com.didekindroid.ioutils.IoHelper;
import com.didekindroid.repository.MasterDataDbHelper;
import com.didekindroid.usuario.dominio.ComunidadBean;

import java.util.List;

import static com.didekindroid.repository.MasterDataDb.ComunidadAutonoma.cu_nombre;
import static com.didekindroid.repository.MasterDataDb.Municipio.mu_nombre;
import static com.didekindroid.repository.MasterDataDb.Provincia.pr_nombre;

public class RegComuFr extends Fragment {

    private static final String TAG = RegComuFr.class.getCanonicalName();

    private static final String CA_POINTER_POS = RegComuFr.class.getSimpleName().concat(".mCApointer");
    private static final String PROV_POINTER_POS =
            RegComuFr.class.getSimpleName().concat(".mProvinciaPointer");
    private static final String MUNI_POINTER_POS =
            RegComuFr.class.getSimpleName().concat(".mMunicipioPointer");
    private static final String TIPO_VIA_POINTER_POS =
            RegComuFr.class.getSimpleName().concat(".mTipoViaPointer");

    private static List<String> TIPOS_VIA;

    private MasterDataDbHelper dbHelper;

    private View mRegComunidadFrView;
    private Spinner mTipoViaSpinner;
    private Spinner autonomaComuSpinner;
    private Spinner provinciaSpinner;
    private Spinner municipioSpinner;

    private int mCApointer;
    private int mProvinciaPointer;
    private int mMunicipioPointer;
    private int mTipoViaPointer;

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

        if (savedInstanceState != null) {
            mCApointer = savedInstanceState.getInt(CA_POINTER_POS);
            mProvinciaPointer = savedInstanceState.getInt(PROV_POINTER_POS);
            mMunicipioPointer = savedInstanceState.getInt(MUNI_POINTER_POS);
            mTipoViaPointer = savedInstanceState.getInt(TIPO_VIA_POINTER_POS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView()");
        mRegComunidadFrView = inflater.inflate(R.layout.reg_comu_fr, container, false);
        return mRegComunidadFrView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated()");

        dbHelper = new MasterDataDbHelper(getActivity());
        new SpinnerCAutonomasLoader().execute();

        mTipoViaSpinner = (Spinner) getView().findViewById(R.id.tipo_via_spinner);
        mTipoViaSpinner.setFocusable(true);
        mTipoViaSpinner.setFocusableInTouchMode(true);
        mTipoViaSpinner.requestFocus();
        autonomaComuSpinner = (Spinner) getView().findViewById(R.id.autonoma_comunidad_spinner);
        provinciaSpinner = (Spinner) getView().findViewById(R.id.provincia_spinner);
        municipioSpinner = (Spinner) getView().findViewById(R.id.municipio_spinner);
        comunidadBean = new ComunidadBean();

        setTipoViaSpinnerAdapter();  // Initialize with TIPOS_VIA array.

        // ............... LISTENERS ...................

        mTipoViaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Log.d(TAG, "In mTipoViaSpinner.setOnItemSelectedListener, onItemSelected()");
                comunidadBean.setTipoVia(parent.getItemAtPosition(position).toString());
                mTipoViaPointer = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Log.d(TAG, "In mTipoViaSpinner.setOnItemSelectedListener, onNothingSelected()");
            }
        });

        autonomaComuSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                Log.d(TAG, "In autonomaComuSpinner.setOnItemSelectedListener, onItemSelected()");

//                comunidadBean.setProvincia(null);
                short cu_id = (short) id;
                new SpinnerProvinciasLoader().execute(cu_id);
                mCApointer = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Log.d(TAG, "In autonomaComuSpinner.setOnItemSelectedListener, onNothingSelected()");
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
//                comunidadBean.setProvincia(new Provincia((short) id));
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

                /*Provincia provincia = comunidadBean.getProvincia();

                if (provincia == null) {
                    provincia = new Provincia(cursor.getShort(1));
                    comunidadBean.setProvincia(provincia);
                }*/
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

        outState.putInt(CA_POINTER_POS, mCApointer);
        outState.putInt(PROV_POINTER_POS, mProvinciaPointer);
        outState.putInt(MUNI_POINTER_POS, mMunicipioPointer);
        outState.putInt(TIPO_VIA_POINTER_POS, mTipoViaPointer);
    }

    @Override
    public void onDestroy()
    {
        Log.d(TAG, "onDestroy()");
        dbHelper.close();
        mRegComunidadFrView = null;
        super.onDestroy();
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

//  :::::::::::::::::::: SPINNERS :::::::::::::::::::::
//  ---------------------------------------------------

    private SimpleCursorAdapter doAdapterSpinner(Cursor cursor, String[] fromColDB)
    {
        Log.d(TAG, "In doAdapterSpinner()");

        int[] toViews = new int[]{R.id.reg_comunidad_spinner_dropdown_item};
        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.reg_comu_spinner_dropdown_item, cursor, fromColDB, toViews, 0);
        return cursorAdapter;
    }

//    :::::::::::: TIPO DE VÍA :::::::::::

    private void setTipoViaSpinnerAdapter()
    {
        Log.d(TAG, "setTipoViaSpinnerAdapter()");

        List<String> tiposVia = IoHelper.doArrayFromFile(RegComuFr.this.getActivity());
        tiposVia.add(0, getResources().getString(R.string.tipo_via_spinner));

        synchronized (this) {
            TIPOS_VIA = tiposVia;
        }

        ArrayAdapter<String> tiposViaAdapter =
                new ArrayAdapter<String>(
                        RegComuFr.this.getActivity(),
                        R.layout.reg_comu_spinner_dropdown_item,
                        TIPOS_VIA);

        tiposViaAdapter.setDropDownViewResource(R.layout.reg_comu_spinner_dropdown_item);
        mTipoViaSpinner.setAdapter(tiposViaAdapter);
        mTipoViaSpinner.setSelection(mMunicipioPointer);
    }

///    ::::::::::::::: COMUNIDAD AUTÓNOMA ::::::::::::::::

    protected class SpinnerCAutonomasLoader extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... params)
        {
            Log.d(TAG, "In SpinnerCAutonomasLoader.doInBackground()");

            Cursor comunidadesCursor = dbHelper.doComunidadesCursor();
            return comunidadesCursor;
        }

        @Override
        protected void onPostExecute(Cursor comunidadesCursor)
        {
            Log.d(TAG, "In SpinnerCAutonomasLoader.onPostExecute()");

            String[] fromColDb = new String[]{cu_nombre};
            autonomaComuSpinner.setAdapter(doAdapterSpinner(comunidadesCursor, fromColDb));
            autonomaComuSpinner.setSelection(mCApointer);
        }
    }

///    :::::::::::::::: PROVINCIA :::::::::::::::

    protected class SpinnerProvinciasLoader extends AsyncTask<Short, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Short... params)
        {
            Log.d(TAG, "In SpinnerProvinciasLoader.doInBackground()");

            Cursor provinciasCAcursor = dbHelper.getProvinciasByCA(params[0]);
            return provinciasCAcursor;
        }

        @Override
        protected void onPostExecute(Cursor provinciasCursor)
        {
            Log.d(TAG, "In SpinnerProvinciasLoader.onPostExecute()");

            String[] fromColDb = new String[]{pr_nombre};
            provinciaSpinner.setAdapter(doAdapterSpinner(provinciasCursor, fromColDb));
            provinciaSpinner.setSelection(mProvinciaPointer);
        }
    }

///   :::::::::::::::::  MUNICIPIO ::::::::::::::::::::

    protected class SpinnerMunicipioLoader extends AsyncTask<Short, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Short... params)
        {
            Cursor municipiosCursor = dbHelper.getMunicipiosByPrId(params[0]);
            return municipiosCursor;
        }

        @Override
        protected void onPostExecute(Cursor municipiosCursor)
        {
            String[] fromColDb = new String[]{mu_nombre};
            municipioSpinner.setAdapter(doAdapterSpinner(municipiosCursor, fromColDb));
            municipioSpinner.setSelection(mMunicipioPointer);
        }
    }
}
