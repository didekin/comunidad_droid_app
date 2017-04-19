package com.didekindroid.incidencia.resolucion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.didekindroid.R;
import com.didekindroid.incidencia.utils.IncidBundleKey;
import com.didekindroid.usuario.firebase.ViewerFirebaseTokenIf;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import timber.log.Timber;

import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.incidencia.utils.IncidFragmentTags.incid_resolucion_ac_frgs_tag;
import static com.didekindroid.usuario.firebase.ViewerFirebaseToken.newViewerFirebaseToken;
import static com.didekindroid.router.ActivityRouter.doUpMenu;
import static com.didekindroid.util.UIutils.doToolBar;

/**
 * This activity is a point of registration for receiving GCM notifications of new incidents.
 * <p>
 * Preconditions:
 * 1. An intent key is received with an IncidImportancia belonging to a user with function 'adm'.
 * 2. An intent key with a Resolucion instance MAY be received.
 * Postconditions:
 * 1. If NOT Resolucion intent is received and the user has authority 'adm':
 * 1.1. An incidencia resolution is registered in BD, associated to its editor.
 * 1.2. An intent is passed with the incidImportancia.
 * 1.3. The edited incidencia is shown.
 * 2. If a Resolucion intent is received and the user has authority 'adm':
 * 2.1. The resolucion is modified in BD, with a new avance record.
 * 2.2. If the user choose the 'close the incidencia' option, the incidencia is closed and a new
 * avance record is inserted too.
 * 3. If NOT Resolucion intent is received and the user hasn't got authority 'adm':
 * 3.1. A message informs that there is not resolución for the incidencia.
 * 4. If a Resolucion intent is received and the user hasn't got authority 'adm':
 * 4.1 The data are shown.
 */
public class IncidResolucionRegEditSeeAc extends AppCompatActivity{

    IncidImportancia mIncidImportancia;
    Resolucion mResolucion;
    ViewerFirebaseTokenIf viewerFirebaseToken;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);

        mIncidImportancia = (IncidImportancia) getIntent().getSerializableExtra(INCID_IMPORTANCIA_OBJECT.key);
        mResolucion = (Resolucion) getIntent().getSerializableExtra(IncidBundleKey.INCID_RESOLUCION_OBJECT.key);

        View mAcView = getLayoutInflater().inflate(R.layout.incid_resolucion_reg_ac, null);
        setContentView(mAcView);
        doToolBar(this, true);

        if (savedInstanceState != null) {
            return;
        }

        Bundle argsFragment = new Bundle();
        argsFragment.putSerializable(INCID_IMPORTANCIA_OBJECT.key, mIncidImportancia);
        argsFragment.putSerializable(INCID_RESOLUCION_OBJECT.key, mResolucion);
        Fragment fragmentToAdd;

        if (mIncidImportancia.getUserComu().hasAdministradorAuthority()) {
            if (mResolucion != null) {
                fragmentToAdd = new IncidResolucionEditFr();
            } else {
                fragmentToAdd = new IncidResolucionRegFr();
            }
        } else { // User without authority 'adm'
            if (mResolucion != null) {
                fragmentToAdd = new IncidResolucionSeeFr();
            } else {
                fragmentToAdd = new IncidResolucionSeeDefaultFr();
            }
        }
        fragmentToAdd.setArguments(argsFragment);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.incid_resolucion_fragment_container_ac, fragmentToAdd, incid_resolucion_ac_frgs_tag)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        Timber.d("onSaveInstanceState()");
        if (mResolucion != null) {
            outState.putSerializable(INCID_RESOLUCION_OBJECT.key, mResolucion);
        }
        outState.putSerializable(INCID_IMPORTANCIA_OBJECT.key, mIncidImportancia);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Timber.d("onRestoreInstanceState()");
        mResolucion = (Resolucion) savedInstanceState.getSerializable(INCID_RESOLUCION_OBJECT.key);
        mIncidImportancia = (IncidImportancia) savedInstanceState.getSerializable(INCID_IMPORTANCIA_OBJECT.key);
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
//    ......................... MENU .............................
//    ============================================================

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");

        int resourceId = item.getItemId();
        Intent intent;

        switch (resourceId) {
            case android.R.id.home:
                doUpMenu(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}