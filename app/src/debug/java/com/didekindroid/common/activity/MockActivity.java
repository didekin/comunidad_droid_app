package com.didekindroid.common.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MockActivity extends AppCompatActivity {

    private static final String TAG = MockActivity.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
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
    protected void onDestroy(){
        Log.d(TAG,"onDestroy()");
        super.onDestroy();
    }
}
