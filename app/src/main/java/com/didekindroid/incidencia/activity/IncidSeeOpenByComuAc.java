package com.didekindroid.incidencia.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.didekin.incidservice.dominio.IncidAndResolBundle;
import com.didekin.incidservice.dominio.Incidencia;
import com.didekin.incidservice.dominio.IncidenciaUser;
import com.didekin.usuario.dominio.Comunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;

import java.util.List;
import java.util.Objects;

import timber.log.Timber;

import static com.didekindroid.common.activity.BundleKey.COMUNIDAD_ID;
import static com.didekindroid.common.activity.BundleKey.INCIDENCIA_LIST_INDEX;
import static com.didekindroid.common.activity.BundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.common.activity.BundleKey.INCID_RESOLUCION_FLAG;
import static com.didekindroid.common.utils.UIutils.doToolBar;
import static com.didekindroid.common.utils.UIutils.getGcmToken;
import static com.didekindroid.incidencia.activity.utils.IncidFragmentTags.incid_see_by_comu_list_fr_tag;
import static com.didekindroid.incidencia.activity.utils.IncidenciaMenu.INCID_CLOSED_BY_COMU_AC;
import static com.didekindroid.incidencia.activity.utils.IncidenciaMenu.INCID_REG_AC;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.didekindroid.usuario.activity.utils.UserMenu.SEE_USERCOMU_BY_COMU_AC;
import static com.didekindroid.usuario.activity.utils.UserMenu.doUpMenu;

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

    IncidSeeByComuListFr mFragment;
    int mIncidenciaIndex;
    Comunidad mComunidadSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate().");
        super.onCreate(savedInstanceState);

        getGcmToken(this);

        setContentView(R.layout.incid_see_open_by_comu_ac);
        doToolBar(this, true);

        if (savedInstanceState != null) {
            Objects.equals(getSupportFragmentManager().findFragmentByTag(incid_see_by_comu_list_fr_tag) != null, true);
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
        Timber.d("onResume()");
        getGcmToken(this);
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState)
    {
        Timber.d("onSaveInstanceState()");
        savedInstanceState.putInt(INCIDENCIA_LIST_INDEX.key, mIncidenciaIndex);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Timber.d("onRestoreInstanceState()");
        if (savedInstanceState != null) {
            mIncidenciaIndex = savedInstanceState.getInt(INCIDENCIA_LIST_INDEX.key, 0);
            mFragment.getListView().setSelection(mIncidenciaIndex); // Only for linearFragments.
        }
    }

    // ============================================================
    //    ..... ACTION BAR ....
    // ============================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Timber.d("onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.incid_see_open_by_comu_ac_mn, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");

        int resourceId = item.getItemId();

        switch (resourceId) {
            case android.R.id.home:
                doUpMenu(this);
                return true;
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
        Timber.d("onIncidenciaSelected()");
        mIncidenciaIndex = position;
        new IncidImportanciaGetter().execute(incidencia.getIncidenciaId());
    }

    @Override
    public void onComunidadSpinnerSelected(Comunidad comunidadSelected)
    {
        Timber.d("onComunidadSpinnerSelected()");
        mComunidadSelected = comunidadSelected;
    }

    @Override
    public ArrayAdapter<IncidenciaUser> getAdapter(Activity activity)
    {
        Timber.d("getAdapter()");
        return new IncidSeeOpenByComuAdapter(this);
    }

    @Override
    public List<IncidenciaUser> getListFromService(long comunidadId) throws UiException
    {
        Timber.d("getListFromService()");
        return IncidenciaServ.seeIncidsOpenByComu(comunidadId);
    }

    /**
     * This method returns a long > 0 if there is a comunidadId in an intent extra.
     */
    @Override
    public long getComunidadSelected()
    {
        Timber.d("getComunidadSelected()");
        return getIntent().getLongExtra(COMUNIDAD_ID.key, 0);
    }

//    ============================================================
//    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
//    ============================================================

    class IncidImportanciaGetter extends AsyncTask<Long, Void, IncidAndResolBundle> {

        UiException uiException;

        @Override
        protected IncidAndResolBundle doInBackground(final Long... incidenciaId)
        {
            Timber.d("doInBackground()");
            IncidAndResolBundle incidAndResolBundle = null;
            try {
                incidAndResolBundle = IncidenciaServ.seeIncidImportancia(incidenciaId[0]);
            } catch (UiException e) {
                uiException = e;
            }
            return incidAndResolBundle;
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        protected void onPostExecute(IncidAndResolBundle incidAndResolBundle)
        {
            Timber.d("onPostExecute()");

            if (uiException != null) {
                uiException.processMe(IncidSeeOpenByComuAc.this, new Intent());
            } else {
                Objects.equals(incidAndResolBundle != null, true);
                Intent intent = new Intent(IncidSeeOpenByComuAc.this, IncidEditAc.class);
                intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, incidAndResolBundle.getIncidImportancia());
                intent.putExtra(INCID_RESOLUCION_FLAG.key, incidAndResolBundle.hasResolucion());
                startActivity(intent);
            }
        }
    }
}
