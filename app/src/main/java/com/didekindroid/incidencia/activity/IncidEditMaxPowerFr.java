package com.didekindroid.incidencia.activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekin.incidservice.dominio.Incidencia;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.incidencia.dominio.IncidImportanciaBean;
import com.didekindroid.incidencia.dominio.IncidenciaBean;
import com.didekindroid.incidencia.repository.IncidenciaDataDbHelper;

import static android.view.View.GONE;
import static com.didekindroid.common.utils.ConnectionUtils.checkInternetConnected;
import static com.didekindroid.common.utils.UIutils.getErrorMsgBuilder;
import static com.didekindroid.common.utils.UIutils.makeToast;
import static com.didekindroid.incidencia.activity.utils.IncidSpinnersHelper.HELPER;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.google.common.base.Preconditions.checkState;

/**
 * User: pedro@didekin
 * Date: 22/01/16
 * Time: 16:16
 */

@SuppressWarnings("ConstantConditions")
public class IncidEditMaxPowerFr extends Fragment implements AmbitoSpinnerSettable,
        ImportanciaSpinnerSettable {

    private static final String TAG = IncidEditMaxPowerFr.class.getCanonicalName();
    View fFragmentView;
    IncidImportancia mIncidImportancia;
    Spinner mAmbitoIncidSpinner;
    Spinner mImportanciaSpinner;
    IncidenciaDataDbHelper dbHelper;
    IncidenciaBean mIncidenciaBean;
    IncidImportanciaBean mIncidImportanciaBean;
    Button mButtonModify;
    Button mButtonErase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView()");
        fFragmentView = inflater.inflate(R.layout.incid_edit_maxpower_fr, container, false);
        return fFragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Log.d(TAG, "onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
        mIncidImportancia = ((IncidenciaDataSupplier) getActivity()).getIncidImportancia();
        boolean flagResolucion = ((IncidenciaDataSupplier) getActivity()).getFlagResolucion();

        mIncidenciaBean = new IncidenciaBean();
        mIncidImportanciaBean = new IncidImportanciaBean();
        dbHelper = new IncidenciaDataDbHelper(getActivity());

        mIncidenciaBean.setComunidadId(mIncidImportancia.getIncidencia().getComunidad().getC_Id());
        ((TextView) fFragmentView.findViewById(R.id.incid_comunidad_txt)).setText(mIncidImportancia.getIncidencia().getComunidad().getNombreComunidad());
        ((EditText) fFragmentView.findViewById(R.id.incid_reg_desc_ed)).setText(mIncidImportancia.getIncidencia().getDescripcion());

        mAmbitoIncidSpinner = (Spinner) getView().findViewById(R.id.incid_reg_ambito_spinner);
        HELPER.doAmbitoIncidenciaSpinner(this);
        mImportanciaSpinner = (Spinner) getView().findViewById(R.id.incid_reg_importancia_spinner);
        HELPER.doImportanciaSpinner(this);

        mButtonModify = (Button) getView().findViewById(R.id.incid_edit_fr_modif_button);
        mButtonModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "mButtonModify.onClick()");
                modifyIncidenciaAndImportancia();
            }
        });

        // Erase button doesn't appear when user hasn't adm authority or there exists a resolucion for the incidencia.
        mButtonErase = (Button) getView().findViewById(R.id.incid_edit_max_fr_borrar_button);
        if (!mIncidImportancia.getUserComu().hasAdministradorAuthority() || flagResolucion){
            mButtonErase.setVisibility(GONE);
            // Texto que acompaña al botón.
            getView().findViewById(R.id.incid_edit_fr_borrar_txt).setVisibility(GONE);
        }
        mButtonErase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "mButtonErase.onClick()");
                eraseIncidencia();
            }
        });

    }

    @Override
    public void onDestroy()
    {
        Log.d(TAG, "onDestroy()");
        dbHelper.close();
        super.onDestroy();
    }

