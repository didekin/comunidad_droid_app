package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekin.incidservice.dominio.Incidencia;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.common.utils.ConnectionUtils;
import com.didekindroid.incidencia.gcm.GcmRegistrationIntentServ;

import static com.didekindroid.common.utils.ConnectionUtils.checkInternetConnected;
import static com.didekindroid.common.utils.UIutils.checkPlayServices;
import static com.didekindroid.common.utils.UIutils.doToolBar;
import static com.didekindroid.common.utils.UIutils.getErrorMsgBuilder;
import static com.didekindroid.common.utils.UIutils.isGcmTokenSentServer;
import static com.didekindroid.common.utils.UIutils.makeToast;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.google.common.base.Preconditions.checkState;

/**
 * Preconditions:
 * 1. The user is registered.
 * 2. No intent received.
 * Postconditions:
 * 1. No intent passed.
 */

/**
 * This activity is a point of registration for receiving notifications of new incidencias.
 */
public class IncidRegAc extends AppCompatActivity {

    private static final String TAG = IncidRegAc.class.getCanonicalName();
    IncidRegAcFragment mRegAcFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        if (checkPlayServices(this) && !isGcmTokenSentServer(this)) {
            Intent intent = new Intent(this, GcmRegistrationIntentServ.class);
            startService(intent);
        }

        View mAcView = getLayoutInflater().inflate(R.layout.incid_reg_ac, null);
        setContentView(mAcView);
        doToolBar(this, true);

        mRegAcFragment = (IncidRegAcFragment) getFragmentManager().findFragmentById(R.id.incid_reg_frg);
        Button mRegisterButton = (Button) findViewById(R.id.incid_reg_ac_button);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "View.OnClickListener().onClick()");
                registerIncidencia();
            }
        });
    }

    private void registerIncidencia()
    {
        Log.d(TAG, "registerIncidencia()");

        StringBuilder errorMsg = getErrorMsgBuilder(this);
        final Incidencia incidencia = mRegAcFragment.mIncidenciaBean.makeIncidencia(mRegAcFragment.mFragmentView, errorMsg, getResources());
        IncidImportancia incidImportancia = null;

        try {
            incidImportancia = mRegAcFragment.mIncidImportanciaBean.makeIncidImportancia(errorMsg, getResources(), incidencia);
        } catch (IllegalStateException e) {
            Log.d(TAG, "registerIncidencia(), incidImportancia == null");
            makeToast(this, errorMsg.toString(), Toast.LENGTH_SHORT);
        }

        if(incidImportancia != null && checkInternetConnected(this)){
            new IncidenciaRegister().execute(incidImportancia);
        }
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    class IncidenciaRegister extends AsyncTask<IncidImportancia, Void, Integer> {

        private final String TAG = IncidenciaRegister.class.getCanonicalName();
        UiException uiException;

        @Override
        protected Integer doInBackground(IncidImportancia... incidImportancias)
        {
            Log.d(TAG, "doInBackground()");
            int rowInserted = 0;

            try {
                rowInserted = IncidenciaServ.regIncidImportancia(incidImportancias[0]);
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
                uiException.processMe(IncidRegAc.this, new Intent());
            } else {
                checkState(rowInserted == 2);
                Intent intent = new Intent(IncidRegAc.this, IncidSeeByComuAc.class);
                startActivity(intent);
            }
        }
    }
}
