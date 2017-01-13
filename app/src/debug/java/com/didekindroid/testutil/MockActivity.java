package com.didekindroid.testutil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.didekindroid.usuario.RegGcmIntentService;

import timber.log.Timber;

import static com.didekindroid.security.TokenIdentityCacher.TKhandler;

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

    public void launchRegService(){
        if (!TKhandler.isGcmTokenSentServer()) {
            startService(new Intent(this, RegGcmIntentService.class));
        }
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
