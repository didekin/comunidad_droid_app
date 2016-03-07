package com.didekindroid.incidencia.activity;

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

import static com.didekindroid.common.utils.AppKeysForBundle.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.common.utils.AppKeysForBundle.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.common.utils.UIutils.doToolBar;
import static com.didekindroid.incidencia.activity.utils.IncidenciaMenu.INCID_COMMENTS_SEE_AC;
import static com.didekindroid.incidencia.activity.utils.IncidenciaMenu.INCID_COMMENT_REG_AC;
import static com.didekindroid.incidencia.activity.utils.IncidenciaMenu.INCID_RESOLUCION_REG_EDIT_AC;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Preconditions:
 * 1. An intent extra is received with the IncidImportancia instance to be edited.
 * -- Users with maximum powers can modify description and ambito of the incidencia.
 * -- Users with minimum powers can only modify the importance assigned by them.
 * Postconditions:
 * 1. An incidencia is updated in BD, once edited.
 * 3. An updated incidencias list of the comunidad is showed.
 */
public class IncidEditAc extends AppCompatActivity implements IncidUserDataSupplier {

    private static final String TAG = IncidEditAc.class.getCanonicalName();
    View mAcView;
    IncidImportancia mIncidImportancia;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        mIncidImportancia = (IncidImportancia) getIntent().getSerializableExtra(INCID_IMPORTANCIA_OBJECT.extra);

        mAcView = getLayoutInflater().inflate(R.layout.incid_edit_ac, null);
        setContentView(mAcView);
        doToolBar(this, true);

        if (mIncidImportancia.isIniciadorIncidencia() || mIncidImportancia.getUserComu().hasAdministradorAuthority()) {
            IncidEditMaxPowerFr mFragmentMax;
            if (savedInstanceState == null) {
                mFragmentMax = new IncidEditMaxPowerFr();
                getFragmentManager().beginTransaction().add(R.id.incid_edit_fragment_container_ac, mFragmentMax).commit();
            }
        }  else {
            IncidEditNoPowerFr mFragmentMin;
            if (savedInstanceState == null){
                mFragmentMin = new IncidEditNoPowerFr();
                getFragmentManager().beginTransaction().add(R.id.incid_edit_fragment_container_ac, mFragmentMin).commit();
            }
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
        MenuItem resolverItem = menu.findItem(R.id.incid_resolucion_reg_ac_mn);
        resolverItem.setVisible(mIncidImportancia.getUserComu().hasAdministradorAuthority());
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
                intent.putExtra(INCID_IMPORTANCIA_OBJECT.extra, mIncidImportancia);
                this.setIntent(intent);
                INCID_COMMENT_REG_AC.doMenuItem(this);
                return true;
            case R.id.incid_comments_see_ac_mn:
                intent = new Intent();
                intent.putExtra(INCID_IMPORTANCIA_OBJECT.extra, mIncidImportancia);
                this.setIntent(intent);
                INCID_COMMENTS_SEE_AC.doMenuItem(this);
                return true;
            case R.id.incid_resolucion_reg_ac_mn:
                new ResolucionGetter().execute();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    ============================================================
//    .......... INTERFACE METHODS .......
//    ============================================================

    @Override
    public IncidImportancia getIncidImportancia()
    {
        return mIncidImportancia;
    }

//    ============================================================
//    ..................... INNER CLASSES  .......................
//    ============================================================

    class ResolucionGetter extends AsyncTask<Void,Void,Resolucion>{

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
            } else {
                Intent intent = new Intent();
                intent.putExtra(INCID_IMPORTANCIA_OBJECT.extra, mIncidImportancia);
                if (resolucion != null){
                    intent.putExtra(INCID_RESOLUCION_OBJECT.extra, resolucion);
                }
                IncidEditAc.this.setIntent(intent);
                INCID_RESOLUCION_REG_EDIT_AC.doMenuItem(IncidEditAc.this);
            }
        }
    }
}


