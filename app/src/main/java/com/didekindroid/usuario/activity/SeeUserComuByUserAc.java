package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;

import timber.log.Timber;

import static com.didekindroid.common.activity.BundleKey.USERCOMU_LIST_OBJECT;
import static com.didekindroid.common.utils.UIutils.doToolBar;
import static com.didekindroid.common.utils.UIutils.isRegisteredUser;
import static com.didekindroid.usuario.activity.utils.UserMenu.COMU_SEARCH_AC;
import static com.didekindroid.usuario.activity.utils.UserMenu.USER_DATA_AC;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

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
        checkState(isRegisteredUser(this));

        setContentView(R.layout.see_usercomu_by_user_ac);
        doToolBar(this, true);
        mFragment = (SeeUserComuByUserFr) getSupportFragmentManager().findFragmentById(R.id.see_usercomu_by_user_frg);
    }

    @Override
    protected void onRestart()
    {
        Timber.d("onRestart()");
        super.onRestart();
    }

    @Override
    protected void onStart()
    {
        Timber.d("onStart()");
        super.onStart();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Timber.d("onRestoreInstanceState()");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume()
    {
        Timber.d("onResume()");
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        Timber.d("onPause()");
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        Timber.d("onSaveInstanceState()");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop()
    {
        Timber.d("onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        Timber.d("onDestroy()");
        super.onDestroy();
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

        int resourceId = checkNotNull(item.getItemId());

        switch (resourceId) {
            case R.id.user_data_ac_mn:
                USER_DATA_AC.doMenuItem(this);
                return true;
            case R.id.comu_search_ac_mn:
                COMU_SEARCH_AC.doMenuItem(this);
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
