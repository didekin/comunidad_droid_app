package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekin.incidservice.dominio.Incidencia;
import com.didekin.incidservice.dominio.Resolucion;
import com.didekin.usuario.dominio.Comunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;
import com.didekindroid.incidencia.dominio.ResolucionBean;

import java.sql.Timestamp;

import static com.didekindroid.common.activity.FechaPickerFr.FechaPickerHelper.initFechaSpinnerView;
import static com.didekindroid.common.activity.BundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.common.utils.ConnectionUtils.checkInternetConnected;
import static com.didekindroid.common.utils.UIutils.getErrorMsgBuilder;
import static com.didekindroid.common.utils.UIutils.makeToast;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.google.common.base.Preconditions.checkState;

/**
 * User: pedro@didekin
 * Date: 13/11/15
 * Time: 15:52
 */
public class IncidResolucionRegFr extends IncidResolucionFrAbstract {

    private static final String TAG = IncidResolucionRegFr.class.getCanonicalName();
    IncidImportancia mIncidImportancia;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView()");
        mFragmentView = inflater.inflate(R.layout.incid_resolucion_reg_frg, container, false);
        mResolucionBean = new ResolucionBean();
        mFechaView = initFechaSpinnerView(this);

        Button mConfirmButton = (Button) mFragmentView.findViewById(R.id.incid_resolucion_reg_ac_button);
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "View.OnClickListener().onClick()");
                registerResolucion();
            }
        });
        return mFragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Log.d(TAG,"onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
        mIncidImportancia = (IncidImportancia) getArguments().getSerializable(INCID_IMPORTANCIA_OBJECT.key);
    }

    //  ================================ HELPER METHODS =======================================

    void registerResolucion()
    {
        Log.d(TAG, "registerResolucion()");

        StringBuilder errorMsg = getErrorMsgBuilder(getActivity());
        Resolucion resolucion = makeResolucionFromBean(errorMsg);

        if (resolucion == null) {
            makeToast(getActivity(), errorMsg.toString(), Toast.LENGTH_SHORT);
        } else {
            if (checkInternetConnected(getActivity())) {
                new ResolucionRegister().execute(resolucion);
            }
        }
    }

    Resolucion makeResolucionFromBean(StringBuilder errorMsg)
    {
        Log.d(TAG, "makeResolucionFromBean()");

        makeResolucionBeanFromView(errorMsg);

        if (mResolucionBean == null) {
            return null;
        }

        final Incidencia incidencia = new Incidencia.IncidenciaBuilder()
                .incidenciaId(mIncidImportancia.getIncidencia().getIncidenciaId())
                .comunidad(new Comunidad.ComunidadBuilder()
                        .c_id(mIncidImportancia.getIncidencia().getComunidad().getC_Id())
                        .build())
                .build();
        return new Resolucion.ResolucionBuilder(incidencia)
                .descripcion(mResolucionBean.getPlan())
                .fechaPrevista(new Timestamp(mResolucionBean.getFechaPrevista()))
                .costeEstimado(mResolucionBean.getCostePrev())
                .build();
    }

    void makeResolucionBeanFromView(StringBuilder errorMsg)
    {
        Log.d(TAG, "makeResolucionBeanFromView()");

        mResolucionBean.setPlan(((EditText) mFragmentView.findViewById(R.id.incid_resolucion_desc_ed)).getText().toString());
        mResolucionBean.setCostePrevText(((EditText) mFragmentView.findViewById(R.id.incid_resolucion_coste_prev_ed)).getText().toString());
        mResolucionBean.setFechaPrevistaText(mFechaView.getText().toString());
        // La fecha se inicializa en FechaPickerFr.onDateSet().

        if (!mResolucionBean.validateBeanPlan(errorMsg, getResources(),mIncidImportancia)) {
            mResolucionBean = null;
        }
    }

//    ============================================================================================
//    .................................... INNER CLASSES .................................
//    ============================================================================================

    private class ResolucionRegister extends AsyncTask<Resolucion, Void, Integer> {

        private final String TAG = ResolucionRegister.class.getCanonicalName();
        UiException uiException;
        Resolucion resolucion;

        @Override
        protected Integer doInBackground(Resolucion... params)
        {
            Log.d(TAG, "doInBackground()");
            int rowInserted = 0;
            resolucion = params[0];

            try {
                rowInserted = IncidenciaServ.regResolucion(resolucion);
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
                // Para el caso resolución duplicada y acceder a IncidEditAc/Resolución.
                intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, mIncidImportancia);
                uiException.processMe(getActivity(), intent);
            } else {
                checkState(rowInserted == 1);
                Intent intent = new Intent(getActivity(), IncidEditAc.class);
                intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, mIncidImportancia);
                startActivity(intent);
            }
        }
    }
}