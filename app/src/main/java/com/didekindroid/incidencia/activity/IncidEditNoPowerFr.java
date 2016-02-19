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

import com.didekin.incidservice.dominio.IncidenciaUser;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
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
public class IncidEditNoPowerFr extends Fragment implements ImportanciaSpinnerSettable {

    private static final String TAG = IncidEditNoPowerFr.class.getCanonicalName();

    View fFragmentView;
    IncidenciaUser fIncidenciaUser;
    Spinner mImportanciaSpinner;
    IncidenciaBean mIncidenciaBean;
    boolean mIsRegUserInIncidencia;
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

        mIncidenciaBean = new IncidenciaBean();
        fIncidenciaUser = ((IncidUserDataSupplier) getActivity()).getIncidenciaUser();
        mIsRegUserInIncidencia = fIncidenciaUser.getUsuarioComunidad() == null;

        ((TextView) fFragmentView.findViewById(R.id.incid_comunidad_txt)).setText(fIncidenciaUser.getIncidencia().getComunidad().getNombreComunidad());
        ((TextView) fFragmentView.findViewById(R.id.incid_reg_desc_txt)).setText(fIncidenciaUser.getIncidencia().getDescripcion());
        ((TextView) fFragmentView.findViewById(R.id.incid_ambito_view))
                .setText(new IncidenciaDataDbHelper(getActivity()).getAmbitoDescByPk(fIncidenciaUser.getIncidencia().getAmbitoIncidencia().getAmbitoId()));

        mImportanciaSpinner = (Spinner) getView().findViewById(R.id.incid_reg_importancia_spinner);
        HELPER.doImportanciaSpinner(this);

        mButtonModify = (Button) getView().findViewById(R.id.incid_edit_fr_modif_button);
        if (mIsRegUserInIncidencia){
            mButtonModify.setText(R.string.incid_regUserInIncid_button_rot);
        }
        mButtonModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "mButtonModify.onClick()");
                modifyIncidenciaUser();
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

    private void modifyIncidenciaUser()
    {
        Log.d(TAG, "modifyIncidenciaUser()");

        StringBuilder errorMsg = getErrorMsgBuilder(getActivity());

        if (!mIncidenciaBean.validateImportancia(errorMsg, getResources())){
            Log.d(TAG, "modifyIncidenciaUser(), incidenciaUser == null");
            makeToast(getActivity(), errorMsg.toString(), Toast.LENGTH_SHORT);
        } else if (!ConnectionUtils.isInternetConnected(getActivity())) {
            UIutils.makeToast(getActivity(), R.string.no_internet_conn_toast, Toast.LENGTH_SHORT);
        } else {
            final IncidenciaUser incidenciaUser = new IncidenciaUser.IncidenciaUserBuilder
                    (fIncidenciaUser.getIncidencia())
                    .importancia(mIncidenciaBean.getImportanciaIncid())
                    .build();
            new IncidenciaModifyer().execute(incidenciaUser);
        }
    }

//    ============================================================
//    ..................... INTERFACE METHODS ....................
//    ============================================================

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

    class IncidenciaModifyer extends AsyncTask<IncidenciaUser, Void, Integer> {

        private final String TAG = IncidenciaModifyer.class.getCanonicalName();
        UiException uiException;

        @Override
        protected Integer doInBackground(IncidenciaUser... incidenciaUsers)
        {
            Log.d(TAG, "doInBackground()");
            int rowInserted = 0;

            try {
                if (mIsRegUserInIncidencia){
                    rowInserted = IncidenciaServ.regUserInIncidencia(incidenciaUsers[0]);
                } else {
                    rowInserted = IncidenciaServ.modifyUser(incidenciaUsers[0]);
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
