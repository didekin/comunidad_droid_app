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

import com.didekin.incidservice.domain.IncidenciaUser;
import com.didekindroid.R;
import com.didekindroid.common.UiException;
import com.didekindroid.common.utils.ConnectionUtils;
import com.didekindroid.common.utils.UIutils;
import com.didekindroid.incidencia.dominio.IncidenciaBean;
import com.didekindroid.incidencia.repository.IncidenciaDataDbHelper;

import static com.didekindroid.common.utils.UIutils.getErrorMsgBuilder;
import static com.didekindroid.common.utils.UIutils.makeToast;
import static com.didekindroid.incidencia.activity.utils.IncidRegEditHelper.HELPER;
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
    IncidenciaUser fIncidenciaUser;
    Spinner mAmbitoIncidSpinner;
    Spinner mImportanciaSpinner;
    IncidenciaDataDbHelper dbHelper;
    IncidenciaBean mIncidenciaBean;
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

        mIncidenciaBean = new IncidenciaBean();
        dbHelper = new IncidenciaDataDbHelper(getActivity());
        fIncidenciaUser = ((IncidUserDataSupplier) getActivity()).getIncidenciaUser();

        mIncidenciaBean.setComunidadId(fIncidenciaUser.getIncidencia().getComunidad().getC_Id());
        ((TextView) fFragmentView.findViewById(R.id.incid_comunidad_txt)).setText(fIncidenciaUser.getIncidencia().getComunidad().getNombreComunidad());
        ((EditText) fFragmentView.findViewById(R.id.incid_reg_desc_ed)).setText(fIncidenciaUser.getIncidencia().getDescripcion());

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
                modifyIncidenciaUser();
            }
        });

        mButtonErase = (Button) getView().findViewById(R.id.incid_edit_max_fr_borrar_button);
        mButtonErase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "mButtonErase.onClick()");
                eraseIncidenciaUser();
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

    private void modifyIncidenciaUser()
    {
        Log.d(TAG, "modifyIncidenciaUser()");

        StringBuilder errorMsg = getErrorMsgBuilder(getActivity());
        final IncidenciaUser incidenciaUser = mIncidenciaBean.makeIncidenciaUser(fFragmentView, errorMsg);

        if (incidenciaUser == null) {
            Log.d(TAG, "modifyIncidenciaUser(), incidenciaUser == null");
            makeToast(getActivity(), errorMsg.toString(), Toast.LENGTH_SHORT);
        } else if (!ConnectionUtils.isInternetConnected(getActivity())) {
            UIutils.makeToast(getActivity(), R.string.no_internet_conn_toast, Toast.LENGTH_SHORT);
        } else {
            new IncidenciaModifyer().execute(incidenciaUser);
            Intent intent = new Intent(getActivity(), IncidSeeByComuAc.class);
            startActivity(intent);
        }
    }

    private void eraseIncidenciaUser()
    {
        Log.d(TAG, "eraseIncidenciaUser()");
        if (!ConnectionUtils.isInternetConnected(getActivity())) {
            UIutils.makeToast(getActivity(), R.string.no_internet_conn_toast, Toast.LENGTH_SHORT);
        } else {
            new IncidenciaEraser().execute(fIncidenciaUser);
            Intent intent = new Intent(getActivity(), IncidSeeByComuAc.class);
            startActivity(intent);
        }

    }

//    ============================================================
//    ..................... INTERFACE METHODS ....................
//    ============================================================

    @Override
    public void onAmbitoIncidSpinnerLoaded()
    {
        Log.d(TAG, "onAmbitoIncidSpinnerLoaded()");
        mAmbitoIncidSpinner.setSelection(fIncidenciaUser.getIncidencia().getAmbitoIncidencia().getAmbitoId());
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
    public Spinner getImportanciaSpinner()
    {
        Log.d(TAG, "getImportanciaSpinner()");
        return mImportanciaSpinner;
    }

    @Override
    public void onImportanciaSpinnerLoaded()
    {
        Log.d(TAG, "onImportanciaSpinnerLoaded(), importancia= " + fIncidenciaUser.getImportancia());
        mImportanciaSpinner.setSelection(fIncidenciaUser.getImportancia());
    }

//    ============================================================
//    ..................... INNER CLASSES  .......................
//    ============================================================

    interface IncidUserDataSupplier {
        IncidenciaUser getIncidenciaUser();
    }

    class IncidenciaModifyer extends AsyncTask<IncidenciaUser, Void, Integer> {

        private final String TAG = IncidenciaModifyer.class.getCanonicalName();
        UiException uiException;

        @Override
        protected Integer doInBackground(IncidenciaUser... incidenciaUsers)
        {
            Log.d(TAG, "doInBackground()");
            int rowInserted = 0;

            try {
                rowInserted = IncidenciaServ.modifyIncidenciaUser(incidenciaUsers[0]);
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
                uiException.getAction().doAction(getActivity(), uiException.getResourceId());
            } else {
                checkState(rowInserted == 1);
            }
        }
    }

    class IncidenciaEraser extends AsyncTask<IncidenciaUser, Void, Integer> {

        private final String TAG = IncidenciaEraser.class.getCanonicalName();
        UiException uiException;

        @Override
        protected Integer doInBackground(IncidenciaUser... params)
        {
            Log.d(TAG, "doInBackground()");
            int rowsDeleted = 0;
            try {
                rowsDeleted = IncidenciaServ.deleteIncidencia(params[0].getIncidencia().getIncidenciaId());
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
                uiException.getAction().doAction(getActivity(), uiException.getResourceId());
            } else {
                checkState(rowsDeleted == 1);
            }
        }
    }
}
