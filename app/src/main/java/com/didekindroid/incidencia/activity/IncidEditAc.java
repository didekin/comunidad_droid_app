package com.didekindroid.incidencia.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.didekin.incidservice.domain.IncidenciaUser;
import com.didekindroid.R;

import static com.didekindroid.common.utils.AppKeysForBundle.INCIDENCIA_USER_OBJECT;
import static com.didekindroid.common.utils.UIutils.doToolBar;

/**
 * Preconditions:
 * 1. An intent extra is passed with the incidenciaUser to be edited.
 * 2. Edition capabilities are dependent on:
 * -- the functional role of the user.
 * -- the ownership of the incident (authorship) by the user.
 * -- the existence of other incidenciaUser associated to the incidencia. See IncidenciaUser.checkPowers().
 * Postconditions:
 * 1. An incidencia is updated in BD, once edited.
 * 3. An intent is passed with the comunidadId of the updated incidencia.
 * 2. La aplicación muestra la lista actualizada de incidencias de la comunidad.
 */
public class IncidEditAc extends AppCompatActivity {

    private static final String TAG = IncidEditAc.class.getCanonicalName();
    View mAcView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        final IncidenciaUser incidenciaUser = (IncidenciaUser) getIntent().getSerializableExtra(INCIDENCIA_USER_OBJECT.extra);

        mAcView = getLayoutInflater().inflate(R.layout.incid_edit_ac, null);
        setContentView(mAcView);
        doToolBar(this, true);

        if (incidenciaUser.isModifyDescOrEraseIncid()) {
            IncidEditMaxPowerFr mFragmentMax = (IncidEditMaxPowerFr) getFragmentManager().findFragmentById(R.id.incid_edit_maxpower_fr_layout);
            if (mFragmentMax == null) {
                mFragmentMax = new IncidEditMaxPowerFr();
                getFragmentManager().beginTransaction().add(R.id.incid_edit_fragment_container_ac, mFragmentMax).commit();
            }
            mFragmentMax.mIncidenciaUser = incidenciaUser;
        } else {
            Log.d(TAG, "No max powers");
        }
    }

//    ============================================================
//    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
//    ============================================================

}


