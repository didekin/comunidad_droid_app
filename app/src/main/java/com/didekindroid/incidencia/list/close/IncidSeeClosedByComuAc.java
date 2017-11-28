package com.didekindroid.incidencia.list.close;

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
import static com.didekindroid.incidencia.utils.IncidFragmentTags.incid_see_close_by_comu_list_fr_tag;
import static com.didekindroid.util.UIutils.doToolBar;

/**
 * Preconditions:
 * 1. The user is NOW registered in the comunidad whose incidencias are shown.
 * 2. The incidencias shown have been registered in the last 24 months and are closed.
 * 3. All the incidencias closed in a comunidad where the user is NOW registered are shown,
 * even is the user was not registered in the comunidad when incidencia was open or closed.
 * 4. All incidencias closed MUST HAVE a bundleWithResolucion.
 * 5. An intent may be passed with a comunidadId, when a notification is sent when the
 * incidencia has been closed or from a comuSpinner instance in a previous activity or fragment.
 * Postconditions:
 * 1. A list of IncidenciaUSer instances are shown.
 * 2. The incidencias are shown in chronological order, from the most recent to the oldest one.
 * 3. If an incidencia is selected, the bundleWithResolucion data are shown.
 * -- Arguments with incidImportancia, bundleWithResolucion and a toShowMenu flag are passed to the bundleWithResolucion
 * fragment.
 */
public class IncidSeeClosedByComuAc extends AppCompatActivity implements ActivityInitiatorIf {

    IncidSeeCloseByComuFr fragmentList;
    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incid_see_closed_by_comu_ac);
        doToolBar(this, true);

        // ComunidadId in intent.
        long comunidadId = getIntent().getLongExtra(COMUNIDAD_ID.key, 0);
        bundle = new Bundle();
        bundle.putLong(COMUNIDAD_ID.key, comunidadId);

        if (savedInstanceState != null) {
            fragmentList = (IncidSeeCloseByComuFr) getSupportFragmentManager().findFragmentByTag(incid_see_close_by_comu_list_fr_tag);
            return;
        }

        fragmentList = new IncidSeeCloseByComuFr();
        fragmentList.setArguments(bundle);
        new FragmentInitiator(this, R.id.incid_see_closed_by_comu_ac).initFragmentTx(fragmentList, incid_see_close_by_comu_list_fr_tag);
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
            case R.id.incid_reg_ac_mn:
                initAcFromMenu(bundle, resourceId);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
