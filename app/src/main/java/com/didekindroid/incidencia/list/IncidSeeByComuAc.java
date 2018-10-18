package com.didekindroid.incidencia.list;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.router.FragmentInitiatorIf;
import com.didekindroid.lib_one.api.router.MnRouterIf;

import timber.log.Timber;

import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_CLOSED_LIST_FLAG;
import static com.didekindroid.incidencia.IncidenciaAssertionMsg.incid_listFlag_should_be_initialized;
import static com.didekindroid.incidencia.list.IncidSeeByComuFr.newInstance;
import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.util.UiUtil.assertTrue;
import static com.didekindroid.lib_one.util.UiUtil.doToolBar;

public class IncidSeeByComuAc extends AppCompatActivity implements FragmentInitiatorIf<IncidSeeByComuFr> {

    IncidSeeByComuFr fragment;
    long comunidadId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incid_see_by_comu_ac);
        doToolBar(this, true);

        assertTrue(getIntent().hasExtra(INCID_CLOSED_LIST_FLAG.key), incid_listFlag_should_be_initialized);
        boolean isClosedList = getIntent().getBooleanExtra(INCID_CLOSED_LIST_FLAG.key, false);

        if (isClosedList) {
            setTitle(R.string.incid_closed_by_user_ac_label);
        } else {
            setTitle(R.string.incid_see_by_user_ac_label);
        }

        comunidadId = getIntent().getLongExtra(COMUNIDAD_ID.key, 0);
        if (savedInstanceState != null) {
            fragment = (IncidSeeByComuFr) getSupportFragmentManager().findFragmentByTag(IncidSeeByComuFr.class.getName());
            return;
        }
        initFragmentTx(newInstance(comunidadId, isClosedList));
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
        return R.id.incid_see_by_comu_ac;
    }

    // ============================================================
    //    ..... ACTION BAR ....
    // ============================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Timber.d("onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.incid_see_by_comu_ac_mn, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        Timber.d("onPrepareOptionsMenu()");
        boolean isClosedIncidList = getIntent().getBooleanExtra(INCID_CLOSED_LIST_FLAG.key, false);
        menu.findItem(R.id.incid_see_open_by_comu_ac_mn).setEnabled(isClosedIncidList).setVisible(isClosedIncidList);
        menu.findItem(R.id.incid_see_closed_by_comu_ac_mn).setEnabled(!isClosedIncidList).setVisible(!isClosedIncidList);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");

        MnRouterIf router = routerInitializer.get().getMnRouter();
        int resourceId = item.getItemId();

        switch (resourceId) {
            case android.R.id.home:
                router.getActionFromMnItemId(resourceId).initActivity(this);
                return true;
            case R.id.incid_see_open_by_comu_ac_mn:
                initReplaceFragmentTx(newInstance(comunidadId, false));
                setTitle(R.string.incid_see_by_user_ac_label);
                getIntent().putExtra(INCID_CLOSED_LIST_FLAG.key, false);
                return true;
            case R.id.incid_see_closed_by_comu_ac_mn:
                initReplaceFragmentTx(newInstance(comunidadId, true));
                setTitle(R.string.incid_closed_by_user_ac_label);
                getIntent().putExtra(INCID_CLOSED_LIST_FLAG.key, true);
                return true;
            case R.id.incid_reg_ac_mn:
                router.getActionFromMnItemId(resourceId)
                        .initActivity(this, COMUNIDAD_ID.getBundleForKey(getIntent().getLongExtra(COMUNIDAD_ID.key, 0)));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
