package com.didekindroid.comunidad;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.InjectorOfParentViewerIf;
import com.didekindroid.lib_one.api.ParentViewerIf;
import com.didekindroid.lib_one.api.ViewerIf;
import com.didekinlib.model.comunidad.Comunidad;

import timber.log.Timber;

import static com.didekindroid.comunidad.ViewerComuDataAc.newViewerComuDataAc;
import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.util.UiUtil.doToolBar;

/**
 * Preconditions:
 * 1. Registered user.
 * 2. Oldest user in the comunidad (to be changed in the future).
 * 3. An intent with a comunidad id key.
 * Postconditions:
 * 1. If the user has comunidad modification power, the comunidad data may have changed in DB.
 * 2. If user hasn't power, the data are merely shown.
 */
public class ComuDataAc extends AppCompatActivity implements InjectorOfParentViewerIf {

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

// ==================================  InjectorOfParentViewerIf  =================================

    @Override
    public ParentViewerIf getInjectedParentViewer()
    {
        Timber.d("getInjectedParentViewer()");
        return viewer;
    }

    @Override
    public void setChildInParentViewer(ViewerIf viewerChild)
    {
        Timber.d("setChildInParentViewer()");
        viewer.setChildViewer(viewerChild);
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
                routerInitializer.get().getMnRouter().getActionFromMnItemId(resourceId).initActivity(this);
                return true;
            case R.id.see_usercomu_by_comu_ac_mn:
                routerInitializer.get().getMnRouter().getActionFromMnItemId(resourceId)
                        .initActivity(this, COMUNIDAD_ID.getBundleForKey(getIntent().getLongExtra(COMUNIDAD_ID.key, 0L)));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
