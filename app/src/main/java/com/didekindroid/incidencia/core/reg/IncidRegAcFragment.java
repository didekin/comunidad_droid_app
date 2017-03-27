package com.didekindroid.incidencia.core.reg;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.Spinner;

import com.didekindroid.R;
import com.didekindroid.api.CtrlerSpinnerIf;
import com.didekindroid.api.ViewerSelectableIf;
import com.didekindroid.incidencia.core.IncidImportanciaBean;
import com.didekindroid.incidencia.core.IncidenciaBean;
import com.didekindroid.incidencia.core.IncidenciaDataDbHelper;
import com.didekindroid.incidencia.spinner.AmbitoSpinnerSettable;
import com.didekindroid.incidencia.spinner.ImportanciaSpinnerSettable;
import com.didekindroid.usuario.firebase.ViewerFirebaseTokenIf;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.incidencia.dominio.Incidencia;

import timber.log.Timber;

import static com.didekindroid.comunidad.spinner.ViewerComuSpinner.newViewerComuSpinner;
import static com.didekindroid.incidencia.spinner.IncidSpinnersHelper.HELPER;
import static com.didekindroid.usuario.firebase.ViewerFirebaseToken.newViewerFirebaseToken;
import static com.didekindroid.util.UIutils.closeCursor;

/**
 *
 */
@SuppressWarnings("ConstantConditions")
public class IncidRegAcFragment extends Fragment implements AmbitoSpinnerSettable, ImportanciaSpinnerSettable {

    IncidenciaBean mIncidenciaBean;
    Spinner mImportanciaSpinner;
    Spinner mAmbitoIncidenciaSpinner;
    IncidenciaDataDbHelper dbHelper;
    View rootFrgView;
    IncidImportanciaBean mIncidImportanciaBean;

    ViewerSelectableIf<Spinner, CtrlerSpinnerIf> viewerComuSpinner;
    ViewerFirebaseTokenIf viewerFirebaseToken;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedState)
    {
        Timber.d("onCreateView()");
        rootFrgView = inflater.inflate(R.layout.incid_reg_frg, container, false);
        return rootFrgView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedState)
    {
        Timber.d("onViewCreated()");
        super.onViewCreated(view, savedState);

        mIncidenciaBean = new IncidenciaBean();
        mIncidImportanciaBean = new IncidImportanciaBean();
        dbHelper = new IncidenciaDataDbHelper(getActivity());

        mAmbitoIncidenciaSpinner = (Spinner) getView().findViewById(R.id.incid_reg_ambito_spinner);
        HELPER.doAmbitoIncidenciaSpinner(this);
        mImportanciaSpinner = (Spinner) getView().findViewById(R.id.incid_reg_importancia_spinner);
        HELPER.doImportanciaSpinner(this);

        viewerComuSpinner = newViewerComuSpinner((Spinner) rootFrgView.findViewById(R.id.incid_reg_comunidad_spinner), getActivity(), null);
        viewerComuSpinner.doViewInViewer(savedState);
    }

    @Override
    public void onStart()
    {
        Timber.d("onStart()");
        super.onStart();
        viewerFirebaseToken = newViewerFirebaseToken(getActivity());
        viewerFirebaseToken.checkGcmTokenAsync();
    }

    @Override
    public void onSaveInstanceState(Bundle savedState)
    {
        Timber.d("onSaveInstanceState()");
        viewerComuSpinner.saveState(savedState);
        super.onSaveInstanceState(savedState);
    }

    @Override
    public void onDestroy()
    {
        Timber.d("onDestroy()");
        super.onDestroy();
        closeCursor(mAmbitoIncidenciaSpinner.getAdapter());
        dbHelper.close();
        viewerComuSpinner.clearSubscriptions();
        viewerFirebaseToken.clearSubscriptions();
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

    // ...................... HELPERS ....................

    @SuppressWarnings("WeakerAccess")
    class ComuSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            Timber.d("comunidadSpinner.onItemSelected()");
            Comunidad comunidad = (Comunidad) parent.getItemAtPosition(position);
            mIncidenciaBean.setComunidadId(comunidad.getC_Id());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {
            Timber.d("comunidadSpinner.onNothingSelected()");
        }
    }
}
