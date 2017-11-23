package com.didekindroid.usuariocomunidad.listbycomu;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.didekindroid.R;
import com.didekindroid.router.ActivityInitiatorIf;

import timber.log.Timber;

import static com.didekindroid.router.ActivityRouter.doUpMenu;
import static com.didekindroid.util.UIutils.doToolBar;

/**
 * User: pedro@didekin
 * Date: 25/08/15
 * Time: 16:30
 * <p>
 * Preconditions:
 * 1. a long comunidadId is passed as an intent key.
 */
public class SeeUserComuByComuAc extends AppCompatActivity implements ActivityInitiatorIf {

    SeeUserComuByComuFr fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.i("onCreate()");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.see_usercomu_by_comu_ac);
        doToolBar(this, true);

        fragment = (SeeUserComuByComuFr) getSupportFragmentManager().findFragmentById(R.id.see_usercomu_by_comu_frg);
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
        getMenuInflater().inflate(R.menu.see_usercomu_by_comu_ac_menu, menu);
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
            case R.id.see_usercomu_by_user_ac_mn:
            case R.id.user_data_ac_mn:
            case R.id.comu_search_ac_mn:
                initAcFromMenu(resourceId);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
