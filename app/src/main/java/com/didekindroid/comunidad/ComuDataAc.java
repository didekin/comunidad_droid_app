package com.didekindroid.comunidad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.didekindroid.R;
import com.didekindroid.api.ChildViewersInjectorIf;
import com.didekindroid.api.ParentViewerInjectedIf;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.router.ActivityInitiatorIf;
import com.didekinlib.model.comunidad.Comunidad;

import timber.log.Timber;

import static com.didekindroid.comunidad.ViewerComuDataAc.newViewerComuDataAc;
import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.router.ActivityRouter.doUpMenu;
import static com.didekindroid.util.UIutils.doToolBar;

/**
 * Preconditions:
 * 1. Registered user.
 * 2. Oldest user in the comunidad (to be changed in the future).
 * 3. An intent with a comunidad id key.
 * Postconditions:
 * 1. If the user has comunidad modification power, the comunidad data may have changed in DB.
 * 2. If user hasn't power, the data are merely shown.
 */
public class ComuDataAc extends AppCompatActivity implements ChildViewersInjectorIf, ActivityInitiatorIf {

    View acView;
    RegComuFr regComuFrg;
    ViewerComuDataAc viewer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);

        acView = getLayoutInflater().inflate(R.layout.comu_data_ac, null);
        setContentView(acView);
        doToolBar(this, true);

        viewer = newViewerComuDataAc(this);
        viewer.doViewInViewer(savedInstanceState,
                new Comunidad.ComunidadBuilder()
                        .c_id(getIntent().getLongExtra(COMUNIDAD_ID.key, 0L))
                        .build());

        regComuFrg = (RegComuFr) getSupportFragmentManager().findFragmentById(R.id.reg_comunidad_frg);
    }

    @Override
    public void onStop()
    {
        Timber.d("onStop()");
        super.onStop();
        viewer.clearSubscriptions();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        Timber.d("onSaveInstanceState()");
        super.onSaveInstanceState(outState);
        viewer.saveState(outState);
    }

// ==================================  ChildViewersInjectorIf  =================================

    @Override
    public ParentViewerInjectedIf getParentViewer()
    {
        Timber.d("getParentViewer()");
        return viewer;
    }

    @Override
    public void setChildInParentViewer(ViewerIf viewerChild)
    {
        Timber.d("setChildInParentViewer()");
        viewer.setChildViewer(viewerChild);
    }

    // ==================================  ActivityInitiatorIf  =================================

    @Override
    public Activity getActivity()
    {
        return this;
    }

//    =========================================  MENU  =============================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Timber.d("onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.comu_data_ac_mn, menu);
        return true;
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
            case R.id.see_usercomu_by_comu_ac_mn:
                Intent intent = new Intent();
                intent.putExtra(COMUNIDAD_ID.key, getIntent().getLongExtra(COMUNIDAD_ID.key, 0L));
                setIntent(intent);
                initAcFromMenu(resourceId);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
