package com.didekindroid.usuariocomunidad.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.didekindroid.R;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.api.ViewerParentInjectedIf;
import com.didekindroid.api.ViewerParentInjectorIf;
import com.didekindroid.comunidad.ComuSearchAc;
import com.didekindroid.comunidad.RegComuFr;
import com.didekindroid.router.ActivityRouter;

import timber.log.Timber;

import static com.didekindroid.usuariocomunidad.register.ViewerRegComuUserComuAc.newViewerRegComuUserComuAc;
import static com.didekindroid.util.UIutils.doToolBar;

/**
 * Preconditions:
 * 1. The user is registered with a different comunidad.
 */
@SuppressWarnings("ConstantConditions")
public class RegComuAndUserComuAc extends AppCompatActivity implements ViewerParentInjectorIf {

    ViewerRegComuUserComuAc viewer;
    View acView;
    RegComuFr regComuFr;
    RegUserComuFr regUserComuFr;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);

        acView = getLayoutInflater().inflate(R.layout.reg_comu_and_usercomu_ac, null);
        setContentView(acView);
        doToolBar(this, true);

        viewer = newViewerRegComuUserComuAc(this);
        viewer.doViewInViewer(savedInstanceState, null);

        regComuFr = (RegComuFr) getSupportFragmentManager().findFragmentById(R.id.reg_comunidad_frg);
        regUserComuFr = (RegUserComuFr) getSupportFragmentManager().findFragmentById(R.id
                .reg_usercomu_frg);
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
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");

        int resourceId = item.getItemId();

        switch (resourceId) {
            case android.R.id.home:
                ActivityRouter.doUpMenu(this);
                /*Intent intent = new Intent(this, ComuSearchAc.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                NavUtils.navigateUpTo(this, intent);*/
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

