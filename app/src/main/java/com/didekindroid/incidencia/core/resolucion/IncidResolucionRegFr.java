package com.didekindroid.incidencia.core.resolucion;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.util.FechaPickerFr;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import java.sql.Timestamp;

import timber.log.Timber;

import static com.didekindroid.incidencia.IncidenciaDao.incidenciaDao;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_BUNDLE;
import static com.didekindroid.incidencia.utils.IncidenciaAssertionMsg.resolucion_should_be_registered;
import static com.didekindroid.lib_one.util.ConnectionUtils.checkInternetConnected;
import static com.didekindroid.lib_one.util.UIutils.assertTrue;
import static com.didekindroid.lib_one.util.UIutils.checkPostExecute;
import static com.didekindroid.lib_one.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.lib_one.util.UIutils.makeToast;
import static com.didekindroid.router.LeadRouter.afterResolucionReg;
import static com.didekindroid.router.UiExceptionRouter.uiException_router;

/**
 * User: pedro@didekin
 * Date: 13/11/15
 * Time: 15:52
 */
public class IncidResolucionRegFr extends Fragment {

    static IncidResolucionRegFr newInstance(IncidImportancia incidImportancia)
    {
        Timber.d("newInstance()");
        IncidResolucionRegFr fr = new IncidResolucionRegFr();
        Bundle args = new Bundle(1);
        args.putSerializable(INCID_IMPORTANCIA_OBJECT.key, incidImportancia);
        fr.setArguments(args);
        return fr;
    }

    IncidImportancia incidImportancia;
    ResolucionBean resolucionBean;
    TextView fechaViewForPicker;
    View frView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Timber.d("onCreateView()");
        frView = inflater.inflate(R.layout.incid_resolucion_reg_frg, container, false);
        resolucionBean = new ResolucionBean();
        fechaViewForPicker = frView.findViewById(R.id.incid_resolucion_fecha_view);
        fechaViewForPicker.setOnClickListener(clickListener -> {
            FechaPickerFr fechaPicker = FechaPickerFr.newInstance(new FechaPickerResolucion(fechaViewForPicker, resolucionBean));
            fechaPicker.show(getActivity().getFragmentManager(), "fechaPicker");
        });

        Button mConfirmButton = frView.findViewById(R.id.incid_resolucion_reg_ac_button);
        mConfirmButton.setOnClickListener(v -> registerResolucion());
        return frView;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Timber.d("onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
        incidImportancia = (IncidImportancia) getArguments().getSerializable(INCID_IMPORTANCIA_OBJECT.key);
    }

    //  ================================ HELPER METHODS =======================================

    void registerResolucion()
    {
        Timber.d("registerResolucion()");

        StringBuilder errorMsg = getErrorMsgBuilder(getActivity());
        Resolucion resolucion = makeResolucionFromBean(errorMsg);

        if (resolucion == null) {
            makeToast(getActivity(), errorMsg.toString());
        } else {
            if (checkInternetConnected(getActivity())) {
                new ResolucionRegister().execute(resolucion);
            }
        }
    }

    Resolucion makeResolucionFromBean(StringBuilder errorMsg)
    {
        Timber.d("makeResolucionFromBean()");

        if (!makeResolucionBeanFromView(errorMsg)) {
            return null;
        }

        final Incidencia incidencia = new Incidencia.IncidenciaBuilder()
                .incidenciaId(incidImportancia.getIncidencia().getIncidenciaId())
                .comunidad(new Comunidad.ComunidadBuilder()
                        .c_id(incidImportancia.getIncidencia().getComunidad().getC_Id())
                        .build())
                .build();
        return new Resolucion.ResolucionBuilder(incidencia)
                .descripcion(resolucionBean.getPlan())
                .fechaPrevista(new Timestamp(resolucionBean.getFechaPrevista().getTimeInMillis()))
                .costeEstimado(resolucionBean.getCostePrev())
                .build();
    }

    boolean makeResolucionBeanFromView(StringBuilder errorMsg)
    {
        Timber.d("makeResolucionBeanFromView()");

        resolucionBean.setPlan(((EditText) frView.findViewById(R.id.incid_resolucion_desc_ed)).getText().toString());
        resolucionBean.setCostePrevText(((EditText) frView.findViewById(R.id.incid_resolucion_coste_prev_ed)).getText().toString());
        resolucionBean.setFechaPrevistaText(fechaViewForPicker.getText().toString());
        // La fecha se inicializa en FechaPickerFr.onDateSet().

        return resolucionBean.validateBeanPlan(errorMsg, getResources(), incidImportancia);
    }

//    ============================================================================================
//    .................................... INNER CLASSES .................................
//    ============================================================================================

    class ResolucionRegister extends AsyncTask<Resolucion, Void, Integer> {

        UiException uiException;
        Resolucion resolucion;

        @Override
        protected Integer doInBackground(Resolucion... params)
        {
            Timber.d("doInBackground()");
            int rowInserted = 0;
            resolucion = params[0];

            try {
                rowInserted = incidenciaDao.regResolucion(resolucion);
            } catch (UiException e) {
                uiException = e;
            }
            return rowInserted;
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        protected void onPostExecute(Integer rowInserted)
        {
            if (checkPostExecute(getActivity())) return;

            Timber.d("onPostExecute()");

            if (uiException != null) {
                uiException_router.getActionFromMsg(uiException.getErrorHtppMsg()).initActivity(getActivity());
            } else {
                assertTrue(rowInserted == 1, resolucion_should_be_registered);
                Bundle bundle = new Bundle(1);
                bundle.putSerializable(INCID_RESOLUCION_BUNDLE.key, new IncidAndResolBundle(incidImportancia, true));
                afterResolucionReg.initActivity(getActivity(), bundle);
            }
        }
    }
}
