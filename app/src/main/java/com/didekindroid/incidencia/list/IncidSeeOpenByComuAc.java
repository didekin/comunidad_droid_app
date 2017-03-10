package com.didekindroid.incidencia.list;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.didekindroid.R;
import com.didekindroid.comunidad.ComuBundleKey;
import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf;
import com.didekindroid.incidencia.core.IncidEditAc;
import com.didekindroid.usuario.firebase.ViewerFirebaseTokenIf;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;

import timber.log.Timber;

import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_FLAG;
import static com.didekindroid.incidencia.utils.IncidFragmentTags.incid_see_by_comu_list_fr_tag;
import static com.didekindroid.usuario.firebase.ViewerFirebaseToken.newViewerFirebaseToken;
import static com.didekindroid.util.ItemMenu.mn_handler;
import static com.didekindroid.util.MenuRouter.doUpMenu;
import static com.didekindroid.util.MenuRouter.routerMap;
import static com.didekindroid.util.UIutils.doToolBar;

/**
 * This activity is a point of registration for receiving GCM notifications of new incidents.
 * <p/>
 * Preconditions:
 * 1. The user is registered.
 * 2. The user is registered NOW in the comunidad whose open incidencias are shown.
 * 3. An intent may be passed with a comunidadId, when a notification is sent when the
 * incidencia has been opened.
 * Postconditions:
 * 1. A list of IncidenciaUSer instances are shown.
 * 2. An intent is passed with an IncidImportancia instance, where the selected incidencia is embedded.
 */
public class IncidSeeOpenByComuAc extends AppCompatActivity implements
        ManagerIncidSeeIf<IncidAndResolBundle> {

    IncidSeeByComuListFr<IncidAndResolBundle> fragment;
    ViewerFirebaseTokenIf viewerFirebaseToken;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate().");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.incid_see_open_by_comu_ac);
        doToolBar(this, true);

        if (savedInstanceState != null) {
            //noinspection unchecked
            fragment = (IncidSeeByComuListFr<IncidAndResolBundle>) getSupportFragmentManager().findFragmentByTag(incid_see_by_comu_list_fr_tag);
            return;
        }
        fragment = new IncidSeeByComuListFr<>();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.incid_see_open_by_comu_ac, fragment, incid_see_by_comu_list_fr_tag)
                .commit();
    }

    @Override
    protected void onStart()
    {
        Timber.d("onStart()");
        super.onStart();
        viewerFirebaseToken = newViewerFirebaseToken(this);
        viewerFirebaseToken.checkGcmTokenAsync();
    }

    @Override
    public void onStop()
    {
        Timber.d("onStop()");
        super.onStop();
        viewerFirebaseToken.clearControllerSubscriptions();
    }

    // ============================================================
    //    ..... ACTION BAR ....
    // ============================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Timber.d("onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.incid_see_open_by_comu_ac_mn, menu);
        return super.onCreateOptionsMenu(menu);
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
            case R.id.incid_see_closed_by_comu_ac_mn:
            case R.id.incid_reg_ac_mn:
                mn_handler.doMenuItem(this, routerMap.get(resourceId));
                return true;
            case R.id.see_usercomu_by_comu_ac_mn:
                Intent intent = new Intent();
                long comunidadSelectedId = fragment.comuSpinnerViewer.getComunidadSelectedId();
                intent.putExtra(ComuBundleKey.COMUNIDAD_ID.key, comunidadSelectedId);
                this.setIntent(intent);
                mn_handler.doMenuItem(this, routerMap.get(resourceId));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // ============================================================
    //   .............. ManagerIncidSeeIf ...............
    // ============================================================

    @Override
    public ControllerIncidSeeIf getController()
    {
        Timber.d("getController()");
        return new ControllerIncidOpenSee(fragment);
    }

    // ============================================================
    //   .............. ManagerIf ...............
    // ============================================================

    @Override
    public Activity getActivity()
    {
        Timber.d("getActivity()");
        return this;
    }

    @Override
    public UiExceptionIf.ActionForUiExceptionIf processViewerError(UiException ui)
    {
        Timber.d("processViewerError()");
        return ui.processMe(this, new Intent());
    }

    @Override
    public void replaceRootView(IncidAndResolBundle incidAndResolBundle)
    {
        Timber.d("replaceActionInView()");
        Intent intent = new Intent(IncidSeeOpenByComuAc.this, IncidEditAc.class);
        intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, incidAndResolBundle.getIncidImportancia());
        intent.putExtra(INCID_RESOLUCION_FLAG.key, incidAndResolBundle.hasResolucion());
        startActivity(intent);
    }
}
