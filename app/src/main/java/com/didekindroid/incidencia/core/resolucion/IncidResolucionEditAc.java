package com.didekindroid.incidencia.core.resolucion;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.router.FragmentInitiatorIf;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import timber.log.Timber;

import static com.didekindroid.incidencia.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.incidencia.IncidenciaAssertionMsg.incidencia_should_be_initialized;
import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.util.UiUtil.assertTrue;
import static com.didekindroid.lib_one.util.UiUtil.doToolBar;

/**
 * This activity is a point of registration for receiving GCM notifications of new incidents.
 * <p>
 * Preconditions:
 * 1. An intent may received with an IncidImportancia instance.
 * 2. An intent may received with an incidencia.
 * 2. An intent key with a Resolucion instance may be received.
 * Postconditions:
 * 1. If NOT Resolucion intent is received and the user has authority 'adm':
 * 1.1. An incidencia resolution is registered in BD, associated to its author.
 * 1.2. An intent is passed with the incidImportancia.
 * 1.3. The edited incidencia is shown.
 * 2. If a Resolucion intent is received and the user has authority 'adm':
 * 2.1. The resolucion is modified in BD, with a new avance record.
 * 2.2. If the user choose the 'close the incidencia' option, the incidencia is closed and a new
 * avance record is inserted too.
 * 3. If a Resolucion intent is received and the user hasn't got authority 'adm':
 * 3.1 The data are shown.
 */
public class IncidResolucionEditAc extends AppCompatActivity implements FragmentInitiatorIf<Fragment> {

    IncidImportancia incidImportancia;
    Resolucion resolucion;
    Incidencia incidencia;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);
        View mAcView = getLayoutInflater().inflate(R.layout.incid_resolucion_ac, null);
        setContentView(mAcView);
        doToolBar(this, true);

        if (savedInstanceState != null) {
            return;
        }

        // Preconditions.
        assertTrue(getIntent().hasExtra(INCID_IMPORTANCIA_OBJECT.key)
                || getIntent().hasExtra(INCID_RESOLUCION_OBJECT.key), incidencia_should_be_initialized);
        incidImportancia = (IncidImportancia) getIntent().getSerializableExtra(INCID_IMPORTANCIA_OBJECT.key);
        resolucion = (Resolucion) getIntent().getSerializableExtra(INCID_RESOLUCION_OBJECT.key);
        boolean hasAdmRole = getIntent().hasExtra(INCID_IMPORTANCIA_OBJECT.key) && incidImportancia.getUserComu().hasAdministradorAuthority();
        incidencia = getIntent().hasExtra(INCID_RESOLUCION_OBJECT.key) ?
                ((Resolucion) getIntent().getSerializableExtra(INCID_RESOLUCION_OBJECT.key)).getIncidencia() :
                incidImportancia.getIncidencia();

        if (incidencia.getFechaCierre() != null){
            initFragmentTx(IncidResolucionSeeFr.newInstance(incidencia, resolucion));
            return;
        }

        if (resolucion != null) {
            if (hasAdmRole) {
                initFragmentTx(IncidResolucionEditFr.newInstance(incidImportancia, resolucion));
            } else {
                initFragmentTx(IncidResolucionSeeFr.newInstance(incidencia, resolucion));
            }
        } else {
            initFragmentTx(IncidResolucionRegFr.newInstance(incidImportancia));
        }
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
