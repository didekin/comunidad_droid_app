package com.didekindroid.incidencia.core.reg;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.InjectorOfParentViewerIf;
import com.didekindroid.lib_one.api.ParentViewerIf;
import com.didekindroid.lib_one.api.ViewerIf;
import com.didekindroid.lib_one.api.router.FragmentInitiatorIf;

import timber.log.Timber;

import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.core.reg.ViewerIncidRegAc.newViewerIncidRegAc;
import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.util.UiUtil.doToolBar;

/**
 * Preconditions:
 * 1. The user is registered.
 * 2. An intent is received with the comunidadId where the incidencia will be open.
 * Postconditions:
 * 1. No intent passed.
 * <p>
 * This activity is a point of registration for receiving notifications of new incidencias.
 */
public class IncidRegAc extends AppCompatActivity implements InjectorOfParentViewerIf, FragmentInitiatorIf<IncidRegFr> {

    IncidRegFr incidRegFr;
    ViewerIncidRegAc viewer;
    View acView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);

        acView = getLayoutInflater().inflate(R.layout.incid_reg_ac, null);
        setContentView(acView);
        doToolBar(this, true);

        viewer = newViewerIncidRegAc(this);
        viewer.doViewInViewer(savedInstanceState, null);
        Bundle bundle = new Bundle(1);
        bundle.putLong(COMUNIDAD_ID.key, getIntent().getLongExtra(COMUNIDAD_ID.key, 0));
        incidRegFr = initFragmentById(bundle, R.id.incid_reg_frg);
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

// ===================  InjectorOfParentViewerIf  ===================

    @Override
    public ParentViewerIf getInjectedParentViewer()
    {
        Timber.d("getInjectedParentViewer()");
        return viewer;
    }

    @Override
    public void setChildInParentViewer(ViewerIf childViewer)
    {
        Timber.d("setChildInParentViewer()");
        viewer.setChildViewer(childViewer);
    }

//    ============================================================
//    ................... FragmentInitiatorIf ....................
//    ============================================================

    @Override
    public AppCompatActivity getActivity()
    {
        return this;
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
                routerInitializer.get().getMnRouter().getActionFromMnItemId(resourceId).initActivity(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
