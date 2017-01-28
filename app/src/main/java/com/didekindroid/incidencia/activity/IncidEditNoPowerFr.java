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

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.IncidenciaDataDbHelper;
import com.didekindroid.incidencia.activity.utils.ImportanciaSpinnerSettable;
import com.didekindroid.incidencia.dominio.IncidImportanciaBean;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;

import timber.log.Timber;

import static com.didekindroid.incidencia.IncidService.IncidenciaServ;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.activity.utils.IncidSpinnersHelper.HELPER;
import static com.didekindroid.incidencia.activity.utils.IncidenciaAssertionMsg.incid_importancia_should_be_initialized;
import static com.didekindroid.incidencia.activity.utils.IncidenciaAssertionMsg.incid_importancia_should_be_modified;
import static com.didekindroid.util.ConnectionUtils.checkInternetConnected;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekindroid.util.UIutils.checkPostExecute;
import static com.didekindroid.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.util.UIutils.makeToast;

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
    IncidenciaDataDbHelper dbHelper;
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
        dbHelper = new IncidenciaDataDbHelper(getActivity());

        ((TextView) fFragmentView.findViewById(R.id.incid_comunidad_txt))
                .setText(mIncidImportancia.getIncidencia().getComunidad().getNombreComunidad());
        ((TextView) fFragmentView.findViewById(R.id.incid_reg_desc_txt))
                .setText(mIncidImportancia.getIncidencia().getDescripcion());
        ((TextView) fFragmentView.findViewById(R.id.incid_ambito_view))
                .setText(dbHelper.getAmbitoDescByPk(mIncidImportancia.getIncidencia().getAmbitoIncidencia().getAmbitoId()));

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
        dbHelper.close();
        super.onDestroy();
    }

//    ============================================================
//    ...................... HELPER METHODS ......................
//    ============================================================

    void modifyIncidImportancia()
    {
        Timber.d("modifyIncidImportancia()");

        StringBuilder errorMsg = getErrorMsgBuilder(getActivity());
        assertTrue(mIncidImportancia != null, incid_importancia_should_be_initialized);
        try {
            IncidImportancia incidImportancia = mIncidImportanciaBean.makeIncidImportancia(
                    errorMsg, getResources(), fFragmentView, mIncidImportancia);
            if (checkInternetConnected(getActivity())) {
                new ImportanciaModifyer().execute(incidImportancia);
            }
        } catch (IllegalStateException e) {
            Timber.e(e.getMessage());
            makeToast(getActivity(), errorMsg.toString(), R.color.deep_purple_100);
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
            if (checkPostExecute(getActivity())) return;

            Timber.d("onPostExecute()");

            if (uiException != null) {
                uiException.processMe(getActivity(), new Intent());
            } else {
                assertTrue(rowInserted == 1, incid_importancia_should_be_modified);
                Intent intent = new Intent(getActivity(), IncidSeeOpenByComuAc.class);
                startActivity(intent);
            }
        }
    }
}