//    ============================================================
//    ...................... HELPER METHODS ......................
//    ============================================================

    private void modifyIncidenciaAndImportancia()
    {
        Log.d(TAG, "modifyIncidenciaAndImportancia()");

        StringBuilder errorMsg = getErrorMsgBuilder(getActivity());
        final Incidencia incidencia = mIncidenciaBean.makeIncidenciaWithUserName(
                fFragmentView, errorMsg, getResources(), mIncidImportancia.getIncidencia().getUserName());
        IncidImportancia incidImportancia = null;
        try {
            incidImportancia = mIncidImportanciaBean.makeIncidImportancia(errorMsg, getResources(), incidencia);
        } catch (IllegalStateException e) {
            makeToast(getActivity(), errorMsg.toString(), Toast.LENGTH_SHORT);
        }
        if(incidImportancia != null && checkInternetConnected(getActivity())){
            new IncidenciaModifyer().execute(incidImportancia);
        }
    }

    private void eraseIncidencia()
    {
        Log.d(TAG, "eraseIncidencia()");

        if (checkInternetConnected(getActivity())){
            checkState(mIncidImportancia.getUserComu().hasAdministradorAuthority());
            new IncidenciaEraser().execute(mIncidImportancia.getIncidencia());
        }
    }

//    ============================================================
//    ..................... INTERFACE METHODS ....................
//    ============================================================

    @Override
    public void onAmbitoIncidSpinnerLoaded()
    {
        Log.d(TAG, "onAmbitoIncidSpinnerLoaded()");
        mAmbitoIncidSpinner.setSelection(mIncidImportancia.getIncidencia().getAmbitoIncidencia().getAmbitoId());
    }

    @Override
    public void setAmbitoSpinnerAdapter(CursorAdapter cursorAdapter)
    {
        Log.d(TAG, "setAmbitoSpinnerAdapter()");
        mAmbitoIncidSpinner.setAdapter(cursorAdapter);
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
        return mAmbitoIncidSpinner;
    }

    @Override
    public IncidenciaBean getIncidenciaBean()
    {
        Log.d(TAG, "getIncidenciaBean()");
        return mIncidenciaBean;
    }

    @Override
    public IncidImportanciaBean getIncidImportanciaBean()
    {
        Log.d(TAG, "getIncidImportanciaBean()");
        return mIncidImportanciaBean;
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
        Log.d(TAG, "onImportanciaSpinnerLoaded(), importancia= " + mIncidImportancia.getImportancia());
        mImportanciaSpinner.setSelection(mIncidImportancia.getImportancia());
    }

//    ============================================================
//    ..................... INNER CLASSES  .......................
//    ============================================================

    class IncidenciaModifyer extends AsyncTask<IncidImportancia, Void, Integer> {

        private final String TAG = IncidenciaModifyer.class.getCanonicalName();
        UiException uiException;

        @Override
        protected Integer doInBackground(IncidImportancia... incidImportancias)
        {
            Log.d(TAG, "doInBackground()");
            int rowInserted = 0;

            try {
                rowInserted = IncidenciaServ.modifyIncidImportancia(incidImportancias[0]);
            } catch (UiException e) {
                uiException = e;
            }
            return rowInserted;
        }

        @Override
        protected void onPostExecute(Integer rowInserted)
        {
            Log.d(TAG, "onPostExecute()");

            if (uiException != null) {
                uiException.processMe(getActivity(), new Intent());
            } else {
                checkState(rowInserted == 1);
                Intent intent = new Intent(getActivity(), IncidSeeByComuAc.class);
                startActivity(intent);
            }
        }
    }

    class IncidenciaEraser extends AsyncTask<Incidencia, Void, Integer> {

        private final String TAG = IncidenciaEraser.class.getCanonicalName();
        UiException uiException;

        @Override
        protected Integer doInBackground(Incidencia... params)
        {
            Log.d(TAG, "doInBackground()");
            int rowsDeleted = 0;
            try {
                rowsDeleted = IncidenciaServ.deleteIncidencia(params[0].getIncidenciaId());
            } catch (UiException e) {
                uiException = e;
            }
            return rowsDeleted;
        }

        @Override
        protected void onPostExecute(Integer rowsDeleted)
        {
            Log.d(TAG, "onPostExecute()");

            if (uiException != null) {
                uiException.processMe(getActivity(), new Intent());
            } else {
                checkState(rowsDeleted == 1);
                Intent intent = new Intent(getActivity(), IncidSeeByComuAc.class);
                startActivity(intent);
            }
        }
    }
}
