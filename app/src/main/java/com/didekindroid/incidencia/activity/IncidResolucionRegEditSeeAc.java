package com.didekindroid.incidencia.activity;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekin.incidservice.dominio.Resolucion;
import com.didekindroid.R;
import com.didekindroid.common.activity.IntentExtraKey;

import static com.didekindroid.common.activity.IntentAction.GET_INCID_RESOLUCION;
import static com.didekindroid.common.activity.IntentExtraKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.common.activity.SavedInstanceKey.INCID_IMPORTANCIA;
import static com.didekindroid.common.activity.SavedInstanceKey.INCID_RESOLUCION;
import static com.didekindroid.common.utils.UIutils.doToolBar;
import static com.google.common.base.Preconditions.checkArgument;

/**
 * Preconditions:
 * 1. An intent extra is received with an IncidImportancia belonging to a user with function 'adm'.
 * 2. An intent extras may or may not be received with a Resolucion instance.
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
public class IncidResolucionRegEditSeeAc extends AppCompatActivity implements IncidUserDataSupplier {

    private static final String TAG = IncidResolucionRegEditSeeAc.class.getCanonicalName();

    IncidImportancia mIncidImportancia;
    Resolucion mResolucion;
    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        mIncidImportancia = (IncidImportancia) getIntent().getSerializableExtra(INCID_IMPORTANCIA_OBJECT.extra);

        mReceiver = new BroadcastReceiver() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onReceive(Context context, Intent intent)
            {
                Log.d(TAG, "BroadcastReceiver.onReceive()");
                mResolucion = (Resolucion) intent.getSerializableExtra(IntentExtraKey.INCID_RESOLUCION_OBJECT.extra);
                checkArgument(mResolucion.getIncidencia().equals(mIncidImportancia.getIncidencia()));
            }
        };

        View mAcView = getLayoutInflater().inflate(R.layout.incid_resolucion_reg_ac, null);
        setContentView(mAcView);
        doToolBar(this, true);

        if (mIncidImportancia.getUserComu().hasAdministradorAuthority()) {
            if (mResolucion != null) {
                // TODO: fragmento de edición.
            } else {
                IncidResolucionRegFr mRegFragment;
                if(savedInstanceState == null){
                    mRegFragment = new IncidResolucionRegFr();
                    getFragmentManager().beginTransaction().add(R.id.incid_resolucion_fragment_container_ac, mRegFragment).commit();
                }
            }
        } else {
            if (mResolucion != null) {
                // TODO: fragmento de visión sin edición.
            } else {
                // TODO: mensaje estático.
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        Log.d(TAG, "onSaveInstanceState()");
        if (mResolucion != null) {
            outState.putSerializable(INCID_RESOLUCION.key, mResolucion);
        }
        outState.putSerializable(INCID_IMPORTANCIA.key, mIncidImportancia);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Log.d(TAG, "onRestoreInstanceState()");
        mResolucion = (Resolucion) savedInstanceState.getSerializable(INCID_RESOLUCION.key);
        mIncidImportancia = (IncidImportancia) savedInstanceState.getSerializable(INCID_IMPORTANCIA.key);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume()
    {
        Log.d(TAG, "onResume()");
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mReceiver, new IntentFilter(GET_INCID_RESOLUCION.action));
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        Log.d(TAG, "onPause()");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onPause();
    }

//  ======================== INTERFACE COMMUNICATIONS METHODS ==========================

    @Override
    public IncidImportancia getIncidImportancia()
    {
        Log.d(TAG, "getIncidImportancia()");
        return mIncidImportancia;
    }

//    ============================================================
//    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
/*    ============================================================*/
}
