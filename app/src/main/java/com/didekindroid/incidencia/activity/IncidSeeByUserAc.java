package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.didekin.incidservice.domain.IncidUserComu;
import com.didekindroid.R;
import com.didekindroid.common.UiException;
import com.didekindroid.incidencia.dominio.IncidUserComuIntent;
import com.didekindroid.incidencia.gcm.GcmRegistrationIntentServ;

import static com.didekindroid.common.utils.AppKeysForBundle.INCIDENCIA_LIST_INDEX;
import static com.didekindroid.common.utils.AppKeysForBundle.INCIDENCIA_ROL;
import static com.didekindroid.common.utils.AppKeysForBundle.INCID_USERCOMU_LIST_OBJECT;
import static com.didekindroid.common.utils.UIutils.checkPlayServices;
import static com.didekindroid.common.utils.UIutils.doToolBar;
import static com.didekindroid.common.utils.UIutils.isGcmTokenSentServer;
import static com.didekindroid.incidencia.activity.utils.IncidenciaMenu.INCID_CLOSED_BY_USER_AC;
import static com.didekindroid.incidencia.activity.utils.IncidenciaMenu.INCID_REG_AC;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Preconditions:
 * 1. The user is registered.
 * Postconditions:
 * 1. The detailed description of the incidencia selected on the list is shown to the user.
 */
public class IncidSeeByUserAc extends AppCompatActivity implements
        IncidSeeByUserListFr.IncidListListener {

    private static final String TAG = IncidSeeByUserAc.class.getCanonicalName();

    IncidSeeByUserListFr mFragment;
    int mIncidenciaIndex;

    /**
     * This activity is a point of registration for receiving GCM notifications of new incidents.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate().");
        super.onCreate(savedInstanceState);

        if (checkPlayServices(this) && !isGcmTokenSentServer(this)) {
            Intent intent = new Intent(this, GcmRegistrationIntentServ.class);
            startService(intent);
        }

        setContentView(R.layout.incid_see_by_user_ac);
        doToolBar(this, false);
        mFragment = (IncidSeeByUserListFr) getFragmentManager()
                .findFragmentById(R.id.incid_see_by_user_frg);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState)
    {
        Log.d(TAG, "onSaveInstanceState()");
        savedInstanceState.putInt(INCIDENCIA_LIST_INDEX.name(), mIncidenciaIndex);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Log.d(TAG, "onRestoreInstanceState()");
        if (savedInstanceState != null) {
            mIncidenciaIndex = savedInstanceState.getInt(INCIDENCIA_LIST_INDEX.name(), 0);
            mFragment.setSelection(mIncidenciaIndex); // Only for linearFragments.
        }
    }

    // ============================================================
    //    ..... ACTION BAR ....
    // ============================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Log.d(TAG, "onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.incid_see_by_user_mn, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.d(TAG, "onOptionsItemSelected()");

        int resourceId = checkNotNull(item.getItemId());

        switch (resourceId) {
            case R.id.incid_closed_see_by_useromu_ac_mn:
                INCID_CLOSED_BY_USER_AC.doMenuItem(this);
                return true;
            case R.id.incid_reg_ac_mn:
                INCID_REG_AC.doMenuItem(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //  ........... HELPER INTERFACES AND CLASSES ..................

    @Override
    public void onIncidenciaSelected(final IncidUserComu incidUserComu, int position)
    {
        Log.d(TAG, "onIncidenciaSelected()");
        mIncidenciaIndex = position;
        new FunctionalRoleGetter().execute(incidUserComu);
    }

//    ============================================================
//    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
//    ============================================================

    private class FunctionalRoleGetter extends AsyncTask<IncidUserComu, Void, String> {

        private final String TAG = FunctionalRoleGetter.class.getCanonicalName();
        private IncidUserComu incidUserComu;
        UiException uiException;

        @Override
        protected String doInBackground(final IncidUserComu... params)
        {
            Log.d(TAG, "doInBackground()");
            incidUserComu = params[0];
            String function = null;
            try {
                function = IncidenciaServ.getHighestRolFunction(
                        incidUserComu.getUsuarioComunidad().getComunidad().getC_Id());
            } catch (UiException e) {
                uiException = e;
            }
            return function;
        }

        @Override
        protected void onPostExecute(String functionalRole)
        {
            Log.d(TAG, "onPostExecute(): functionalRole = " + functionalRole);

            if (uiException != null) {
                uiException.getAction().doAction(IncidSeeByUserAc.this, uiException.getResourceId());
            } else {
                checkState(functionalRole != null);
                Intent intent = new Intent(IncidSeeByUserAc.this, IncidEditAc.class);
                intent.putExtra(INCIDENCIA_ROL.extra, functionalRole);
                intent.putExtra(INCID_USERCOMU_LIST_OBJECT.extra, new IncidUserComuIntent(incidUserComu));
                startActivity(intent);
            }
        }
    }
}
