package com.didekindroid.comunidad;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.didekindroid.R;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.api.ViewerParentInjectedIf;
import com.didekindroid.api.ViewerParentInjectorIf;
import com.didekindroid.router.ActivityInitiator;

import timber.log.Timber;

import static com.didekindroid.comunidad.ViewerComuSearchAc.newViewerComuSearch;
import static com.didekindroid.util.UIutils.doToolBar;

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
public class ComuSearchAc extends AppCompatActivity implements ViewerParentInjectorIf {

    View acView;
    RegComuFr regComuFrg;
    ViewerComuSearchAc viewer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("In onCreate()");
        super.onCreate(savedInstanceState);

        acView = getLayoutInflater().inflate(R.layout.comu_search_ac, null);
        setContentView(acView);
        doToolBar(this, false);

        viewer = newViewerComuSearch(this);
        viewer.doViewInViewer(savedInstanceState, null);
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

    // ==================================  ViewerParentInjectorIf  =================================

    @Override
    public ViewerParentInjectedIf getViewerAsParent()
    {
        Timber.d("getViewerAsParent()");
        return viewer;
    }

    @Override
    public void setChildInViewer(ViewerIf viewerChild)
    {
        Timber.d("setChildInViewer()");
        viewer.setChildViewer(viewerChild);
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
        boolean isRegistered = viewer.getController().isRegisteredUser();
        menu.findItem(R.id.see_usercomu_by_user_ac_mn).setVisible(isRegistered).setEnabled(isRegistered);
        menu.findItem(R.id.user_data_ac_mn).setVisible(isRegistered).setEnabled(isRegistered);
        menu.findItem(R.id.login_ac_mn).setVisible(!isRegistered).setEnabled(!isRegistered);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");
        int resourceId = item.getItemId();

        switch (resourceId) {
            case R.id.user_data_ac_mn:
            case R.id.see_usercomu_by_user_ac_mn:
            case R.id.login_ac_mn:
            case R.id.reg_nueva_comunidad_ac_mn:
                new ActivityInitiator(this).initAcFromMnKeepIntent(resourceId);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}