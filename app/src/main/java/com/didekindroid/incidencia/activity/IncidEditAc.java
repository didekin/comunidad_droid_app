package com.didekindroid.incidencia.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.didekin.incidservice.domain.IncidenciaUser;
import com.didekindroid.incidencia.dominio.IncidenciaUserIntent;

import static com.didekindroid.common.utils.AppKeysForBundle.INCIDENCIA_ROL;
import static com.didekindroid.common.utils.AppKeysForBundle.INCID_USERCOMU_LIST_OBJECT;

/**
 * Preconditions:
 * 1. An intent extra is passed with the functional role of the user.
 * 2. An intent extra is passed with the object IncidenciaUser to be edited.
 * 3. Edition capabilities are dependent on:
 *      -- the functional role of the user.
 *      -- the rol of the user as original author or futher comentator.
 * Postconditions:
 * 1. An incidencia is updated and persisted, once edited.
 */
public class IncidEditAc extends AppCompatActivity {

    private static final String TAG = IncidEditAc.class.getCanonicalName();
    private IncidenciaUser mIncidenciaUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        final IncidenciaUserIntent incidenciaUserIntent =
                (IncidenciaUserIntent) getIntent().getSerializableExtra(INCID_USERCOMU_LIST_OBJECT.extra);
        mIncidenciaUser = incidenciaUserIntent.getIncidenciaUser();
        final String functionalRole = getIntent().getStringExtra(INCIDENCIA_ROL.extra);
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

}
