package com.didekindroid.incidencia.list.open;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.didekindroid.R;
import com.didekindroid.api.RootViewReplacer;
import com.didekindroid.api.RootViewReplacerIf;

import timber.log.Timber;

import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.utils.IncidFragmentTags.incid_see_by_comu_list_fr_tag;
import static com.didekindroid.api.ItemMenu.mn_handler;
import static com.didekindroid.MenuRouter.doUpMenu;
import static com.didekindroid.MenuRouter.routerMap;
import static com.didekindroid.util.UIutils.doToolBar;

/**
 * This activity is a point of registration for receiving GCM notifications of new incidents.
 * <p/>
 * Preconditions:
 * 1. The user is registered.
 * 2. The user is registered NOW in the comunidad whose open incidencias are shown.
 * 3. An intent may be passed with a comunidadId, when a notification is sent when the
 * incidencia has been opened.
 * Postconditions:
 * 1. A list of IncidenciaUSer instances are shown.
 * 2. An intent is passed with an IncidImportancia instance, where the selected incidencia is embedded.
 */
public class IncidSeeOpenByComuAc extends AppCompatActivity implements RootViewReplacerIf {

    IncidSeeOpenByComuFr fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate().");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incid_see_open_by_comu_ac);
        doToolBar(this, true);

        if (savedInstanceState != null) {
            fragment = (IncidSeeOpenByComuFr) getSupportFragmentManager().findFragmentByTag(incid_see_by_comu_list_fr_tag);
            return;
        }
        fragment = new IncidSeeOpenByComuFr();
        Bundle argsFragment = new Bundle();
        // We create an argument for the fragment even if the intent extra doesn't exist in the activity.
        argsFragment.putLong(COMUNIDAD_ID.key, getIntent().getLongExtra(COMUNIDAD_ID.key, 0));
        fragment.setArguments(argsFragment);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.incid_see_open_by_comu_ac, fragment, incid_see_by_comu_list_fr_tag)
                .commit();
    }

    @Override
    public void replaceRootView(Bundle bundle)
    {
        Timber.d("replaceActionInView()");
        new RootViewReplacer(this).replaceRootView(bundle);
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
                mn_handler.doMenuItem(this, routerMap.get(resourceId));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
