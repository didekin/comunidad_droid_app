package com.didekindroid.usuario.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import com.didekindroid.R;

import static com.didekindroid.uiutils.UIutils.isRegisteredUser;
import static com.didekindroid.usuario.activity.utils.UserMenu.COMU_SEARCH_AC;
import static com.didekindroid.usuario.activity.utils.UserMenu.SEE_COMU_AND_USERCOMU_BY_USER_AC;
import static com.didekindroid.usuario.activity.utils.UserMenu.USER_DATA_AC;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * User: pedro@didekin
 * Date: 25/08/15
 * Time: 16:30
 */
/**
 *  Preconditions:
 *  1. a long comunidadId is passed as an intent extra.
 *  2. the user is registered.
 */
public class SeeUserComuByComuAc extends Activity {

    public static final String TAG = SeeUserComuByComuAc.class.getCanonicalName();
    SeeUserComuByComuFr mFragment;
    private Button mRegisterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate()");

        // Preconditions: the user is registeres; an existing comunidad passed as intent.
        checkState(isRegisteredUser(this));

        setContentView(R.layout.see_usercomu_by_comu_ac);
        mFragment = (SeeUserComuByComuFr) getFragmentManager().findFragmentById(R.id.see_usercomu_by_comu_frg);
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
        getMenuInflater().inflate(R.menu.see_usercomu_by_comu_ac_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.d(TAG, "onOptionsItemSelected()");

        int resourceId = checkNotNull(item.getItemId());

        switch (resourceId) {
            case R.id.comu_by_user_list_ac_mn:
                SEE_COMU_AND_USERCOMU_BY_USER_AC.doMenuItem(this);
                return true;
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
}
