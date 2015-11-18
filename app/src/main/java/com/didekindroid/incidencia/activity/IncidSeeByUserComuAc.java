package com.didekindroid.incidencia.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.didekindroid.R;
import com.didekindroid.common.utils.UIutils;

import static com.didekindroid.common.utils.UIutils.doToolBar;

public class IncidSeeByUserComuAc extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incid_see_by_user_comu_ac);
        doToolBar(this, false);

    }
}
