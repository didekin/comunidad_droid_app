package com.didekindroid.lib_one.api;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.didekindroid.R;

import timber.log.Timber;

import static com.didekindroid.lib_one.util.UIutils.doToolBar;

public class ActivityMock extends AppCompatActivity {

    Class<? extends Activity> defaultActivityClassToGo;
    View acView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);

        acView = getLayoutInflater().inflate(R.layout.mock_ac, null);
        setContentView(acView);
        doToolBar(this, true);

        defaultActivityClassToGo = ActivityNextMock.class;
    }
}
