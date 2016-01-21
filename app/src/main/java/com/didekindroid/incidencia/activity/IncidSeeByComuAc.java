package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.didekin.incidservice.domain.Incidencia;
import com.didekindroid.R;
import com.didekindroid.incidencia.gcm.GcmRegistrationIntentServ;

import static com.didekindroid.common.utils.AppKeysForBundle.INCIDENCIA_LIST_ID;
import static com.didekindroid.common.utils.AppKeysForBundle.INCIDENCIA_LIST_INDEX;
import static com.didekindroid.common.utils.UIutils.checkPlayServices;
import static com.didekindroid.common.utils.UIutils.doToolBar;
import static com.didekindroid.common.utils.UIutils.isGcmTokenSentServer;
import static com.didekindroid.incidencia.activity.utils.IncidenciaMenu.INCID_CLOSED_BY_COMU_AC;
import static com.didekindroid.incidencia.activity.utils.IncidenciaMenu.INCID_REG_AC;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Preconditions:
 * 1. The user is registered.
 * 2. The user is registered NOW in the comunidad whose open incidencias are shown.
 * Postconditions:
 * 1. The incidencia selected on the list is shown to the user in mode edition:
 *    -- If the user was the original author of the incidencia, it can modified its description.
 *    -- Every user can modify the importancia assigned by her to the incidencia.
 *    -- Only the original author can erase an incidencia, if the rest of the users have assigned an importancia
 *       equal o lower than 'low'.
 * 2. An intent is passed with the incidenciaId of the incidencia selected.
 */
public class IncidSeeByComuAc extends AppCompatActivity implements
        IncidListListener {

    private static final String TAG = IncidSeeByComuAc.class.getCanonicalName();

    IncidSeeByComuListFr mFragment;
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

        setContentView(R.layout.incid_see_by_comu_ac);
        doToolBar(this, true);
        mFragment = (IncidSeeByComuListFr) getFragmentManager()
                .findFragmentById(R.id.incid_see_by_comu_frg);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState)
    {
        Log.d(TAG, "onSaveInstanceState()");
        savedInstanceState.putInt(INCIDENCIA_LIST_INDEX.extra, mIncidenciaIndex);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Log.d(TAG, "onRestoreInstanceState()");
        if (savedInstanceState != null) {
            mIncidenciaIndex = savedInstanceState.getInt(INCIDENCIA_LIST_INDEX.extra, 0);
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
        getMenuInflater().inflate(R.menu.incid_see_by_comu_ac_mn, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.d(TAG, "onOptionsItemSelected()");

        int resourceId = checkNotNull(item.getItemId());

        switch (resourceId) {
            case R.id.incid_see_closed_by_comu_ac_mn:
                INCID_CLOSED_BY_COMU_AC.doMenuItem(this);
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
    public void onIncidenciaSelected(final Incidencia incidencia, int position)
    {
        Log.d(TAG, "onIncidenciaSelected()");
        mIncidenciaIndex = position;
        Intent intent = new Intent(IncidSeeByComuAc.this, IncidEditAc.class);
        intent.putExtra(INCIDENCIA_LIST_ID.extra, incidencia.getIncidenciaId());
        startActivity(intent);
    }
}
