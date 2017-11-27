package com.didekindroid.accesorio;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

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

        // Adding Floating Action Button to bottom right of main view
        FloatingActionButton fab = findViewById(R.id.confidencialidad_fab);
        fab.setOnClickListener(v -> doUpMenu(this));
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
