package com.didekindroid.incidencia.list.open;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.didekindroid.R;
import com.didekindroid.router.ActivityInitiatorIf;
import com.didekindroid.router.FragmentInitiatorIf;

import timber.log.Timber;

import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.list.open.IncidSeeOpenByComuFr.newInstance;
import static com.didekindroid.router.ActivityRouter.doUpMenu;
import static com.didekindroid.util.UIutils.doToolBar;

/**
 * This activity is a point of registration for receiving GCM notifications of new incidents.
 * <p/>
 * Preconditions:
 * 1. The user is registered.
 * 2. The user is registered NOW in the comunidad whose open incidencias are shown.
 * 3. An intent may be passed with a comunidadId, when a notification is sent when the
 * incidencia has been opened, or when the previous activity has a comuSpinner instance.
 * Postconditions:
 * 1. A list of IncidenciaUSer instances are shown.
 * 2. An intent is passed with an IncidResolucionBundle instance, where the selected incidencia is embedded.
 */
public class IncidSeeOpenByComuAc extends AppCompatActivity implements ActivityInitiatorIf,
        FragmentInitiatorIf<IncidSeeOpenByComuFr> {

    IncidSeeOpenByComuFr fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate().");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incid_see_open_by_comu_ac);
        doToolBar(this, true);

        if (savedInstanceState == null) {
            initFragmentTx(newInstance(getIntent().getLongExtra(COMUNIDAD_ID.key, 0)));
        }
        fragment = (IncidSeeOpenByComuFr) getSupportFragmentManager().findFragmentByTag(IncidSeeOpenByComuFr.class.getName());
    }

// ======================  ActivityInitiatorIf  ===================

    @Override
    public AppCompatActivity getActivity()
    {
        return this;
    }

// ======================  FragmentInitiatorIf  ===================

    @Override
    public int getContainerId()
    {
        return R.id.incid_see_open_by_comu_ac;
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
            case R.id.incid_reg_ac_mn:
                // comunidadId is passed.
                initAcFromMenu(getIntent().getExtras(), resourceId);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
