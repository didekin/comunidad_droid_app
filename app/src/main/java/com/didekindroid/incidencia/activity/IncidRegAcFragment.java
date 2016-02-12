package com.didekindroid.incidencia.activity;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.Spinner;

import com.didekin.usuario.dominio.Comunidad;
import com.didekindroid.R;
import com.didekindroid.incidencia.dominio.IncidenciaBean;
import com.didekindroid.incidencia.repository.IncidenciaDataDbHelper;

import static com.didekindroid.incidencia.activity.utils.IncidRegEditHelper.HELPER;

/**
 *
 */
public class IncidRegAcFragment extends Fragment implements ComuSpinnerSettable,
        AmbitoSpinnerSettable, ImportanciaSpinnerSettable {

    private static final String TAG = IncidRegAcFragment.class.getCanonicalName();

    IncidenciaBean mIncidenciaBean;
    Spinner mComunidadSpinner;
    Spinner mImportanciaSpinner;
    Spinner mAmbitoIncidenciaSpinner;
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

        mIncidenciaBean = new IncidenciaBean();
        dbHelper = new IncidenciaDataDbHelper(getActivity());
        doComunidadSpinner();
        mAmbitoIncidenciaSpinner = (Spinner) getView().findViewById(R.id.incid_reg_ambito_spinner);
        HELPER.doAmbitoIncidenciaSpinner(this);
        mImportanciaSpinner = (Spinner) getView().findViewById(R.id.incid_reg_importancia_spinner);
        HELPER.doImportanciaSpinner(this);
    }

    @Override
    public void onDestroy()
    {
        Log.d(TAG, "onDestroy()");
        dbHelper.close();
        super.onDestroy();
    }

//    ============================================================
//              .......... HELPER METHDOS .......
//    ============================================================

    @SuppressWarnings("ConstantConditions")
    private void doComunidadSpinner()
    {
        mComunidadSpinner = (Spinner) getView().findViewById(R.id.incid_reg_comunidad_spinner);
        new ComunidadSpinnerSetter<>(this).execute();

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
    }

//    ============================================================
//              .......... INTERFACE METHDOS .......
//    ============================================================

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
        mComunidadSpinner.setSelection(0);
    }

    @Override
    public void onAmbitoIncidSpinnerLoaded()
    {
        Log.d(TAG, "onAmbitoIncidSpinnerLoaded()");
        mAmbitoIncidenciaSpinner.setSelection(0);
    }

    @Override
    public void setAmbitoSpinnerAdapter(CursorAdapter cursorAdapter)
    {
        Log.d(TAG, "setAmbitoSpinnerAdapter()");
        mAmbitoIncidenciaSpinner.setAdapter(cursorAdapter);
    }

    @Override
    public IncidenciaDataDbHelper getDbHelper()
    {
        Log.d(TAG, "getDbHelper()");
        return dbHelper;
    }

    @Override
    public Spinner getAmbitoSpinner()
    {
        Log.d(TAG, "getAmbitoSpinner()");
        return mAmbitoIncidenciaSpinner;
    }

    @Override
    public IncidenciaBean getIncidenciaBean()
    {
        Log.d(TAG, "getIncidenciaBean()");
        return mIncidenciaBean;
    }

    @Override
    public Spinner getImportanciaSpinner()
    {
        Log.d(TAG, "getImportanciaSpinner()");
        return mImportanciaSpinner;
    }

    @Override
    public void onImportanciaSpinnerLoaded()
    {
        Log.d(TAG, "onImportanciaSpinnerLoaded()");
        mImportanciaSpinner.setSelection(0);
    }
}
