package com.didekindroid.comunidad;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.DrawerDecoratedIf;
import com.didekindroid.lib_one.api.InjectorOfParentViewerIf;
import com.didekindroid.lib_one.api.ParentViewerIf;
import com.didekindroid.lib_one.api.ViewerIf;
import com.didekindroid.lib_one.usuario.ViewerUserDrawer;

import timber.log.Timber;

import static com.didekindroid.comunidad.ViewerComuSearchAc.newViewerComuSearch;
import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.usuario.ViewerUserDrawer.newViewerDrawerMain;
import static com.didekindroid.lib_one.util.DrawerConstant.drawer_decorator_layout;
import static com.didekindroid.lib_one.util.UiUtil.doToolBar;
import static java.util.Objects.requireNonNull;

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
public class ComuSearchAc extends AppCompatActivity implements InjectorOfParentViewerIf, DrawerDecoratedIf {

    DrawerLayout acView;
    RegComuFr regComuFrg;
    ViewerComuSearchAc viewerAc;
    ViewerUserDrawer viewerDrawer;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("In onCreate()");
        super.onCreate(savedInstanceState);

        acView = (DrawerLayout) getLayoutInflater().inflate(drawer_decorator_layout, null, false);
        acView.addView(getLayoutInflater().inflate(R.layout.comu_search_include, acView, false), 0);
        setContentView(acView);
        doToolBar(this, true).setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        viewerAc = newViewerComuSearch(this);
        viewerAc.doViewInViewer(savedInstanceState, null);
        viewerDrawer = newViewerDrawerMain(requireNonNull(DrawerDecoratedIf.class.cast(this)));
        viewerDrawer.doViewInViewer(savedInstanceState, null);
        regComuFrg = (RegComuFr) getSupportFragmentManager().findFragmentById(R.id.reg_comunidad_frg);
    }

    @Override
    public void onStop()
    {
        Timber.d("onStop()");
        super.onStop();
        viewerAc.clearSubscriptions();
        viewerDrawer.clearSubscriptions();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        Timber.d("onSaveInstanceState()");
        super.onSaveInstanceState(outState);
        viewerAc.saveState(outState);
        viewerDrawer.saveState(outState);
    }

    // ==================================  InjectorOfParentViewerIf  =================================

    @Override
    public ParentViewerIf getInjectedParentViewer()
    {
        Timber.d("getInjectedParentViewer()");
        return viewerAc;
    }

    @Override
    public void setChildInParentViewer(ViewerIf viewerChild)
    {
        Timber.d("setChildInParentViewer()");
        viewerAc.setChildViewer(viewerChild);
    }

    /* ==================================== DrawerDecoratedIf ====================================*/

    @Override
    public DrawerLayout getDrawerDecoratedView()
    {
        Timber.d("getDrawerDecoratedView()");
        return acView;
    }

    @Override
    public int getDrawerMnRsId()
    {
        Timber.d("getDrawerMnRsId()");
        return R.menu.drawer_user_mn;
    }

    @Override
    public ViewerUserDrawer getViewerDrawer()
    {
        return viewerDrawer;
    }

//    ============================================================
//    ..... ACTION BAR ....
//    ============================================================

    @SuppressWarnings("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Timber.d("onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.login_item_menu, menu);
        getMenuInflater().inflate(R.menu.comu_search_ac_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        Timber.d("onPrepareOptionsMenu()");
        boolean isRegistered = requireNonNull(viewerAc.getController()).isRegisteredUser();
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
                routerInitializer.get().getMnRouter().getActionFromMnItemId(resourceId).initActivity(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}