package com.didekindroid.usuariocomunidad.listbycomu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.didekindroid.R;

import timber.log.Timber;

import static com.didekindroid.lib_one.util.UIutils.doToolBar;
import static com.didekindroid.router.MnRouter.resourceIdToMnItem;

/**
 * User: pedro@didekin
 * Date: 25/08/15
 * Time: 16:30
 * <p>
 * Preconditions:
 * 1. a long comunidadId is passed as an intent key.
 */
public class SeeUserComuByComuAc extends AppCompatActivity {

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
                resourceIdToMnItem.get(resourceId).initActivity(this);
                return true;
            case R.id.see_usercomu_by_user_ac_mn:
            case R.id.user_data_ac_mn:
            case R.id.comu_search_ac_mn:
                resourceIdToMnItem.get(resourceId).initActivity(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
