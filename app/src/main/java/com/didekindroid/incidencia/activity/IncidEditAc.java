package com.didekindroid.incidencia.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.didekin.incidservice.domain.IncidenciaUser;
import com.didekindroid.R;

import static com.didekindroid.common.utils.AppKeysForBundle.INCIDENCIA_USER_OBJECT;
import static com.didekindroid.common.utils.UIutils.doToolBar;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Preconditions:
 * 1. An intent extra is passed with the IncidenciaUser to be edited.
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

        if (mIncidenciaUser.isModifyDescOrEraseIncid()) {
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
//        getMenuInflater().inflate(R.menu.incid_edit_ac_mn, menu);   TODO: rematar y probar cuando estén hechos los comentarios.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.d(TAG, "onOptionsItemSelected()");

        int resourceId = checkNotNull(item.getItemId());

        switch (resourceId) {
            /*case R.id.see_usercomu_by_comu_ac_mn:
                Intent intent = new Intent(this, SeeUserComuByComuAc.class);
                intent.putExtra(COMUNIDAD_ID.extra, mIdComunidad);
                this.setIntent(intent);
                SEE_USERCOMU_BY_COMU_AC.doMenuItem(this);
                return true;*/
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


