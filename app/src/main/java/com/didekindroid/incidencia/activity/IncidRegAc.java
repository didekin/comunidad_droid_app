package com.didekindroid.incidencia.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.didekindroid.R;

import static com.didekindroid.utils.UIutils.doToolBar;

public class IncidRegAc extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incid_reg_ac);
        doToolBar(this, true);
    }

}
