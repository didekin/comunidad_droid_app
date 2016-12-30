package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.didekin.incidencia.dominio.IncidImportancia;
import com.didekin.incidencia.dominio.Resolucion;
import com.didekindroid.R;
import com.didekindroid.exception.UiAppException;

import java.util.Objects;

import timber.log.Timber;

import static com.didekinaar.utils.AarItemMenu.mn_handler;
import static com.didekinaar.utils.UIutils.checkPostExecute;
import static com.didekinaar.utils.UIutils.doToolBar;
import static com.didekindroid.incidencia.IncidService.IncidenciaServ;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_ACTIVITY_VIEW_ID;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_RESOLUCION_FLAG;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.incidencia.activity.utils.IncidFragmentTags.incid_edit_ac_frgs_tag;
import static com.didekindroid.util.AppMenuRouter.doUpMenu;
import static com.didekindroid.util.AppMenuRouter.routerMap;

/**
 * Preconditions:
 * 1. An intent key is received with the IncidImportancia instance to be edited.
 * -- Users with maximum powers can modify description and ambito of the incidencia.
 * -- Users with minimum powers can only modify the importance assigned by them.
 * 2. An intent key is received with a flag signalling if the incidencia has an open resolucion.
 * Postconditions:
 * 1. An incidencia is updated in BD, once edited.
 * 3. An updated incidencias list of the comunidad is showed.
 */
public class IncidEditAc extends AppCompatActivity {

    View mAcView;
    IncidImportancia mIncidImportancia;
    boolean flagResolucion;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate()");

        mIncidImportancia = (IncidImportancia) getIntent().getSerializableExtra(INCID_IMPORTANCIA_OBJECT.key);
        Objects.equals(mIncidImportancia != null && mIncidImportancia.getIncidencia() != null && mIncidImportancia.getIncidencia().getIncidenciaId() > 0, true);

        flagResolucion = getIntent().getBooleanExtra(INCID_RESOLUCION_FLAG.key, false);

        mAcView = getLayoutInflater().inflate(R.layout.incid_edit_ac, null);
        setContentView(mAcView);
        doToolBar(this, true);

        if (savedInstanceState != null) {
            Objects.equals(getSupportFragmentManager().findFragmentByTag(incid_edit_ac_frgs_tag) != null,true);
            return;
        }

        Bundle argsFragment = new Bundle();
        argsFragment.putSerializable(INCID_IMPORTANCIA_OBJECT.key, mIncidImportancia);
        argsFragment.putInt(INCID_ACTIVITY_VIEW_ID.key, R.id.incid_edit_fragment_container_ac);
        Fragment fragmentToAdd;

        if (mIncidImportancia.isIniciadorIncidencia() || mIncidImportancia.getUserComu().hasAdministradorAuthority()) {
            argsFragment.putBoolean(INCID_RESOLUCION_FLAG.key, flagResolucion);
            fragmentToAdd = new IncidEditMaxPowerFr();
        } else {
            fragmentToAdd = new IncidEditNoPowerFr();
        }

        fragmentToAdd.setArguments(argsFragment);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.incid_edit_fragment_container_ac, fragmentToAdd, incid_edit_ac_frgs_tag)
                .commit();
    }

//    ============================================================
//    ......................... MENU .............................
//    ============================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Timber.d("onCreateOptionsMenu()");
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.incid_edit_ac_mn, menu);
        return true;
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
            case R.id.incid_comment_reg_ac_mn:
            case R.id.incid_comments_see_ac_mn:
                Intent intent = new Intent();
                intent.putExtra(INCIDENCIA_OBJECT.key, mIncidImportancia.getIncidencia());
                setIntent(intent);
                mn_handler.doMenuItem(this, routerMap.get(resourceId));
                return true;
            case R.id.incid_resolucion_reg_ac_mn:
                new ResolucionGetter().execute(resourceId);
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    ============================================================
//    ..................... INNER CLASSES  .......................
//    ============================================================

    // TODO: to persist the task during restarts and properly cancel the task when the activity is destroyed. (Example in Shelves)
    class ResolucionGetter extends AsyncTask<Integer, Void, Resolucion> {

        private UiAppException uiException;
        private int itemMenuId;

        @Override
        protected Resolucion doInBackground(Integer... resourceId)
        {
            Timber.d("doInBackground()");
            itemMenuId = resourceId[0];
            Resolucion resolucion = null;

            try {
                resolucion = IncidenciaServ.seeResolucion(mIncidImportancia.getIncidencia().getIncidenciaId());
            } catch (UiAppException e) {
                uiException = e;
            }
            return resolucion;
        }

        @Override
        protected void onPostExecute(Resolucion resolucion)
        {
            if (checkPostExecute(IncidEditAc.this)) return;

            Timber.d("onPostExecute()");

            if (uiException != null) {
                uiException.processMe(IncidEditAc.this, new Intent());
                return;
            }

            Intent intent0 = new Intent();
            intent0.putExtra(INCID_IMPORTANCIA_OBJECT.key, mIncidImportancia);
            if (resolucion != null) {
                intent0.putExtra(INCID_RESOLUCION_OBJECT.key, resolucion);
            }
            setIntent(intent0);
            mn_handler.doMenuItem(IncidEditAc.this, routerMap.get(itemMenuId));
        }
    }
}


