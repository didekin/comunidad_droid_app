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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.common.utils.ConnectionUtils;
import com.didekindroid.incidencia.dominio.IncidImportanciaBean;
import com.didekindroid.incidencia.repository.IncidenciaDataDbHelper;

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
public class IncidEditNoPowerFr extends Fragment implements ImportanciaSpinnerSettable {

    private static final String TAG = IncidEditNoPowerFr.class.getCanonicalName();

    View fFragmentView;
    IncidImportancia mIncidImportancia;
    Spinner mImportanciaSpinner;
    IncidImportanciaBean mIncidImportanciaBean;
    Button mButtonModify;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView()");
        fFragmentView = inflater.inflate(R.layout.incid_edit_nopower_fr, container, false);
        return fFragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Log.d(TAG, "onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

        mIncidImportanciaBean = new IncidImportanciaBean();
        mIncidImportancia = ((IncidenciaDataSupplier) getActivity()).getIncidImportancia();

        ((TextView) fFragmentView.findViewById(R.id.incid_comunidad_txt))
                .setText(mIncidImportancia.getIncidencia().getComunidad().getNombreComunidad());
        ((TextView) fFragmentView.findViewById(R.id.incid_reg_desc_txt))
                .setText(mIncidImportancia.getIncidencia().getDescripcion());
        ((TextView) fFragmentView.findViewById(R.id.incid_ambito_view))
                .setText(new IncidenciaDataDbHelper(getActivity()).getAmbitoDescByPk(mIncidImportancia.getIncidencia().getAmbitoIncidencia().getAmbitoId()));

        mImportanciaSpinner = (Spinner) getView().findViewById(R.id.incid_reg_importancia_spinner);
        HELPER.doImportanciaSpinner(this);

        mButtonModify = (Button) getView().findViewById(R.id.incid_edit_fr_modif_button);
        mButtonModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "mButtonModify.onClick()");
                modifyIncidImportancia();
            }
        });
    }

    @Override
    public void onDestroy()
    {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }

//    ============================================================
//    ...................... HELPER METHODS ......................
//    ============================================================

    private void modifyIncidImportancia()
    {
        Log.d(TAG, "modifyIncidImportancia()");

        StringBuilder errorMsg = getErrorMsgBuilder(getActivity());
        IncidImportancia incidImportancia = null;
        try {
            incidImportancia = mIncidImportanciaBean.makeIncidImportancia(errorMsg, getResources(), mIncidImportancia.getIncidencia());
        } catch (IllegalStateException e) {
            Log.d(TAG, "modifyIncidImportancia(), incidImportancia == null");
            makeToast(getActivity(), errorMsg.toString(), Toast.LENGTH_SHORT);
        }
        if (!ConnectionUtils.isInternetConnected(getActivity())) {
            makeToast(getActivity(), R.string.no_internet_conn_toast, Toast.LENGTH_SHORT);
        } else {
            new ImportanciaModifyer().execute(incidImportancia);
        }
    }

//    ============================================================
//    ..................... INTERFACE METHODS ....................
//    ============================================================

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

    class ImportanciaModifyer extends AsyncTask<IncidImportancia, Void, Integer> {

        private final String TAG = ImportanciaModifyer.class.getCanonicalName();
        UiException uiException;

        @Override
        protected Integer doInBackground(IncidImportancia... incidImportancias)
        {
            Log.d(TAG, "doInBackground()");
            int rowInserted = 0;

            try {
                // importancia == 0, if there is not an IncidImportancia instance previously persisted.
                if (mIncidImportancia.getImportancia() == 0) {
                    rowInserted = IncidenciaServ.regIncidImportancia(incidImportancias[0]);
                } else {
                    rowInserted = IncidenciaServ.modifyIncidImportancia(incidImportancias[0]);
                }
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
}
