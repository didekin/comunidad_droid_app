package com.didekindroid.incidencia.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.didekin.incidservice.dominio.IncidAndResolBundle;
import com.didekin.incidservice.dominio.Incidencia;
import com.didekin.incidservice.dominio.IncidenciaUser;
import com.didekin.usuario.dominio.Comunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.incidencia.gcm.GcmRegistrationIntentService;

import java.util.List;

import static com.didekindroid.common.activity.BundleKey.COMUNIDAD_ID;
import static com.didekindroid.common.activity.BundleKey.INCIDENCIA_LIST_INDEX;
import static com.didekindroid.common.activity.BundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.common.activity.BundleKey.INCID_RESOLUCION_FLAG;
import static com.didekindroid.common.activity.FragmentTags.incid_see_by_comu_list_fr_tag;
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
 * <p/>
 * Preconditions:
 * 1. The user is registered.
 * 2. The user is registered NOW in the comunidad whose open incidencias are shown.
 * 3. An intent may be passed with a comunidadId, when a notification is sent when the
 * incidencia has been opened.
 * Postconditions:
 * 1. An intent is passed with an IncidImportancia instance, where the selected incidencia is embedded.
 */
public class IncidSeeOpenByComuAc extends AppCompatActivity implements
        IncidSeeListListener {

    private static final String TAG = IncidSeeOpenByComuAc.class.getCanonicalName();

    IncidSeeByComuListFr mFragment;
    int mIncidenciaIndex;
    Comunidad mComunidadSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate().");
        super.onCreate(savedInstanceState);

        //noinspection StatementWithEmptyBody
        if (checkPlayServices(this)) {
            if (!isGcmTokenSentServer(this)){
                startService(new Intent(this, GcmRegistrationIntentService.class));
            }
        } else{
            // TODO: mostrar cuadro de diálogo para instalar GCM.
        }

        setContentView(R.layout.incid_see_open_by_comu_ac);
        doToolBar(this, true);

        if (savedInstanceState != null) {
            checkState(getSupportFragmentManager().findFragmentByTag(incid_see_by_comu_list_fr_tag) != null);
            return;
        }
        mFragment = new IncidSeeByComuListFr();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.incid_see_open_by_comu_ac, mFragment, incid_see_by_comu_list_fr_tag)
                .commit();
    }

    @Override
    protected void onResume()
    {
        Log.d(TAG, "onResume()");
        if (checkPlayServices(this) && !isGcmTokenSentServer(this)) {
            startService(new Intent(this, GcmRegistrationIntentService.class));
        }
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState)
    {
        Log.d(TAG, "onSaveInstanceState()");
        savedInstanceState.putInt(INCIDENCIA_LIST_INDEX.key, mIncidenciaIndex);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Log.d(TAG, "onRestoreInstanceState()");
        if (savedInstanceState != null) {
            mIncidenciaIndex = savedInstanceState.getInt(INCIDENCIA_LIST_INDEX.key, 0);
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
        getMenuInflater().inflate(R.menu.incid_see_open_by_comu_ac_mn, menu);
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
                intent.putExtra(COMUNIDAD_ID.key, mComunidadSelected.getC_Id());
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
        new IncidImportanciaGetter().execute(incidencia.getIncidenciaId());
    }

    @Override
    public void onComunidadSpinnerSelected(Comunidad comunidadSelected)
    {
        Log.d(TAG, "onComunidadSpinnerSelected()");
        mComunidadSelected = comunidadSelected;
    }

    @Override
    public ArrayAdapter<IncidenciaUser> getAdapter(Activity activity)
    {
        Log.d(TAG, "getAdapter()");
        return new IncidSeeOpenByComuAdapter(this);
    }

    @Override
    public List<IncidenciaUser> getListFromService(long comunidadId) throws UiException
    {
        Log.d(TAG, "getListFromService()");
        return IncidenciaServ.seeIncidsOpenByComu(comunidadId);
    }

    @Override
    public long getComunidadSelected()
    {
        Log.d(TAG,"getComunidadSelected()");
        return getIntent().getLongExtra(COMUNIDAD_ID.key, 0);
    }

//    ============================================================
//    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
//    ============================================================

    private class IncidImportanciaGetter extends AsyncTask<Long, Void, IncidAndResolBundle> {

        private final String TAG = IncidImportanciaGetter.class.getCanonicalName();
        UiException uiException;

        @Override
        protected IncidAndResolBundle doInBackground(final Long... incidenciaId)
        {
            Log.d(TAG, "doInBackground()");
            IncidAndResolBundle incidAndResolBundle = null;
            try {
                incidAndResolBundle = IncidenciaServ.seeIncidImportancia(incidenciaId[0]);
            } catch (UiException e) {
                uiException = e;
            }
            return incidAndResolBundle;
        }

        @Override
        protected void onPostExecute(IncidAndResolBundle incidAndResolBundle)
        {
            Log.d(TAG, "onPostExecute()");

            if (uiException != null) {
                uiException.processMe(IncidSeeOpenByComuAc.this, new Intent());
            } else {
                checkState(incidAndResolBundle != null);
                Intent intent = new Intent(IncidSeeOpenByComuAc.this, IncidEditAc.class);
                intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, incidAndResolBundle.getIncidImportancia());
                intent.putExtra(INCID_RESOLUCION_FLAG.key, incidAndResolBundle.hasResolucion());
                startActivity(intent);
            }
        }
    }
}
