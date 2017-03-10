package com.didekindroid.api;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.didekindroid.R;

import timber.log.Timber;

import static com.didekindroid.util.UIutils.doToolBar;

public class ActivityNextMock extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);
        View mAcView = getLayoutInflater().inflate(R.layout.next_mock_ac, null);
        setContentView(mAcView);
        doToolBar(this, true);
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
}
