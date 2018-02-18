package com.didekindroid.incidencia.core.resolucion;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.router.FragmentInitiatorIf;
import com.didekindroid.lib_one.usuario.notification.ViewerNotifyTokenIf;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import timber.log.Timber;

import static com.didekindroid.incidencia.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.core.resolucion.IncidResolucionRegFr.newInstance;
import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.usuario.notification.ViewerNotifyToken.newViewerFirebaseToken;
import static com.didekindroid.lib_one.util.UiUtil.assertTrue;
import static com.didekindroid.lib_one.util.UiUtil.doToolBar;
import static com.didekindroid.usuariocomunidad.UserComuAssertionMsg.usercomu_should_have_admAuthority;

/**
 * This activity is a point of registration for receiving GCM notifications of new incidents.
 * <p>
 * Preconditions:
 * 1. An intent key is received with an IncidImportancia.
 * 2. User has adm role in the community.
 * Postconditions:
 * 1. An incidencia resolution is registered in BD, associated to its author.
 * 2. An intent is passed with the incidImportancia.
 * 3. The edited incidencia is shown.
 */
public class IncidResolucionRegAc extends AppCompatActivity implements FragmentInitiatorIf<IncidResolucionRegFr> {

    IncidImportancia incidImportancia;
    Resolucion resolucion;
    ViewerNotifyTokenIf viewerFirebaseToken;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);

        // Preconditions.
        incidImportancia = (IncidImportancia) getIntent().getSerializableExtra(INCID_IMPORTANCIA_OBJECT.key);
        assertTrue(incidImportancia.getUserComu().hasAdministradorAuthority(), usercomu_should_have_admAuthority);

        View mAcView = getLayoutInflater().inflate(R.layout.incid_resolucion_ac, null);
        setContentView(mAcView);
        doToolBar(this, true);

        if (savedInstanceState != null) {
            return;
        }
        initFragmentTx(newInstance(incidImportancia));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        Timber.d("onSaveInstanceState()");
        outState.putSerializable(INCID_IMPORTANCIA_OBJECT.key, incidImportancia);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Timber.d("onRestoreInstanceState()");
        incidImportancia = (IncidImportancia) savedInstanceState.getSerializable(INCID_IMPORTANCIA_OBJECT.key);
        super.onRestoreInstanceState(savedInstanceState);
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
        viewerFirebaseToken.clearSubscriptions();
    }

//    ============================================================
//    ................... FragmentInitiatorIf ....................
//    ============================================================

    @Override
    public AppCompatActivity getActivity()
    {
        return this;
    }

    @Override
    public int getContainerId()
    {
        return R.id.incid_resolucion_fragment_container_ac;
    }

//    ============================================================
//    ......................... MENU .............................
//    ============================================================

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");

        int resourceId = item.getItemId();

        switch (resourceId) {
            case android.R.id.home:
                routerInitializer.get().getMnRouter().getActionFromMnItemId(resourceId).initActivity(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
