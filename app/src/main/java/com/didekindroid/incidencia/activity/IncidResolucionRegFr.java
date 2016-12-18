package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.didekin.incidencia.dominio.IncidImportancia;
import com.didekin.incidencia.dominio.Incidencia;
import com.didekin.incidencia.dominio.Resolucion;
import com.didekin.comunidad.Comunidad;
import com.didekindroid.exception.UiAppException;
import com.didekindroid.R;
import com.didekindroid.incidencia.dominio.ResolucionBean;

import java.sql.Timestamp;
import java.util.Objects;

import timber.log.Timber;

import static com.didekinaar.utils.UIutils.checkPostExecute;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekinaar.utils.FechaPickerFr.FechaPickerHelper.initFechaSpinnerView;
import static com.didekinaar.utils.ConnectionUtils.checkInternetConnected;
import static com.didekinaar.utils.UIutils.getErrorMsgBuilder;
import static com.didekinaar.utils.UIutils.makeToast;
import static com.didekindroid.incidencia.IncidService.IncidenciaServ;

/**
 * User: pedro@didekin
 * Date: 13/11/15
 * Time: 15:52
 */
public class IncidResolucionRegFr extends IncidResolucionFrAbstract {

    IncidImportancia mIncidImportancia;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Timber.d("onCreateView()");
        mFragmentView = inflater.inflate(R.layout.incid_resolucion_reg_frg, container, false);
        mResolucionBean = new ResolucionBean();
        mFechaView = initFechaSpinnerView(this, (TextView) mFragmentView.findViewById(R.id.incid_resolucion_fecha_view));

        Button mConfirmButton = (Button) mFragmentView.findViewById(R.id.incid_resolucion_reg_ac_button);
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("View.OnClickListener().onClick()");
                registerResolucion();
            }
        });
        return mFragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Timber.d("onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
        mIncidImportancia = (IncidImportancia) getArguments().getSerializable(INCID_IMPORTANCIA_OBJECT.key);
    }

    //  ================================ HELPER METHODS =======================================

    void registerResolucion()
    {
        Timber.d("registerResolucion()");

        StringBuilder errorMsg = getErrorMsgBuilder(getActivity());
        Resolucion resolucion = makeResolucionFromBean(errorMsg);

        if (resolucion == null) {
            makeToast(getActivity(), errorMsg.toString(), com.didekinaar.R.color.deep_purple_100);
        } else {
            if (checkInternetConnected(getActivity())) {
                new ResolucionRegister().execute(resolucion);
            }
        }
    }

    Resolucion makeResolucionFromBean(StringBuilder errorMsg)
    {
        Timber.d("makeResolucionFromBean()");

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
        Timber.d("makeResolucionBeanFromView()");

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

        UiAppException uiException;
        Resolucion resolucion;

        ResolucionRegister()
        {
        }

        @Override
        protected Integer doInBackground(Resolucion... params)
        {
            Timber.d("doInBackground()");
            int rowInserted = 0;
            resolucion = params[0];

            try {
                rowInserted = IncidenciaServ.regResolucion(resolucion);
            } catch (UiAppException e) {
                uiException = e;
            }
            return rowInserted;
        }

        @Override
        protected void onPostExecute(Integer rowInserted)
        {
            if (checkPostExecute(getActivity())) return;

            Timber.d("onPostExecute()");

            if (uiException != null) {
                Intent intent = new Intent();
                // Para el caso resolución duplicada y acceder a IncidEditAc/Resolución.
                intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, mIncidImportancia);
                uiException.processMe(getActivity(), intent);
            } else {
                Objects.equals(rowInserted == 1,true);
                Intent intent = new Intent(getActivity(), IncidEditAc.class);
                intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, mIncidImportancia);
                startActivity(intent);
            }
        }
    }
}
