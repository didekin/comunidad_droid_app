package com.didekindroid.incidencia.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.didekin.incidservice.domain.IncidenciaUser;
import com.didekindroid.R;
import com.didekindroid.common.UiException;

import static com.didekindroid.common.utils.AppKeysForBundle.INCIDENCIA_LIST_ID;
import static com.didekindroid.common.utils.UIutils.doToolBar;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.google.common.base.Preconditions.checkState;

/**
 * Preconditions:
 * 1. An intent extra is passed with the incidenciaId of the incidencia to be edited.
 * 3. Edition capabilities are dependent on:
 *      -- the functional role of the user.
 *      -- the ownership of the incident (authorship) by the user.
 * Postconditions:
 * 1. An incidencia is updated in BD, once edited.
 */
public class IncidEditAc extends AppCompatActivity {

    private static final String TAG = IncidEditAc.class.getCanonicalName();
    View mAcView;
    private IncidenciaUser mIncidenciaUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        final long incidenciaId = getIntent().getLongExtra(INCIDENCIA_LIST_ID.extra, 0);
        new IncidUserGetter().execute(incidenciaId);

        mAcView = getLayoutInflater().inflate(R.layout.incid_reg_ac, null);
        setContentView(mAcView);
        doToolBar(this, true);
    }

//    ============================================================
//    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
//    ============================================================

    private class IncidUserGetter extends AsyncTask<Long, Void, IncidenciaUser> {

        private final String TAG = IncidUserGetter.class.getCanonicalName();
        UiException uiException;

        @Override
        protected IncidenciaUser doInBackground(final Long... incidenciaId)
        {
            Log.d(TAG, "doInBackground()");
            IncidenciaUser incidenciaUser = null;
            try {
                incidenciaUser = IncidenciaServ.getIncidenciaUserWithPowers(incidenciaId[0]);
            } catch (UiException e) {
                uiException = e;
            }
            return incidenciaUser;
        }

        @Override
        protected void onPostExecute(IncidenciaUser incidenciaUser)
        {
            Log.d(TAG, "onPostExecute()");

            if (uiException != null) {
                uiException.getAction().doAction(IncidEditAc.this, uiException.getResourceId());
            } else {
                checkState(incidenciaUser != null);
                mIncidenciaUser = incidenciaUser;
            }
        }
    }
}
