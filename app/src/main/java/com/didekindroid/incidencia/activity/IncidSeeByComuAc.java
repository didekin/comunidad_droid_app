package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekin.incidservice.dominio.Incidencia;
import com.didekin.usuario.dominio.Comunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.incidencia.gcm.GcmRegistrationIntentServ;

import static com.didekindroid.common.utils.AppKeysForBundle.COMUNIDAD_ID;
import static com.didekindroid.common.utils.AppKeysForBundle.INCIDENCIA_LIST_INDEX;
import static com.didekindroid.common.utils.AppKeysForBundle.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.common.utils.UIutils.checkPlayServices;
import static com.didekindroid.common.utils.UIutils.doToolBar;
import static com.didekindroid.common.utils.UIutils.isGcmTokenSentServer;
import static com.didekindroid.incidencia.activity.utils.IncidenciaMenu.INCID_CLOSED_BY_COMU_AC;
import static com.didekindroid.incidencia.activity.utils.IncidenciaMenu.INCID_REG_AC;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.didekindroid.usuario.activity.utils.UserMenu.SEE_USERCOMU_BY_COMU_AC;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * This activity is a point of registration for receiving GCM notifications of new incidents.
 *
 * Preconditions:
 * 1. The user is registered.
 * 2. The user is registered NOW in the comunidad whose open incidencias are shown.
 * Postconditions:
 * 1. An intent is passed with an IncidImportancia instance, where the selected incidencia is embedded.
 */
public class IncidSeeByComuAc extends AppCompatActivity implements
        IncidListListener {

    private static final String TAG = IncidSeeByComuAc.class.getCanonicalName();

    IncidSeeByComuListFr mFragment;
    int mIncidenciaIndex;
    Comunidad mComunidadSelected;

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
            case R.id.see_usercomu_by_comu_ac_mn:
                Intent intent = new Intent();
                intent.putExtra(COMUNIDAD_ID.extra, mComunidadSelected.getC_Id());
                this.setIntent(intent);
                SEE_USERCOMU_BY_COMU_AC.doMenuItem(this);
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
        new IncidUserGetter().execute(incidencia.getIncidenciaId());
    }

    @Override
    public void onComunidadSpinnerSelected(Comunidad comunidadSelected)
    {
        Log.d(TAG, "onComunidadSpinnerSelected()");
        mComunidadSelected = comunidadSelected;
    }

    //    ============================================================
//    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
//    ============================================================

    private class IncidUserGetter extends AsyncTask<Long, Void, IncidImportancia> {

        private final String TAG = IncidUserGetter.class.getCanonicalName();
        UiException uiException;

        @Override
        protected IncidImportancia doInBackground(final Long... incidenciaId)
        {
            Log.d(TAG, "doInBackground()");
            IncidImportancia incidImportancia = null;
            try {
                incidImportancia = IncidenciaServ.seeIncidImportancia(incidenciaId[0]);
            } catch (UiException e) {
                uiException = e;
            }
            return incidImportancia;
        }

        @Override
        protected void onPostExecute(IncidImportancia incidImportancia)
        {
            Log.d(TAG, "onPostExecute()");

            if (uiException != null) {
                uiException.processMe(IncidSeeByComuAc.this, new Intent());
            } else {
                checkState(incidImportancia != null);
                Intent intent = new Intent(IncidSeeByComuAc.this, IncidEditAc.class);
                intent.putExtra(INCID_IMPORTANCIA_OBJECT.extra, incidImportancia);
                startActivity(intent);
            }
        }
    }
}
