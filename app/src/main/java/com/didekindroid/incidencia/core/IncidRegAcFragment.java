package com.didekindroid.incidencia.core;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.Spinner;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf;
import com.didekindroid.incidencia.spinner.AmbitoSpinnerSettable;
import com.didekindroid.incidencia.spinner.ImportanciaSpinnerSettable;
import com.didekindroid.incidencia.spinner.ManagerComuSpinnerIf;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.incidencia.dominio.Incidencia;

import timber.log.Timber;

import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.spinner.IncidSpinnersHelper.HELPER;
import static com.didekindroid.incidencia.spinner.ViewerComuSpinner.newComuSpinnerViewer;
import static com.didekindroid.util.UIutils.closeCursor;

/**
 *
 */
@SuppressWarnings("ConstantConditions")
public class IncidRegAcFragment extends Fragment implements AmbitoSpinnerSettable,
        ImportanciaSpinnerSettable, ManagerComuSpinnerIf {

    IncidenciaBean mIncidenciaBean;
    Spinner mImportanciaSpinner;
    Spinner mAmbitoIncidenciaSpinner;
    IncidenciaDataDbHelper dbHelper;
    View rootFrgView;
    IncidImportanciaBean mIncidImportanciaBean;

    ManagerComuSpinnerIf comuSpinnerManager;
    ViewerComuSpinnerIf comuSpinnerViewer;
    Spinner comunidadSpinner;
    int comunidadSelectedIndex;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        Timber.d("onAttach()");
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedState)
    {
        Timber.d("onCreateView()");
        rootFrgView = inflater.inflate(R.layout.incid_reg_frg, container, false);
        return rootFrgView;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onActivityCreated(Bundle savedState)
    {
        Timber.d("onActivityCreated()");
        super.onActivityCreated(savedState);

        mIncidenciaBean = new IncidenciaBean();
        mIncidImportanciaBean = new IncidImportanciaBean();
        dbHelper = new IncidenciaDataDbHelper(getActivity());

        mAmbitoIncidenciaSpinner = (Spinner) getView().findViewById(R.id.incid_reg_ambito_spinner);
        HELPER.doAmbitoIncidenciaSpinner(this);
        mImportanciaSpinner = (Spinner) getView().findViewById(R.id.incid_reg_importancia_spinner);
        HELPER.doImportanciaSpinner(this);

        comuSpinnerManager = this;
        comuSpinnerViewer = newComuSpinnerViewer(comuSpinnerManager).setDataInView(savedState);
    }

    @Override
    public void onSaveInstanceState(Bundle savedState)
    {
        Timber.d("onSaveInstanceState()");
        comuSpinnerViewer.saveSelectedIndex(savedState);
        super.onSaveInstanceState(savedState);
    }

    @Override
    public void onDestroy()
    {
        Timber.d("onDestroy()");
        super.onDestroy();
        closeCursor(mAmbitoIncidenciaSpinner.getAdapter());
        dbHelper.close();
        comuSpinnerViewer.clearControllerSubscriptions();
    }

//    ============================================================
//              .......... HELPER METHDOS .......
//    ============================================================

    public View getRootFrgView()
    {
        return rootFrgView;
    }

//    ============================================================
//    .................... AMBITO INCIDENCIA SPINNER .............
//    ============================================================

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

//    ============================================================================
//    ....................... IMPORTANCIA SPINNER VIEWER .........................
//    ============================================================================

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

//    ============================================================================
//    ....................... MANAGER COMUNIDAD SPINNER  .........................
//    ============================================================================

    @Override
    public long getComunidadIdInIntent()
    {
        Timber.d("getComunidadIdInIntent()");
        return getActivity().getIntent().getLongExtra(COMUNIDAD_ID.key, 0);
    }

    @Override
    public UiExceptionIf.ActionForUiExceptionIf processViewerError(UiException ui)
    {
        Timber.d("processViewerError()");
        return ui.processMe(getActivity(), new Intent());
    }

    @Override
    public void replaceRootView(Object initParamsForView)
    {

    }

    public Spinner initSpinnerView()
    {
        Timber.d("initSpinnerView()");
        Spinner spinnerToInit = getSpinnerViewInManager();
        spinnerToInit.setOnItemSelectedListener(getSpinnerListener());
        return spinnerToInit;
    }

    @Override
    public Spinner getSpinnerViewInManager()
    {
        Timber.d("getSpinnerViewInManager()");
        return (Spinner) rootFrgView.findViewById(R.id.incid_reg_comunidad_spinner);
    }

    @Override
    public AdapterView.OnItemSelectedListener getSpinnerListener()
    {
        Timber.d("getSpinnerListener()");
        return new ComuSelectedListener();
    }

    // ...................... HELPERS ....................

    class ComuSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            Timber.d("comunidadSpinner.onItemSelected()");
            comunidadSelectedIndex = position;
            Comunidad comunidad = (Comunidad) parent.getItemAtPosition(position);
            mIncidenciaBean.setComunidadId(comunidad.getC_Id());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        { Timber.d("comunidadSpinner.onNothingSelected()"); }
    }
}
