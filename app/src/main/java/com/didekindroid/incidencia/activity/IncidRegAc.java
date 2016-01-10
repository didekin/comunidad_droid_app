package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.didekin.incidservice.domain.IncidUserComu;
import com.didekindroid.R;
import com.didekindroid.common.UiException;
import com.didekindroid.common.utils.ConnectionUtils;
import com.didekindroid.common.utils.UIutils;
import com.didekindroid.incidencia.gcm.GcmRegistrationIntentServ;

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

        if (checkPlayServices(this) && !isGcmTokenSentServer(this)){
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
        final IncidUserComu incidUserComu = mRegAcFragment.mIncidenciaBean.makeIncidUserComu(mRegAcFragment.mFragmentView, errorMsg);

        if (incidUserComu == null) {
            Log.d(TAG, "registerIncidencia(), incidUserComu == null");
            makeToast(this, errorMsg.toString(), Toast.LENGTH_SHORT);
        } else if (!ConnectionUtils.isInternetConnected(this)) {
            UIutils.makeToast(this, R.string.no_internet_conn_toast, Toast.LENGTH_LONG);
        } else {
            new IncidenciaRegister().execute(incidUserComu);
            Intent intent = new Intent(this, IncidSeeByUserAc.class);
            startActivity(intent);
        }
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    class IncidenciaRegister extends AsyncTask<IncidUserComu, Void, Integer> {

        private final String TAG = IncidenciaRegister.class.getCanonicalName();
        UiException uiException;

        @Override
        protected Integer doInBackground(IncidUserComu... incidUserComus)
        {
            Log.d(TAG, "doInBackground()");
            int rowInserted = 0;

            try {
                rowInserted = IncidenciaServ.regIncidenciaUserComu(incidUserComus[0]);
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
                uiException.getAction().doAction(IncidRegAc.this, uiException.getResourceId());
            } else {
                checkState(rowInserted == 1);
            }
        }
    }
}
