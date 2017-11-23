package com.didekindroid.usuariocomunidad.data;

import android.annotation.SuppressLint;
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
import com.didekindroid.usuariocomunidad.register.RegUserComuFr;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import timber.log.Timber;

import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.router.ActivityRouter.doUpMenu;
import static com.didekindroid.usuariocomunidad.data.ViewerUserComuDataAc.newViewerUserComuDataAc;
import static com.didekindroid.usuariocomunidad.util.UserComuBundleKey.USERCOMU_LIST_OBJECT;
import static com.didekindroid.util.UIutils.doToolBar;

/**
 * Preconditions:
 * 1. Registered user.
 * 2. An intent with a UsuarioComunidad key, with:
 * -- userComu: id, alias, userName.
 * -- comunidad: id, tipoVia, nombreVia, numero, sufijoNumero, fechaAlta,
 * ---- municipio: codInProvincia, nombre.
 * ------ provincia: provinciaId, nombre.
 * -- usuarioComunidad: portal, escalera, planta, puerta, roles.
 * Postconditions:
 * 1a. Registered user with modified data in a comunidad: once done, it goes to SeeUserComuByUserAc.
 * 1b. Registered user with data deleted in the comunidad: once done, it goes to SeeUserComuByUserAc.
 * 1c. Unregistered user, once she has deleted the data of the one comunidad associated to her, it goes to
 * ComuSearchAc.
 */
@SuppressWarnings("ConstantConditions")
public class UserComuDataAc extends AppCompatActivity implements ChildViewersInjectorIf, ActivityInitiatorIf {

    ViewerUserComuDataAc viewer;
    RegUserComuFr regUserComuFr;
    UsuarioComunidad oldUserComu;
    View acView;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);
        acView = getLayoutInflater().inflate(R.layout.usercomu_data_ac_layout, null);
        setContentView(acView);
        doToolBar(this, true);

        oldUserComu = (UsuarioComunidad) getIntent().getSerializableExtra(USERCOMU_LIST_OBJECT.key);
        viewer = newViewerUserComuDataAc(this);
        viewer.doViewInViewer(
                savedInstanceState,
                oldUserComu);

        regUserComuFr = (RegUserComuFr) getSupportFragmentManager().findFragmentById(R.id.reg_usercomu_frg);
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
    public void setChildInParentViewer(ViewerIf childViewer)
    {
        Timber.d("setChildInParentViewer()");
        viewer.setChildViewer(childViewer);
    }

    // ==================================  ActivityInitiatorIf  =================================

    @Override
    public Activity getActivity()
    {
        return this;
    }

    // ============================================================
    //    ..... ACTION BAR ....
    // ============================================================

    @Override
    public boolean onCreateOptionsMenu(final Menu menu)
    {
        Timber.d("onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.usercomu_data_ac_mn, menu);
        // comu_data_ac_mn is shown only to adm/pre rol and to oldest user.
        viewer.setAcMenu(menu);
        return true;
    }

    /**
     * Option 'comu_data_ac_mn' is only visible if the user is the oldest (oldest fecha_alta) UsuarioComunidad in
     * this comunidad, or has the roles adm or pre.
     * <p/>
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        Timber.d("onPrepareOptionsMenu()");
        MenuItem comuDataItem = menu.findItem(R.id.comu_data_ac_mn);
        comuDataItem.setVisible(viewer.showComuDataMn.get());
        comuDataItem.setEnabled(viewer.showComuDataMn.get());
        return super.onPrepareOptionsMenu(menu);
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
            case R.id.comu_data_ac_mn:
            case R.id.incid_see_open_by_comu_ac_mn:
            case R.id.incid_reg_ac_mn:
                Intent intent = new Intent();
                intent.putExtra(COMUNIDAD_ID.key, oldUserComu.getComunidad().getC_Id());
                setIntent(intent);
                initAcFromMenu(resourceId);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

