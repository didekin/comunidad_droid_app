package com.didekindroid.incidencia.activity;

import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekin.incidservice.dominio.Incidencia;
import com.didekin.incidservice.dominio.Resolucion;
import com.didekin.usuario.dominio.Comunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.common.activity.FechaPickerFr.ActivityForFechaPicker;
import com.didekindroid.common.utils.ConnectionUtils;

import java.sql.Timestamp;

import static com.didekindroid.common.utils.AppKeysForBundle.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.common.utils.UIutils.doToolBar;
import static com.didekindroid.common.utils.UIutils.getErrorMsgBuilder;
import static com.didekindroid.common.utils.UIutils.makeToast;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

/**
 * Preconditions:
 * 1. An intent extra is received with an IncidImportancia belonging to a user with function 'adm'.
 * Postconditions:
 * 1. An incidencia resolution is registered in BD, associated to its editor.
 * 2. An intent is passed with the incidImportancia.
 * 3. The edited incidencia is shown.
 */
public class IncidResolucionRegAc extends AppCompatActivity implements
        ActivityForFechaPicker<IncidResolucionRegAc>, IncidUserDataSupplier {

    private static final String TAG = IncidResolucionRegAc.class.getCanonicalName();
    IncidResolucionRegAcFragment mRegFragment;
    IncidImportancia mIncidImportancia;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);

        View mAcView = getLayoutInflater().inflate(R.layout.incid_resolucion_reg_ac, null);
        setContentView(mAcView);
        doToolBar(this, true);

        mIncidImportancia = (IncidImportancia) getIntent().getSerializableExtra(INCID_IMPORTANCIA_OBJECT.extra);
        checkArgument(mIncidImportancia.getUserComu().hasAdministradorAuthority());

        mRegFragment = (IncidResolucionRegAcFragment) getFragmentManager()
                .findFragmentById(R.id.incid_resolucion_reg_frg);
        Button mConfirmButton = (Button) findViewById(R.id.incid_resolucion_reg_ac_button);
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "View.OnClickListener().onClick()");
                registerResolucion();
            }
        });
    }

    private void registerResolucion()
    {
        Log.d(TAG, "registerResolucion()");
        StringBuilder errorMsg = getErrorMsgBuilder(this);
        Resolucion resolucion = makeResolucionFromBean(mRegFragment.makeResolucionBeanFromView(errorMsg));

        if (resolucion == null) {
            Log.d(TAG, "registerResolucion(), resolucion == null");
            makeToast(this, errorMsg.toString(), Toast.LENGTH_SHORT);
        } else if (!ConnectionUtils.isInternetConnected(this)) {
            makeToast(this, R.string.no_internet_conn_toast, Toast.LENGTH_SHORT);
        } else {
            new ResolucionRegister().execute(resolucion);
        }
    }

    private Resolucion makeResolucionFromBean(IncidResolucionRegAcFragment.ResolucionBean resolucionBean)
    {
        if (resolucionBean == null) {
            return null;
        }

        final Incidencia incidencia = new Incidencia.IncidenciaBuilder()
                .incidenciaId(mIncidImportancia.getIncidencia().getIncidenciaId())
                .comunidad(new Comunidad.ComunidadBuilder()
                        .c_id(mIncidImportancia.getIncidencia().getComunidad().getC_Id())
                        .build())
                .build();
        return new Resolucion.ResolucionBuilder(incidencia)
                .descripcion(resolucionBean.descripcion)
                .fechaPrevista(new Timestamp(resolucionBean.fechaPrevista))
                .costeEstimado(resolucionBean.costePrev)
                .build();
    }

//  ======================== INTERFACE COMMUNICATIONS METHODS ==========================

    @Override
    public OnDateSetListener giveMyFechaFragment()
    {
        Log.d(TAG, "giveMyFechaFragment()");
        return mRegFragment;
    }

    @Override
    public IncidResolucionRegAc getActivity()
    {
        Log.d(TAG, "getActivity()");
        return this;
    }

    @Override
    public IncidImportancia getIncidImportancia()
    {
        Log.d(TAG, "getIncidImportancia()");
        return mIncidImportancia;
    }

//    ============================================================
//    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
//    ============================================================

    private class ResolucionRegister extends AsyncTask<Resolucion, Void, Integer> {

        private final String TAG = ResolucionRegister.class.getCanonicalName();
        UiException uiException;

        @Override
        protected Integer doInBackground(Resolucion... params)
        {
            Log.d(TAG, "doInBackground()");
            int rowInserted = 0;

            try {
                rowInserted = IncidenciaServ.regResolucion(params[0]);
            } catch (UiException e) {
                uiException = e;
            }
            return rowInserted;
        }

        @Override
        protected void onPostExecute(Integer rowInserted)
        {
            Log.d(TAG, "onPostExecute()");

            if (uiException != null) {
                Intent intent = new Intent();
                intent.putExtra(INCID_IMPORTANCIA_OBJECT.extra, mIncidImportancia);
                uiException.processMe(IncidResolucionRegAc.this, intent);
            } else {
                checkState(rowInserted == 1);
                Intent intent = new Intent(IncidResolucionRegAc.this, IncidResolucionEditAc.class);
                intent.putExtra(INCID_IMPORTANCIA_OBJECT.extra, mIncidImportancia);
                startActivity(intent);
            }
        }
    }
}
