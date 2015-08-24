package com.didekindroid.usuario.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import com.didekindroid.R;

/**
 * User: pedro@didekin
 * Date: 11/05/15
 * Time: 19:13
 */
public class RegUserComuAc extends Activity {

    public static final String TAG = RegUserComuAc.class.getCanonicalName();
    RegUserComuFr regUserComuFr;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate()");
        setContentView(R.layout.reg_usercomu_ac);
        regUserComuFr = (RegUserComuFr) getFragmentManager().findFragmentById(R.id.reg_usercomu_fr);
    }

    @Override
    protected void onRestart()
    {
        Log.d(TAG,"onRestart()");
        super.onRestart();
    }

    @Override
    protected void onStart()
    {
        Log.d(TAG,"onStart()");
        super.onStart();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Log.d(TAG,"onRestoreInstanceState()");
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume()
    {
        Log.d(TAG,"onResume()");
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        Log.d(TAG,"onPause()");
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        Log.d(TAG,"onSaveInstanceState()");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop()
    {
        Log.d(TAG,"onStop()");
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        Log.d(TAG,"onDestroy()");
        super.onDestroy();
    }
}