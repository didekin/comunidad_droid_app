package com.didekindroid.usuariocomunidad.register;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.ChildViewersInjectorIf;
import com.didekindroid.lib_one.api.ParentViewerInjectedIf;
import com.didekindroid.lib_one.api.ViewerIf;
import com.didekindroid.usuario.RegUserFr;
import com.didekinlib.model.comunidad.Comunidad;

import timber.log.Timber;

import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.lib_one.util.UIutils.doToolBar;
import static com.didekindroid.router.MnRouterAction.resourceIdToMnItem;
import static com.didekindroid.usuariocomunidad.register.ViewerRegUserAndUserComuAc.newViewerRegUserAndUserComuAc;

/**
 * User: pedro@didekin
 * Date: 11/05/15
 * Time: 19:13
 * <p>
 * Preconditions:
 * 1. The user is not registered.
 * 2. The activity receives a comunidad object, as an intent key, with the following fields:
 * -- comunidadId.
 * -- nombreComunidad (with tipoVia,nombreVia, numero and sufijoNumero).
 * -- municipio, with codInProvincia and nombre.
 * -- provincia, with provinciaId and nombre.
 * The comunidad already exists in BD.
 * <p/>
 * Postconditions:
 * 1. A long comunidadId is passed as an intent key.
 * 2. The activity SeeUserComuByComuAc is started.
 */
@SuppressWarnings("ConstantConditions")
public class RegUserAndUserComuAc extends AppCompatActivity implements ChildViewersInjectorIf {

    View acView;
    ViewerRegUserAndUserComuAc viewer;
    RegUserComuFr regUserComuFr;
    RegUserFr regUserFr;
    Menu acMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);
        acView = getLayoutInflater().inflate(R.layout.reg_user_and_usercomu_ac, null);
        setContentView(acView);
        doToolBar(this, true);

        viewer = newViewerRegUserAndUserComuAc(this);
        viewer.doViewInViewer(savedInstanceState,
                new Comunidad.ComunidadBuilder()
                        .copyComunidadNonNullValues(
                                (Comunidad) getIntent().getExtras().getSerializable(COMUNIDAD_LIST_OBJECT.key)
                        )
                        .build());

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

//    ============================================================
//    ..... ACTION BAR ....
//    ============================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Timber.d("onCreateOptionsMenu()");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.reg_user_activities_mn, menu);
        acMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        Timber.d("onPrepareOptionsMenu()");
        boolean isRegistered = viewer.getController().isRegisteredUser();
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
                resourceIdToMnItem.get(resourceId).initActivity(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
