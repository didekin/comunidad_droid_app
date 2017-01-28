package com.didekindroid.incidencia.activity.incidreg;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.Spinner;

import com.didekindroid.R;
import com.didekindroid.incidencia.IncidenciaDataDbHelper;
import com.didekindroid.incidencia.activity.utils.AmbitoSpinnerSettable;
import com.didekindroid.incidencia.activity.utils.ComuSpinnerSettable;
import com.didekindroid.incidencia.activity.utils.ComunidadSpinnerSetter;
import com.didekindroid.incidencia.activity.utils.ImportanciaSpinnerSettable;
import com.didekindroid.incidencia.dominio.IncidImportanciaBean;
import com.didekindroid.incidencia.dominio.IncidenciaBean;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.incidencia.dominio.Incidencia;

import timber.log.Timber;

import static com.didekindroid.incidencia.activity.utils.IncidSpinnersHelper.HELPER;
import static com.didekindroid.util.UIutils.closeCursor;

/**
 *
 */
@SuppressWarnings("ConstantConditions")
public class IncidRegAcFragment extends Fragment implements ComuSpinnerSettable,
        AmbitoSpinnerSettable, ImportanciaSpinnerSettable {

    IncidenciaBean mIncidenciaBean;
    Spinner mComunidadSpinner;
    Spinner mImportanciaSpinner;
    Spinner mAmbitoIncidenciaSpinner;
    IncidenciaDataDbHelper dbHelper;
    View mFragmentView;
    IncidImportanciaBean mIncidImportanciaBean;

    public IncidRegAcFragment()
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
        mFragmentView = inflater.inflate(R.layout.incid_reg_frg, container, false);
        return mFragmentView;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        Timber.d("onActivityCreated()");

        mIncidenciaBean = new IncidenciaBean();
        mIncidImportanciaBean = new IncidImportanciaBean();
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
        Timber.d("onDestroy()");
        closeCursor(mAmbitoIncidenciaSpinner.getAdapter());
        dbHelper.close();
        super.onDestroy();
    }

//    ============================================================
//              .......... HELPER METHDOS .......
//    ============================================================

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
                Timber.d("mComunidadSpinner.onNothingSelected()");
            }
        });
    }

    public View getmFragmentView()
    {
        return mFragmentView;
    }

//    ============================================================
//              .......... INTERFACE METHDOS .......
//    ============================================================

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
        mComunidadSpinner.setSelection(0);
    }

    @Override
    public void onAmbitoIncidSpinnerLoaded()
    {
        Timber.d("onAmbitoIncidSpinnerLoaded()");
        mAmbitoIncidenciaSpinner.setSelection(0);
    }

    @Override
    public void setAmbitoSpinnerAdapter(CursorAdapter cursorAdapter)
    {
        Timber.d("setAmbitoSpinnerAdapter()");
        mAmbitoIncidenciaSpinner.setAdapter(cursorAdapter);
    }

    @Override
    public IncidenciaDataDbHelper getDbHelper()
    {
        Timber.d("getDbHelper()");
        return dbHelper;
    }

    @Override
    public Spinner getAmbitoSpinner()
    {
        Timber.d("getAmbitoSpinner()");
        return mAmbitoIncidenciaSpinner;
    }

    @Override
    public IncidenciaBean getIncidenciaBean()
    {
        Timber.d("getIncidenciaBean()");
        return mIncidenciaBean;
    }

    @Override
    public IncidImportanciaBean getIncidImportanciaBean()
    {
        Timber.d("getIncidImportanciaBean()");
        return mIncidImportanciaBean;
    }

    @Override
    public Spinner getImportanciaSpinner()
    {
        Timber.d("getImportanciaSpinner()");
        return mImportanciaSpinner;
    }

    @Override
    public void onImportanciaSpinnerLoaded()
    {
        Timber.d("onImportanciaSpinnerLoaded()");
        mImportanciaSpinner.setSelection(0);
    }

    @Override
    public Incidencia getIncidencia()
    {
        Timber.d("getIncidencia()");
        throw new UnsupportedOperationException("getIncidencia() not supported");
    }
}
