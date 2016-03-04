package com.didekindroid.incidencia.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.didekindroid.R;

import static com.didekindroid.common.utils.UIutils.doToolBar;

public class IncidResolucionEditAc extends AppCompatActivity {

    private static final String TAG = IncidResolucionEditAc.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        View mAcView = getLayoutInflater().inflate(R.layout.incid_resolucion_edit_ac, null);
        setContentView(mAcView);
        doToolBar(this, true);
    }

    // TODO: cuando el coste sea cero, poner un String vacío en pantalla.
    // TODO: hay que mostrar 'Registro: fecha_alta -- userComu que da el alta'.
}
