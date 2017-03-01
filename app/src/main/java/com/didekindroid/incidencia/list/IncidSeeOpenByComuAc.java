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
import com.didekindroid.incidencia.core.ControllerIncidRegIf;
import com.didekindroid.usuario.firebase.FirebaseTokenReactorIf;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;

import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_FLAG;
import static com.didekindroid.incidencia.utils.IncidFragmentTags.incid_see_by_comu_list_fr_tag;
import static com.didekindroid.usuario.firebase.FirebaseTokenReactor.tokenReactor;
import static com.didekindroid.util.ItemMenu.mn_handler;
import static com.didekindroid.util.MenuRouter.doUpMenu;
import static com.didekindroid.util.MenuRouter.routerMap;
import static com.didekindroid.util.UIutils.destroySubscriptions;
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
public class IncidSeeOpenByComuAc extends AppCompatActivity implements ManagerIncidSeeIf<IncidAndResolBundle>, ControllerIncidRegIf {

    IncidSeeByComuListFr<IncidAndResolBundle> mFragment;
    Comunidad mComunidadSelected;
    CompositeDisposable subscriptions;
    FirebaseTokenReactorIf reactor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate().");
        super.onCreate(savedInstanceState);

        reactor = tokenReactor;
        checkGcmToken();

        setContentView(R.layout.incid_see_open_by_comu_ac);
        doToolBar(this, true);

        if (savedInstanceState != null) {
            //noinspection unchecked
            mFragment = (IncidSeeByComuListFr<IncidAndResolBundle>) getSupportFragmentManager().findFragmentByTag(incid_see_by_comu_list_fr_tag);
            return;
        }
        mFragment = new IncidSeeByComuListFr<>();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.incid_see_open_by_comu_ac, mFragment, incid_see_by_comu_list_fr_tag)
                .commit();
    }

    @Override
    protected void onResume()
    {
        Timber.d("onResume()");
        checkGcmToken();
        super.onResume();
    }

    @Override
    public void onStop()
    {
        Timber.d("onStop()");
        super.onStop();
        destroySubscriptions(subscriptions);
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
                intent.putExtra(ComuBundleKey.COMUNIDAD_ID.key, mComunidadSelected.getC_Id());
                this.setIntent(intent);
                mn_handler.doMenuItem(this, routerMap.get(resourceId));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // ============================================================
    //    ..... IncidRegController IMPLEMENTATION ....
    /* ============================================================*/

    @Override
    public void checkGcmToken()
    {
        Timber.d("checkGcmToken()");
        subscriptions = reactor.checkGcmToken(subscriptions);
    }

    // ============================================================
    //   .............. INCIDENCIA LIST MANAGER ...............
    // ============================================================

    @Override
    public void replaceRootView(IncidAndResolBundle incidAndResolBundle)
    {
        Timber.d("replaceActionInView()");
        Intent intent = new Intent(IncidSeeOpenByComuAc.this, IncidEditAc.class);
        intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, incidAndResolBundle.getIncidImportancia());
        intent.putExtra(INCID_RESOLUCION_FLAG.key, incidAndResolBundle.hasResolucion());
        startActivity(intent);
    }

    @Override
    public Activity getActivity()
    {
        Timber.d("getContext()");
        return this;
    }

    @Override
    public UiExceptionIf.ActionForUiExceptionIf processViewerError(UiException ui)
    {
        Timber.d("processViewerError()");
        return ui.processMe(this, new Intent());
    }

    @Override
    public ControllerIncidSeeIf getController()
    {
        Timber.d("getController()");
        return new ControllerIncidOpenSee(mFragment);
    }
}
