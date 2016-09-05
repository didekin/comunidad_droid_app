package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekin.incidservice.dominio.Incidencia;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.incidencia.activity.utils.ImportanciaSpinnerSettable;
import com.didekindroid.incidencia.dominio.IncidImportanciaBean;
import com.didekindroid.incidencia.repository.IncidenciaDataDbHelper;

import timber.log.Timber;

import static com.didekindroid.common.activity.BundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.common.utils.ConnectionUtils.checkInternetConnected;
import static com.didekindroid.common.utils.UIutils.getErrorMsgBuilder;
import static com.didekindroid.common.utils.UIutils.makeToast;
import static com.didekindroid.incidencia.activity.utils.IncidSpinnersHelper.HELPER;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * User: pedro@didekin
 * Date: 22/01/16
 * Time: 16:16
 */

@SuppressWarnings("ConstantConditions")
public class IncidEditNoPowerFr extends Fragment implements ImportanciaSpinnerSettable {

    View fFragmentView;
    IncidImportancia mIncidImportancia;
    Spinner mImportanciaSpinner;
    IncidImportanciaBean mIncidImportanciaBean;
    Button mButtonModify;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Timber.d("onCreateView()");
        fFragmentView = inflater.inflate(R.layout.incid_edit_nopower_fr, container, false);
        return fFragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Timber.d("onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

        mIncidImportanciaBean = new IncidImportanciaBean();
        mIncidImportancia = (IncidImportancia) getArguments().getSerializable(INCID_IMPORTANCIA_OBJECT.key);

        ((TextView) fFragmentView.findViewById(R.id.incid_comunidad_txt))
                .setText(mIncidImportancia.getIncidencia().getComunidad().getNombreComunidad());
        ((TextView) fFragmentView.findViewById(R.id.incid_reg_desc_txt))
                .setText(mIncidImportancia.getIncidencia().getDescripcion());
        ((TextView) fFragmentView.findViewById(R.id.incid_ambito_view))
                .setText(new IncidenciaDataDbHelper(getActivity()).getAmbitoDescByPk(mIncidImportancia.getIncidencia().getAmbitoIncidencia().getAmbitoId()));

        mImportanciaSpinner = (Spinner) getView().findViewById(R.id.incid_reg_importancia_spinner);
        HELPER.doImportanciaSpinner(this);
        HELPER.initUserComusImportanciaView(this);

        mButtonModify = (Button) getView().findViewById(R.id.incid_edit_fr_modif_button);
        mButtonModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("mButtonModify.onClick()");
                modifyIncidImportancia();
            }
        });
    }

    @Override
    public void onDestroy()
    {
        Timber.d("onDestroy()");
        super.onDestroy();
    }

//    ============================================================
//    ...................... HELPER METHODS ......................
//    ============================================================

    private void modifyIncidImportancia()
    {
        Timber.d("modifyIncidImportancia()");

        StringBuilder errorMsg = getErrorMsgBuilder(getActivity());
        try {
            IncidImportancia incidImportancia = mIncidImportanciaBean.makeIncidImportancia(
                    errorMsg, getResources(), fFragmentView, checkNotNull(mIncidImportancia));
            if (checkInternetConnected(getActivity())) {
                new ImportanciaModifyer().execute(incidImportancia);
            }
        } catch (IllegalStateException e) {
            Timber.e(e.getMessage());
            makeToast(getActivity(), errorMsg.toString(), Toast.LENGTH_SHORT);
        }
    }

//    ============================================================
//    ..................... INTERFACE METHODS ....................
//    ============================================================

    @Override
    public Incidencia getIncidencia()
    {
        Timber.d("getIncidencia()");
        return mIncidImportancia.getIncidencia();
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
        Timber.d("onImportanciaSpinnerLoaded(), importancia= %d%n", mIncidImportancia.getImportancia());
        mImportanciaSpinner.setSelection(mIncidImportancia.getImportancia());
    }

//    ============================================================
//    ..................... INNER CLASSES  .......................
//    ============================================================

    class ImportanciaModifyer extends AsyncTask<IncidImportancia, Void, Integer> {

        UiException uiException;

        @Override
        protected Integer doInBackground(IncidImportancia... incidImportancias)
        {
            Timber.d("doInBackground()");
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
            Timber.d("onPostExecute()");

            if (uiException != null) {
                uiException.processMe(getActivity(), new Intent());
            } else {
                checkState(rowInserted == 1);
                Intent intent = new Intent(getActivity(), IncidSeeOpenByComuAc.class);
                startActivity(intent);
            }
        }
    }
}
