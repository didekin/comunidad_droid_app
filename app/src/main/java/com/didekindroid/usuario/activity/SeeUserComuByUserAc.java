package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.didekin.serviceone.domain.UsuarioComunidad;
import com.didekindroid.R;

import static com.didekindroid.common.utils.AppKeysForBundle.USERCOMU_LIST_OBJECT;
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
 * 1. An object UsuarioComunidad is passed as an intent extra with:
 * -- an object Comunidad fully initialized.
 * -- an object Usuario fully initialized.
 * -- the rest of data of an object UsuarioComunidad fully initialized.
 */
public class SeeUserComuByUserAc extends AppCompatActivity implements
        SeeUserComuByUserFr.SeeUserComuByUserFrListener {

    public static final String TAG = SeeUserComuByUserAc.class.getCanonicalName();

    SeeUserComuByUserFr mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        // Preconditions: the user is registered.
        checkState(isRegisteredUser(this));

        setContentView(R.layout.see_usercomu_by_user_ac);
        doToolBar(this, true);
        mFragment = (SeeUserComuByUserFr) getFragmentManager().findFragmentById(R.id.see_usercomu_by_user_frg);
    }

    @Override
    protected void onRestart()
    {
        Log.d(TAG, "onRestart()");
        super.onRestart();
    }

    @Override
    protected void onStart()
    {
        Log.d(TAG, "onStart()");
        super.onStart();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Log.d(TAG, "onRestoreInstanceState()");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume()
    {
        Log.d(TAG, "onResume()");
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        Log.d(TAG, "onPause()");
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        Log.d(TAG, "onSaveInstanceState()");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop()
    {
        Log.d(TAG, "onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }

    // ============================================================
    //    ..... ACTION BAR ....
    // ============================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Log.d(TAG, "onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.see_usercomu_by_user_ac_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.d(TAG, "onOptionsItemSelected()");

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
        Log.d(TAG, "onUserComuSelected()");
        Intent intent = new Intent(this, UserComuDataAc.class);
        intent.putExtra(USERCOMU_LIST_OBJECT.extra, userComu);
        startActivity(intent);
    }
}
