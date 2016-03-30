package com.didekindroid.incidencia.activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekin.incidservice.dominio.Resolucion;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;

import static com.didekindroid.common.activity.BundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.common.activity.BundleKey.INCID_ACTIVITY_VIEW_ID;
import static com.didekindroid.common.activity.BundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.common.activity.BundleKey.INCID_RESOLUCION_FLAG;
import static com.didekindroid.common.activity.BundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.common.activity.FragmentTags.incid_edit_ac_frgs_tag;
import static com.didekindroid.common.utils.UIutils.doToolBar;
import static com.didekindroid.incidencia.activity.utils.IncidenciaMenu.INCID_COMMENTS_SEE_AC;
import static com.didekindroid.incidencia.activity.utils.IncidenciaMenu.INCID_COMMENT_REG_AC;
import static com.didekindroid.incidencia.activity.utils.IncidenciaMenu.INCID_RESOLUCION_REG_EDIT_AC;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Preconditions:
 * 1. An intent key is received with the IncidImportancia instance to be edited.
 * -- Users with maximum powers can modify description and ambito of the incidencia.
 * -- Users with minimum powers can only modify the importance assigned by them.
 * Postconditions:
 * 1. An incidencia is updated in BD, once edited.
 * 3. An updated incidencias list of the comunidad is showed.
 */
public class IncidEditAc extends AppCompatActivity {

    private static final String TAG = IncidEditAc.class.getCanonicalName();

    View mAcView;
    IncidImportancia mIncidImportancia;
    boolean flagResolucion;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        mIncidImportancia = (IncidImportancia) getIntent().getSerializableExtra(INCID_IMPORTANCIA_OBJECT.key);
        flagResolucion = getIntent().getBooleanExtra(INCID_RESOLUCION_FLAG.key, false);

        mAcView = getLayoutInflater().inflate(R.layout.incid_edit_ac, null);
        setContentView(mAcView);
        doToolBar(this, true);

        if (savedInstanceState != null) {
            checkState(getFragmentManager().findFragmentByTag(incid_edit_ac_frgs_tag) != null);
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
        getFragmentManager().beginTransaction()
                .add(R.id.incid_edit_fragment_container_ac, fragmentToAdd, incid_edit_ac_frgs_tag)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

//    ============================================================
//    ......................... MENU .............................
//    ============================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Log.d(TAG, "onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.incid_edit_ac_mn, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        Log.d(TAG, "onPrepareOptionsMenu()");
        /*MenuItem resolverItem = menu.findItem(R.id.incid_resolucion_reg_ac_mn);
        resolverItem.setVisible(mIncidencia.getUserComu().hasAdministradorAuthority());*/
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.d(TAG, "onOptionsItemSelected()");

        int resourceId = checkNotNull(item.getItemId());
        Intent intent;

        switch (resourceId) {
            case R.id.incid_comment_reg_ac_mn:
                intent = new Intent();
                intent.putExtra(INCIDENCIA_OBJECT.key, mIncidImportancia.getIncidencia());
                setIntent(intent);
                INCID_COMMENT_REG_AC.doMenuItem(this);
                return true;
            case R.id.incid_comments_see_ac_mn:
                intent = new Intent();
                intent.putExtra(INCIDENCIA_OBJECT.key, mIncidImportancia.getIncidencia());
                setIntent(intent);
                INCID_COMMENTS_SEE_AC.doMenuItem(this);
                return true;
            case R.id.incid_resolucion_reg_ac_mn:
                new ResolucionGetter().execute();
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    ============================================================
//    ..................... INNER CLASSES  .......................
//    ============================================================

    class ResolucionGetter extends AsyncTask<Void, Void, Resolucion> {

        private final String TAG = ResolucionGetter.class.getCanonicalName();
        private UiException uiException;

        @Override
        protected Resolucion doInBackground(Void... aVoid)
        {
            Log.d(TAG, "doInBackground()");
            Resolucion resolucion = null;

            try {
                resolucion = IncidenciaServ.seeResolucion(mIncidImportancia.getIncidencia().getIncidenciaId());
            } catch (UiException e) {
                uiException = e;
            }
            return resolucion;
        }

        @Override
        protected void onPostExecute(Resolucion resolucion)
        {
            Log.d(TAG, "onPostExecute()");

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
            INCID_RESOLUCION_REG_EDIT_AC.doMenuItem(IncidEditAc.this);
        }
    }
}


