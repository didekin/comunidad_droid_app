package com.didekindroid.incidencia.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekin.incidservice.dominio.Resolucion;
import com.didekindroid.R;
import com.didekindroid.common.activity.BundleKey;

import static com.didekindroid.common.activity.FragmentTags.incid_resolucion_ac_frgs_tag;
import static com.didekindroid.common.activity.BundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.common.activity.BundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.common.activity.SavedInstanceKey.INCID_IMPORTANCIA;
import static com.didekindroid.common.activity.SavedInstanceKey.INCID_RESOLUCION;
import static com.didekindroid.common.utils.UIutils.doToolBar;

/**
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
public class IncidResolucionRegEditSeeAc extends AppCompatActivity {

    private static final String TAG = IncidResolucionRegEditSeeAc.class.getCanonicalName();

    IncidImportancia mIncidImportancia;
    Resolucion mResolucion;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        mIncidImportancia = (IncidImportancia) getIntent().getSerializableExtra(INCID_IMPORTANCIA_OBJECT.key);
        mResolucion = (Resolucion) getIntent().getSerializableExtra(BundleKey.INCID_RESOLUCION_OBJECT.key);

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
        getFragmentManager().beginTransaction()
                .add(R.id.incid_resolucion_fragment_container_ac, fragmentToAdd, incid_resolucion_ac_frgs_tag)
                .addToBackStack(null)
                .commit();
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
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

//    ============================================================
//    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
//    ============================================================

}
