package com.didekindroid.incidencia.core.resolucion;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekindroid.incidencia.core.CtrlerIncidenciaCore;
import com.didekindroid.lib_one.util.FechaPickerFr;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import java.sql.Timestamp;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.incidencia.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_RESOLUCION_BUNDLE;
import static com.didekindroid.incidencia.IncidContextualName.new_incid_resolucion_just_registered;
import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.util.ConnectionUtils.checkInternetConnected;
import static com.didekindroid.lib_one.util.FechaPickerFr.fecha_picker_fr_tag;
import static com.didekindroid.lib_one.util.UiUtil.getErrorMsgBuilder;
import static com.didekindroid.lib_one.util.UiUtil.getUiExceptionFromThrowable;
import static com.didekindroid.lib_one.util.UiUtil.makeToast;
import static java.util.Objects.requireNonNull;

/**
 * User: pedro@didekin
 * Date: 13/11/15
 * Time: 15:52
 */
public class IncidResolucionRegFr extends Fragment {

    IncidImportancia incidImportancia;
    ResolucionBean resolucionBean;
    TextView fechaViewForPicker;
    View frView;
    CtrlerIncidenciaCore controller;

    static IncidResolucionRegFr newInstance(IncidImportancia incidImportancia)
    {
        Timber.d("newInstance()");
        IncidResolucionRegFr fr = new IncidResolucionRegFr();
        Bundle args = new Bundle(1);
        args.putSerializable(INCID_IMPORTANCIA_OBJECT.key, incidImportancia);
        fr.setArguments(args);
        return fr;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Timber.d("onCreateView()");
        frView = inflater.inflate(R.layout.incid_resolucion_reg_frg, container, false);
        resolucionBean = new ResolucionBean();
        fechaViewForPicker = frView.findViewById(R.id.incid_resolucion_fecha_view);
        fechaViewForPicker.setOnClickListener(clickListener -> {
            FechaPickerFr fechaPicker = FechaPickerFr.newInstance(new FechaPickerResolucion(fechaViewForPicker, resolucionBean));
            fechaPicker.show(requireNonNull(getActivity()).getFragmentManager(), fecha_picker_fr_tag);
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
        controller = new CtrlerIncidenciaCore();
    }

    @Override
    public void onStop()
    {
        Timber.d("onStop()");
        super.onStop();
        controller.clearSubscriptions();
    }

    //  ================================ HELPER METHODS =======================================

    void registerResolucion()
    {
        Timber.d("registerResolucion()");

        StringBuilder errorMsg = getErrorMsgBuilder(requireNonNull(getActivity()));
        Resolucion resolucion = makeResolucionFromBean(errorMsg);

        if (resolucion == null) {
            makeToast(getActivity(), errorMsg.toString());
        } else {
            if (checkInternetConnected(getActivity())) {
                controller.regResolucion(
                        new DisposableSingleObserver<Integer>() {
                            @Override
                            public void onSuccess(Integer integer)
                            {
                                routerInitializer.get().getContextRouter()
                                        .getActionFromContextNm(new_incid_resolucion_just_registered)
                                        .initActivity(
                                                getActivity(),
                                                INCID_RESOLUCION_BUNDLE.getBundleForKey(
                                                        new IncidAndResolBundle(incidImportancia, true))
                                        );
                            }

                            @Override
                            public void onError(Throwable e)
                            {
                                routerInitializer.get().getExceptionRouter()
                                        .getActionFromMsg(getUiExceptionFromThrowable(e).getErrorHtppMsg())
                                        .initActivity(getActivity());
                            }
                        },
                        resolucion);
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
}
