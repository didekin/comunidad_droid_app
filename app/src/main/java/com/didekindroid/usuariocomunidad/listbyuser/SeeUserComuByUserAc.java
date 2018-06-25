package com.didekindroid.usuariocomunidad.listbyuser;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.didekindroid.R;
import com.didekindroid.lib_one.security.AuthTkCacherIf;

import timber.log.Timber;

import static com.didekindroid.comunidad.util.ComuContextualName.to_reg_new_comu_usercomu;
import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.lib_one.util.CommonAssertionMsg.user_should_be_registered;
import static com.didekindroid.lib_one.util.UiUtil.assertTrue;
import static com.didekindroid.lib_one.util.UiUtil.doToolBar;


/**
 * Preconditions:
 * 1. The user is registered.
 * Postconditions:
 * 1. An object UsuarioComunidad is passed as an intent key with:
 * -- an object Comunidad fully initialized.
 * -- an object Usuario fully initialized.
 * -- the rest of data of an object UsuarioComunidad fully initialized.
 */
public class SeeUserComuByUserAc extends AppCompatActivity {

    SeeUserComuByUserFr mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.see_usercomu_by_user_ac);
        doToolBar(this, true);

        FloatingActionButton fab = findViewById(R.id.new_comunidad_fab);
        fab.setOnClickListener(
                v -> routerInitializer.get().getContextRouter()
                        .getActionFromContextNm(to_reg_new_comu_usercomu).initActivity(this));

        mFragment = (SeeUserComuByUserFr) getSupportFragmentManager().findFragmentById(R.id.see_usercomu_by_user_frg);
    }

    // ============================================================
    //    ..... ACTION BAR ....
    // ============================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Timber.d("onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.see_usercomu_by_user_ac_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");

        int resourceId = item.getItemId();
        switch (resourceId) {
            case android.R.id.home:
            case R.id.comu_search_ac_mn:
                routerInitializer.get().getMnRouter().getActionFromMnItemId(resourceId).initActivity(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
