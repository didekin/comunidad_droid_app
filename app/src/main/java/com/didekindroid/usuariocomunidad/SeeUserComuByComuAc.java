package com.didekindroid.usuariocomunidad;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.didekindroid.R;

import java.util.Objects;

import timber.log.Timber;

import static com.didekinaar.security.TokenIdentityCacher.TKhandler;
import static com.didekinaar.utils.AarItemMenu.mn_handler;
import static com.didekinaar.utils.UIutils.doToolBar;
import static com.didekindroid.util.AppMenuRouter.doUpMenu;
import static com.didekindroid.util.AppMenuRouter.routerMap;

/**
 * User: pedro@didekin
 * Date: 25/08/15
 * Time: 16:30
 */
/**
 *  Preconditions:
 *  1. a long comunidadId is passed as an intent key.
 *  2. the user is registered.
 */
public class SeeUserComuByComuAc extends AppCompatActivity {

    SeeUserComuByComuFr mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Timber.i("onCreate()");

        // Preconditions: the user is registered.
        Objects.equals(TKhandler.isRegisteredUser(), true);

        setContentView(R.layout.see_usercomu_by_comu_ac);
        doToolBar(this, true);
        mFragment = (SeeUserComuByComuFr) getSupportFragmentManager().findFragmentById(R.id.see_usercomu_by_comu_frg);
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
        switch (resourceId){
            case android.R.id.home:
                doUpMenu(this);
                return true;
            case R.id.see_usercomu_by_user_ac_mn:
            case R.id.user_data_ac_mn:
            case R.id.comu_search_ac_mn:
                mn_handler.doMenuItem(this, routerMap.get(resourceId));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
