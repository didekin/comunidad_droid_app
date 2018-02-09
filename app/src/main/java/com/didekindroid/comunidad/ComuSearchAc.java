package com.didekindroid.comunidad;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.ChildViewersInjectorIf;
import com.didekindroid.lib_one.api.ParentViewerInjectedIf;
import com.didekindroid.lib_one.api.ViewerIf;
import com.didekindroid.lib_one.api.ViewerManagerIf;
import com.didekindroid.router.ViewerDrawerMain;

import timber.log.Timber;

import static com.didekindroid.comunidad.ViewerComuSearchAc.newViewerComuSearch;
import static com.didekindroid.lib_one.util.UIutils.doToolBar;
import static com.didekindroid.router.MnRouter.resourceIdToMnItem;
import static com.didekindroid.router.ViewerDrawerMain.newViewerDrawerMain;

/**
 * Postconditions:
 * <p/>
 * 1. An object comunidad, to be used as search criterium, is passed as an intent key with the following fields:
 * -- tipoVia.
 * -- nombreVia.
 * -- numero.
 * -- sufijoNumero (it can be an empty string).
 * -- municipio with codInProvincia and provinciaId.
 */
@SuppressWarnings("ConstantConditions")
public class ComuSearchAc extends AppCompatActivity implements ChildViewersInjectorIf,
        ViewerManagerIf {

    View acView;
    RegComuFr regComuFrg;
    ViewerComuSearchAc viewerAc;
    ViewerDrawerMain viewerDrawer;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("In onCreate()");
        super.onCreate(savedInstanceState);

        acView = getLayoutInflater().inflate(R.layout.comu_search_ac, null, false);
        setContentView(acView);
        doToolBar(this, true).setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        initViewers(savedInstanceState);
        regComuFrg = (RegComuFr) getSupportFragmentManager().findFragmentById(R.id.reg_comunidad_frg);
    }

    @Override
    public void onStop()
    {
        Timber.d("onStop()");
        super.onStop();
        clearViewersSubscr();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        Timber.d("onSaveInstanceState()");
        super.onSaveInstanceState(outState);
        savedStateViewers(outState);
    }

    public ViewerDrawerMain getViewerDrawer()
    {
        return viewerDrawer;
    }

    // ==================================  ChildViewersInjectorIf  =================================

    @Override
    public ParentViewerInjectedIf getParentViewer()
    {
        Timber.d("getParentViewer()");
        return viewerAc;
    }

    @Override
    public void setChildInParentViewer(ViewerIf viewerChild)
    {
        Timber.d("setChildInParentViewer()");
        viewerAc.setChildViewer(viewerChild);
    }

    /* ==================================== ViewerManagerIf ====================================*/

    @Override
    public void initViewers(Bundle savedInstanceState)
    {
        Timber.d("initViewers()");
        viewerAc = newViewerComuSearch(this);
        viewerAc.doViewInViewer(savedInstanceState, null);
        viewerDrawer = newViewerDrawerMain(this);
        viewerDrawer.doViewInViewer(savedInstanceState, null);
    }

    @Override
    public void clearViewersSubscr()
    {
        Timber.d("clearViewersSubscr()");
        viewerAc.clearSubscriptions();
        viewerDrawer.clearSubscriptions();
    }

    @Override
    public void savedStateViewers(Bundle outState)
    {
        Timber.d("savedStateViewers()");
        viewerAc.saveState(outState);
        viewerDrawer.saveState(outState);
    }

//    ============================================================
//    ..... ACTION BAR ....
//    ============================================================

    @SuppressWarnings("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Timber.d("onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.comu_search_ac_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        Timber.d("onPrepareOptionsMenu()");
        boolean isRegistered = viewerAc.getController().isRegisteredUser();
        menu.findItem(R.id.login_ac_mn).setVisible(!isRegistered).setEnabled(!isRegistered);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");
        int resourceId = item.getItemId();

        switch (resourceId) {
            case android.R.id.home:
                viewerDrawer.openDrawer();
                return true;
            case R.id.login_ac_mn:
            case R.id.reg_nueva_comunidad_ac_mn:
                resourceIdToMnItem.get(resourceId).initActivity(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}