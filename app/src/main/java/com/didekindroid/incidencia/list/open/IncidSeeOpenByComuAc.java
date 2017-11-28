package com.didekindroid.incidencia.list.open;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.didekindroid.R;
import com.didekindroid.router.ActivityInitiatorIf;
import com.didekindroid.router.FragmentInitiator;

import timber.log.Timber;

import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.list.open.IncidSeeOpenByComuFr.newInstance;
import static com.didekindroid.incidencia.utils.IncidFragmentTags.incid_see_open_by_comu_list_fr_tag;
import static com.didekindroid.router.ActivityRouter.doUpMenu;
import static com.didekindroid.util.UIutils.doToolBar;

/**
 * This activity is a point of registration for receiving GCM notifications of new incidents.
 * <p/>
 * Preconditions:
 * 1. The user is registered.
 * 2. The user is registered NOW in the comunidad whose open incidencias are shown.
 * 3. An intent may be passed with a comunidadId, when a notification is sent when the
 * incidencia has been opened or when the previous activity has a comuSpinner instance.
 * Postconditions:
 * 1. A list of IncidenciaUSer instances are shown.
 * 2. An intent is passed with an IncidImportancia instance, where the selected incidencia is embedded.
 */
public class IncidSeeOpenByComuAc extends AppCompatActivity implements ActivityInitiatorIf {

    IncidSeeOpenByComuFr fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate().");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incid_see_open_by_comu_ac);
        doToolBar(this, true);

        if (savedInstanceState != null) {
            fragment = (IncidSeeOpenByComuFr) getSupportFragmentManager().findFragmentByTag(incid_see_open_by_comu_list_fr_tag);
            return;
        }

        // ComunidadId in intent.
        fragment = newInstance(getIntent().getLongExtra(COMUNIDAD_ID.key, 0));
        new FragmentInitiator(this, R.id.incid_see_open_by_comu_ac).initFragmentTx(fragment, incid_see_open_by_comu_list_fr_tag);
    }

    // ==================================  ActivityInitiatorIf  =================================

    @Override
    public Activity getActivity()
    {
        return this;
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
                initAcFromMenu(getIntent().getExtras(), resourceId);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
