package com.didekindroid.accesorio;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.didekindroid.R;

import timber.log.Timber;

import static com.didekindroid.router.ActivityRouter.doUpMenu;
import static com.didekindroid.util.UIutils.doToolBar;
import static com.didekindroid.util.UIutils.doWrongMenuItem;

public class ConfidencialidadAc extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confidencialidad_ac);
        doToolBar(this, true);

        Button okButton = findViewById(R.id.confidencialidad_ac_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                doUpMenu(ConfidencialidadAc.this);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");
        int resourceId = item.getItemId();

        switch (resourceId) {
            case android.R.id.home:
                doUpMenu(this);
                return true;
            default:
                doWrongMenuItem(item);
                return false;
        }
    }
}
