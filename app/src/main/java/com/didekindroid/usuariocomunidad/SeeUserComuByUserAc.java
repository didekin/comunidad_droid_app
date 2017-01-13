package com.didekindroid.usuariocomunidad;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.didekin.usuariocomunidad.UsuarioComunidad;
import com.didekindroid.R;

import java.util.Objects;

import timber.log.Timber;

import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.util.ItemMenu.mn_handler;
import static com.didekindroid.util.UIutils.doToolBar;
import static com.didekindroid.usuariocomunidad.UserComuBundleKey.USERCOMU_LIST_OBJECT;
import static com.didekindroid.util.MenuRouter.doUpMenu;
import static com.didekindroid.util.MenuRouter.routerMap;

/**
 * Preconditions:
 * 1. The user is registered.
 * Postconditions:
 * 1. An object UsuarioComunidad is passed as an intent key with:
 * -- an object Comunidad fully initialized.
 * -- an object Usuario fully initialized.
 * -- the rest of data of an object UsuarioComunidad fully initialized.
 */
public class SeeUserComuByUserAc extends AppCompatActivity implements
        SeeUserComuByUserFr.SeeUserComuByUserFrListener {

    SeeUserComuByUserFr mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);

        // Preconditions: the user is registered.
        Objects.equals(TKhandler.isRegisteredUser(), true);

        setContentView(R.layout.see_usercomu_by_user_ac);
        doToolBar(this, true);
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
                doUpMenu(this);
                return true;
            case R.id.user_data_ac_mn:
            case R.id.comu_search_ac_mn:
                mn_handler.doMenuItem(this, routerMap.get(resourceId));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    ============================================================
//    .......... LISTENER IMPLEMENTATION AND AUXILIARY METHODS .......
//    ============================================================

    @Override
    public void onUserComuSelected(UsuarioComunidad userComu, int position)
    {
        Timber.d("onUserComuSelected()");
        Intent intent = new Intent(this, UserComuDataAc.class);
        intent.putExtra(USERCOMU_LIST_OBJECT.key, userComu);
        startActivity(intent);
    }
}
