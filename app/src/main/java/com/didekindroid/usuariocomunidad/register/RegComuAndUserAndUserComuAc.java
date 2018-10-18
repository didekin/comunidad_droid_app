package com.didekindroid.usuariocomunidad.register;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.didekindroid.R;
import com.didekindroid.comunidad.RegComuFr;
import com.didekindroid.lib_one.api.InjectorOfParentViewerIf;
import com.didekindroid.lib_one.api.ParentViewerIf;
import com.didekindroid.lib_one.api.ViewerIf;
import com.didekindroid.lib_one.usuario.RegUserFr;

import timber.log.Timber;

import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.util.UiUtil.doToolBar;
import static com.didekindroid.usuariocomunidad.register.ViewerRegComuUserUserComuAc.newViewerRegComuUserUserComuAc;
import static java.util.Objects.requireNonNull;

/**
 * Preconditions:
 * 1. The user is not registered.
 * 2. The comunidad has not been registered either, by other users.
 * 3. There is not extras in the activity intent.
 */
public class RegComuAndUserAndUserComuAc extends AppCompatActivity implements InjectorOfParentViewerIf {

    RegComuFr regComuFr;
    RegUserComuFr regUserComuFr;
    RegUserFr regUserFr;
    ViewerRegComuUserUserComuAc viewer;
    View acView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);

        acView = getLayoutInflater().inflate(R.layout.reg_comu_and_user_and_usercomu_ac, null);
        setContentView(acView);
        doToolBar(this, true);

        viewer = newViewerRegComuUserUserComuAc(this);
        viewer.doViewInViewer(savedInstanceState, null);

        regComuFr = (RegComuFr) getSupportFragmentManager().findFragmentById(R.id.reg_comunidad_frg);
        regUserComuFr = (RegUserComuFr) getSupportFragmentManager().findFragmentById(R.id.reg_usercomu_frg);
        regUserFr = (RegUserFr) getSupportFragmentManager().findFragmentById(R.id.reg_user_frg);
    }

    @Override
    public void onStop()
    {
        Timber.d("onStop()");
        super.onStop();
        viewer.clearSubscriptions();
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

//    ============================================================
//    ..... ACTION BAR ....
//    ============================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Timber.d("onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.login_item_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        Timber.d("onPrepareOptionsMenu()");
        boolean isRegistered = requireNonNull(viewer.getController()).isRegisteredUser();
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
            case R.id.login_ac_mn:
                routerInitializer.get().getMnRouter().getActionFromMnItemId(resourceId).initActivity(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
