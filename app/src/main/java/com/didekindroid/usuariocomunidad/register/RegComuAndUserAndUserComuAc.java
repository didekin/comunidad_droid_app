package com.didekindroid.usuariocomunidad.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.didekindroid.R;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.api.ViewerParentInjectedIf;
import com.didekindroid.api.ViewerParentInjectorIf;
import com.didekindroid.comunidad.RegComuFr;
import com.didekindroid.router.ActivityInitiator;
import com.didekindroid.usuario.RegUserFr;

import timber.log.Timber;

import static com.didekindroid.router.ActivityRouter.acRouter;
import static com.didekindroid.router.ActivityRouter.doUpMenuWithIntent;
import static com.didekindroid.usuariocomunidad.register.ViewerRegComuUserUserComuAc.newViewerRegComuUserUserComuAc;
import static com.didekindroid.util.UIutils.doToolBar;

/**
 * Preconditions:
 * 1. The user is not registered.
 * 2. The comunidad has not been registered either, by other users.
 * 3. There is not extras in the activity intent.
 */
// TODO: añadir un campo de número de vecinos en la comunidad (aprox.).
public class RegComuAndUserAndUserComuAc extends AppCompatActivity implements ViewerParentInjectorIf {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Timber.d("onCreateOptionsMenu()");

        MenuInflater inflater = getMenuInflater();
        getMenuInflater().inflate(R.menu.reg_user_activities_mn, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");

        int resourceId = item.getItemId();
        switch (resourceId) {
            case android.R.id.home:
                Intent intent = new Intent(this, acRouter.nextActivityFromMn(resourceId));
                doUpMenuWithIntent(this, intent);
                return true;
            case R.id.login_ac_mn:
                new ActivityInitiator(this).initAcFromMnKeepIntent(resourceId);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
