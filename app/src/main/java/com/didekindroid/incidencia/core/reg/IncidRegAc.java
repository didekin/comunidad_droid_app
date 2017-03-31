package com.didekindroid.incidencia.core.reg;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.didekindroid.R;
import com.didekindroid.api.RootViewReplacer;
import com.didekindroid.api.RootViewReplacerIf;

import timber.log.Timber;

import static com.didekindroid.MenuRouter.doUpMenu;
import static com.didekindroid.incidencia.core.reg.ViewerIncidRegAc.newViewerIncidRegAc;
import static com.didekindroid.util.UIutils.doToolBar;

/**
 * Preconditions:
 * 1. The user is registered.
 * 2. No intent received.
 * Postconditions:
 * 1. No intent passed.
 * <p>
 * This activity is a point of registration for receiving notifications of new incidencias.
 * TODO: añadir varios tags a la incidencia para facilitar búsquedas.
 */
public class IncidRegAc extends AppCompatActivity implements RootViewReplacerIf {

    IncidRegAcFragment mRegAcFragment;
    ViewerIncidRegAc viewer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);

        View mAcView = getLayoutInflater().inflate(R.layout.incid_reg_ac, null);
        setContentView(mAcView);
        doToolBar(this, true);

        mRegAcFragment = (IncidRegAcFragment) getSupportFragmentManager().findFragmentById(R.id.incid_reg_frg);
        viewer = newViewerIncidRegAc(mAcView, this);
        viewer.doViewInViewer(savedInstanceState, null);
    }

    @Override
    public void replaceRootView(@NonNull Bundle bundle)
    {
        Timber.d("replaceRootView()");
        new RootViewReplacer(this).replaceRootView(bundle);
    }

    // ============================================================
    //    ..... ACTION BAR ....
    // ============================================================

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
                return super.onOptionsItemSelected(item);
        }
    }
}
