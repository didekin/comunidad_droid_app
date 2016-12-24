package com.didekinaar.testutil;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import timber.log.Timber;

public class MockActivity extends AppCompatActivity {

    Class<? extends Activity> defaultActivityClassToGo;
    volatile short counter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);
        defaultActivityClassToGo = MockNextDefaultActivity.class;
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
    protected void onDestroy(){
        Timber.d("onDestroy()");
        super.onDestroy();
    }

    public short getCounter()
    {
        return counter;
    }

    public void setCounter(short counter)
    {
        this.counter = counter;
    }
}
