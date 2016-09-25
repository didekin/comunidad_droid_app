package com.didekindroid.incidencia.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.didekin.incidservice.dominio.Incidencia;
import com.didekin.incidservice.dominio.IncidenciaUser;
import com.didekin.incidservice.dominio.Resolucion;
import com.didekin.usuario.dominio.Comunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;

import java.util.List;
import java.util.Objects;

import timber.log.Timber;

import static com.didekindroid.common.activity.BundleKey.COMUNIDAD_ID;
import static com.didekindroid.common.activity.BundleKey.INCIDENCIA_LIST_INDEX;
import static com.didekindroid.common.activity.BundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.common.activity.BundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.common.activity.BundleKey.IS_MENU_IN_FRAGMENT_FLAG;
import static com.didekindroid.common.utils.UIutils.doToolBar;
import static com.didekindroid.incidencia.activity.utils.IncidFragmentTags.incid_resolucion_see_fr_tag;
import static com.didekindroid.incidencia.activity.utils.IncidFragmentTags.incid_see_by_comu_list_fr_tag;
import static com.didekindroid.incidencia.activity.utils.IncidenciaMenu.INCID_REG_AC;
import static com.didekindroid.incidencia.activity.utils.IncidenciaMenu.INCID_SEE_BY_COMU_AC;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;

/**
 * Preconditions:
 * 1. The user is NOW registered in the comunidad whose incidencias are shown.
 * 2. The incidencias shown have been registered in the last 24 months and are closed.
 * 3. All the incidencias closed in a comunidad where the user is NOW registered are shown,
 *    even is the user was not registered in the comunidad when incidencia was open or closed.
 * 4. All incidencias closed MUST HAVE a resolucion.
 * 5. An intent may be passed with a comunidadId, when a notification is sent when the
 *    incidencia has been closed.
 * Postconditions:
 * 1. A list of IncidenciaUSer instances are shown.
 * 2. The incidencias are shown in chronological order, from the most recent to the oldest one.
 * 3. If an incidencia is selected, the resolucion data are shown.
 * -- Arguments with incidImportancia, resolucion and a toShowMenu flag are passed to the resolucion
 * fragment.
 */
public class IncidSeeClosedByComuAc extends AppCompatActivity implements
        IncidSeeListListener {

    IncidSeeByComuListFr mFragment;
    int mIncidenciaIndex;
    Comunidad mComunidadSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.incid_see_closed_by_comu_ac);
        doToolBar(this, true);
        if (savedInstanceState != null) {
            Objects.equals(getSupportFragmentManager().findFragmentByTag(incid_see_by_comu_list_fr_tag) != null, true);
            return;
        }
        mFragment = new IncidSeeByComuListFr();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.incid_see_closed_by_comu_ac, mFragment, incid_see_by_comu_list_fr_tag)
                .commit();
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
            mFragment.getListView().setSelection(mIncidenciaIndex);
        }
    }

    // ============================================================
    //    ..... ACTION BAR ....
    // ============================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Timber.d("onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.incid_see_closed_by_comu_ac_mn, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");

        int resourceId = item.getItemId();

        switch (resourceId) {
            case R.id.incid_see_open_by_comu_ac_mn:
                INCID_SEE_BY_COMU_AC.doMenuItem(this);
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
        Timber.d("onIncidenciaSelected()");
        mIncidenciaIndex = position;
        new ResolucionGetter().execute(incidencia);
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
        return new IncidSeeClosedByComuAdapter(this);
    }

    @Override
    public List<IncidenciaUser> getListFromService(long comunidadId) throws UiException
    {
        return IncidenciaServ.seeIncidsClosedByComu(comunidadId);
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

    private class ResolucionGetter extends AsyncTask<Incidencia, Void, Resolucion> {

        UiException uiException;
        Incidencia incidencia;

        ResolucionGetter()
        {
        }

        @Override
        protected Resolucion doInBackground(final Incidencia... incidencias)
        {
            Timber.d("doInBackground()");
            incidencia = incidencias[0];
            Resolucion resolucion = null;
            try {
                resolucion = IncidenciaServ.seeResolucion(incidencia.getIncidenciaId());
            } catch (UiException e) {
                uiException = e;
            }
            return resolucion;
        }

        @Override
        protected void onPostExecute(Resolucion resolucion)
        {
            Timber.d("onPostExecute()");

            if (uiException != null) {
                uiException.processMe(IncidSeeClosedByComuAc.this, new Intent());
            } else {
                // Switch fragment here.
                Objects.equals(resolucion != null, true);
                Bundle bundle = new Bundle();
                bundle.putBoolean(IS_MENU_IN_FRAGMENT_FLAG.key, true);
                bundle.putSerializable(INCIDENCIA_OBJECT.key, incidencia);
                bundle.putSerializable(INCID_RESOLUCION_OBJECT.key, resolucion);
                Fragment switchFragment = new IncidResolucionSeeFr();
                switchFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.incid_see_closed_by_comu_ac, switchFragment, incid_resolucion_see_fr_tag)
                        .addToBackStack(switchFragment.getClass().getName())
                        .commit();
            }
        }
    }
}
