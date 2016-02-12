package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.didekin.incidservice.domain.IncidenciaUser;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;

import static com.didekindroid.common.utils.AppKeysForBundle.INCIDENCIA_USER_OBJECT;
import static com.didekindroid.common.utils.UIutils.doToolBar;
import static com.didekindroid.incidencia.activity.utils.IncidenciaMenu.INCID_COMMENTS_SEE_AC;
import static com.didekindroid.incidencia.activity.utils.IncidenciaMenu.INCID_COMMENT_REG_AC;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Preconditions:
 * 1. An intent extra is received with the IncidenciaUser to be edited.
 * 2. Edition capabilities are dependent on:
 * -- the functional role of the user.
 * -- the ownership of the incident (authorship) by the user.
 * -- the existence of other IncidenciaUser associated to the incidencia. See IncidenciaUser.checkPowers().
 * 3.
 * -- Users with maximum powers can modify description and ambito of the incidencia, as well as to erase it.
 * -- Users with minimum powers can only modify the importance assigned by them.
 * Postconditions:
 * 1. An incidencia is updated in BD, once edited.
 * 2. An intent is passed with the comunidadId of the updated incidencia.
 * 3. An updated incidencias list of the comunidad is showed.
 */
public class IncidEditAc extends AppCompatActivity implements IncidUserDataSupplier {

    private static final String TAG = IncidEditAc.class.getCanonicalName();
    View mAcView;
    IncidenciaUser mIncidenciaUser;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        mIncidenciaUser = (IncidenciaUser) getIntent().getSerializableExtra(INCIDENCIA_USER_OBJECT.extra);

        mAcView = getLayoutInflater().inflate(R.layout.incid_edit_ac, null);
        setContentView(mAcView);
        doToolBar(this, true);

        UsuarioComunidad userComu = mIncidenciaUser.getUsuarioComunidad();

        if (mIncidenciaUser.isYetIniciador()
                || (userComu != null &&  userComu.hasRoleAdministrador())) {
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

        MenuItem resolverItem = menu.findItem(R.id.incid_resolucion_ac_mn);
        resolverItem.setVisible(true); // TODO: continuer.
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
                intent.putExtra(INCIDENCIA_USER_OBJECT.extra, mIncidenciaUser);
                this.setIntent(intent);
                INCID_COMMENT_REG_AC.doMenuItem(this);
                return true;
            case R.id.incid_comments_see_ac_mn:
                intent = new Intent();
                intent.putExtra(INCIDENCIA_USER_OBJECT.extra, mIncidenciaUser);
                this.setIntent(intent);
                INCID_COMMENTS_SEE_AC.doMenuItem(this);
                return true;
            case R.id.incid_resolucion_ac_mn:

            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    ============================================================
//    .......... INTERFACE METHODS .......
//    ============================================================

    @Override
    public IncidenciaUser getIncidenciaUser()
    {
        return mIncidenciaUser;
    }
}


