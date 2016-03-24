package com.didekindroid.incidencia.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.didekin.incidservice.dominio.Incidencia;
import com.didekin.incidservice.dominio.IncidenciaUser;
import com.didekin.usuario.dominio.Comunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;

import java.util.List;

import static com.didekindroid.common.activity.IntentExtraKey.INCIDENCIA_LIST_INDEX;
import static com.didekindroid.common.utils.UIutils.doToolBar;
import static com.didekindroid.incidencia.activity.utils.IncidenciaMenu.INCID_REG_AC;
import static com.didekindroid.incidencia.activity.utils.IncidenciaMenu.INCID_SEE_BY_COMU_AC;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Preconditions:
 * 1. The user is NOW registered in the comunidad whose incidencias are shown.
 * 2. The incidencias shown have been registered in the last 24 months and are closed.
 * 3. All the incidencias closed in a comunidad where the user is NOW registered are shown,
 *    even is the user was not registered in the comunidad when incidencia was open or closed.
 * 4. The incidencias are shown in chronological order, from the most recent to the oldest one.
 * Postconditions:
 * 1. A list of IncidenciaUSer instances are shown.
 * 2. An intent is passed with an IncidImportancia instance, where the selected incidencia is embedded.
 */
public class IncidSeeClosedByComuAc extends AppCompatActivity implements
        IncidSeeListListener {

    private static final String TAG = IncidSeeClosedByComuAc.class.getCanonicalName();

    IncidSeeByComuListFr mFragment;
    int mIncidenciaIndex;
    Comunidad mComunidadSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.incid_see_closed_by_comu_ac);
        doToolBar(this, true);
        mFragment = (IncidSeeByComuListFr) getFragmentManager().findFragmentById(R.id.incid_see_by_comu_frg);
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
            mFragment.setSelection(mIncidenciaIndex);
        }
    }

    // ============================================================
    //    ..... ACTION BAR ....
    // ============================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Log.d(TAG, "onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.incid_see_closed_by_comu_ac_mn, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.d(TAG, "onOptionsItemSelected()");

        int resourceId = checkNotNull(item.getItemId());

        switch (resourceId) {
            case R.id.incid_see_by_comu_ac_mn:
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
        Log.d(TAG, "onIncidenciaSelected()");
        mIncidenciaIndex = position;
//        new FunctionalRoleGetter().execute(incidenciaUser);
    }

    @Override
    public void onComunidadSpinnerSelected(Comunidad comunidadSelected)
    {
        Log.d(TAG, "onComunidadSpinnerSelected()");
        // TODO: implementar el campo en la activity para esta comunidad y utilizarlo en el menú.

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

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

}
