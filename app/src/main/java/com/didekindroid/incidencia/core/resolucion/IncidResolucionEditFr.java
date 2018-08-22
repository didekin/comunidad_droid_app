package com.didekindroid.incidencia.core.resolucion;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekindroid.incidencia.core.CtrlerIncidenciaCore;
import com.didekindroid.lib_one.util.ConnectionUtils;
import com.didekindroid.lib_one.util.FechaPickerFr;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.incidencia.dominio.Avance;
import com.didekinlib.model.incidencia.dominio.IncidAndResolBundle;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_CLOSED_LIST_FLAG;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_RESOLUCION_BUNDLE;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.incidencia.IncidContextualName.incid_open_just_closed;
import static com.didekindroid.incidencia.IncidContextualName.incid_resolucion_just_modified;
import static com.didekindroid.incidencia.IncidenciaAssertionMsg.resolucion_fechaPrev_should_be_initialized;
import static com.didekindroid.incidencia.IncidenciaAssertionMsg.resolucion_should_be_initialized;
import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.util.FechaPickerFr.fecha_picker_fr_tag;
import static com.didekindroid.lib_one.util.UiUtil.assertTrue;
import static com.didekindroid.lib_one.util.UiUtil.formatTimeStampToString;
import static com.didekindroid.lib_one.util.UiUtil.getCalendarFromTimeMillis;
import static com.didekindroid.lib_one.util.UiUtil.getErrorMsgBuilder;
import static com.didekindroid.lib_one.util.UiUtil.getStringFromInteger;
import static com.didekindroid.lib_one.util.UiUtil.getUiExceptionFromThrowable;
import static com.didekindroid.lib_one.util.UiUtil.makeToast;
import static java.util.Objects.requireNonNull;

/**
 * User: pedro@didekin
 * Date: 13/11/15
 * Time: 15:52
 */
public class IncidResolucionEditFr extends Fragment {

    Resolucion resolucion;
    IncidImportancia incidImportancia;
    ResolucionBean resolucionBean;
    TextView fechaViewForPicker;
    View frView;
    CtrlerIncidenciaCore controller;

    static IncidResolucionEditFr newInstance(IncidImportancia incidImportancia, Resolucion resolucion)
    {
        Timber.d("newInstance()");
        IncidResolucionEditFr fr = new IncidResolucionEditFr();
        Bundle args = new Bundle(1);
        args.putSerializable(INCID_IMPORTANCIA_OBJECT.key, incidImportancia);
        args.putSerializable(INCID_RESOLUCION_OBJECT.key, resolucion);
        fr.setArguments(args);
        return fr;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Timber.d("onCreateView()");
        frView = inflater.inflate(R.layout.incid_resolucion_edit_fr, container, false);
        resolucionBean = new ResolucionBean();

        fechaViewForPicker = frView.findViewById(R.id.incid_resolucion_fecha_view);
        fechaViewForPicker.setOnClickListener(clickListener -> {
            FechaPickerFr fechaPicker = FechaPickerFr.newInstance(new FechaPickerResolucion(fechaViewForPicker, resolucionBean));
            fechaPicker.show(requireNonNull(getActivity()).getFragmentManager(), fecha_picker_fr_tag);
        });

        Button mModifyButton = frView.findViewById(R.id.incid_resolucion_fr_modif_button);
        mModifyButton.setOnClickListener(v -> modifyResolucion(false));

        Button mCloseIncidButton = frView.findViewById(R.id.incid_resolucion_edit_fr_close_button);
        mCloseIncidButton.setOnClickListener(v -> modifyResolucion(true));

        return frView;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Timber.d("onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

        incidImportancia = (IncidImportancia) getArguments().getSerializable(INCID_IMPORTANCIA_OBJECT.key);
        resolucion = (Resolucion) getArguments().getSerializable(INCID_RESOLUCION_OBJECT.key);
        assertTrue(resolucion != null, resolucion_should_be_initialized);
        // Inicialización de la fecha en BD en el bean, para manternela si no la modifica.
        assertTrue(resolucion.getFechaPrev() != null, resolucion_fechaPrev_should_be_initialized);
        resolucionBean.setFechaPrevista(getCalendarFromTimeMillis(resolucion.getFechaPrev().getTime()));

        IncidAvanceSeeAdapter mAdapter = new IncidAvanceSeeAdapter(getActivity());
        mAdapter.clear();
        mAdapter.addAll(resolucion.getAvances());

        ListView mListView = frView.findViewById(android.R.id.list);
        mListView.setEmptyView(frView.findViewById(android.R.id.empty));
        /* To get visible a divider on top of the list.*/
        mListView.addHeaderView(new View(getContext()), null, true);
        mListView.setAdapter(mAdapter);

        // Plan (modo lectura).
        ((TextView) frView.findViewById(R.id.incid_resolucion_txt)).setText(resolucion.getDescripcion());
        ((EditText) frView.findViewById(R.id.incid_resolucion_coste_prev_ed)).setText(getStringFromInteger(resolucion.getCosteEstimado()));
        // Fecha texto que se corresponde con la que he utilizado para inicializar el bean.
        fechaViewForPicker.setText(formatTimeStampToString(resolucion.getFechaPrev()));
    }

    @Override
    public void onStop()
    {
        Timber.d("onDestroy()");
        if (controller != null) {
            controller.clearSubscriptions();
        }
        super.onStop();
    }

    //  ================================ HELPER METHODS =======================================

    void modifyResolucion(boolean isToBeClosed)
    {
        Timber.d("modifyResolucion()");

        StringBuilder errorMsg = getErrorMsgBuilder(requireNonNull(getActivity()));
        Resolucion resolucion = makeResolucionFromBean(errorMsg);
        controller = new CtrlerIncidenciaCore();

        if (resolucion == null) {
            makeToast(getActivity(), errorMsg.toString());
        } else if (!ConnectionUtils.isInternetConnected(getActivity())) {
            makeToast(getActivity(), R.string.no_internet_conn_toast);
        } else if (isToBeClosed)
            controller.closeIncidencia(
                    new ResolucionSingleObserver<Integer>() {
                        @Override
                        public void onSuccess(Integer updatedRows)
                        {
                            Timber.d("closeIncidencia().onSuccess(), updatedRows: %d", updatedRows);
                            routerInitializer.get().getContextRouter()
                                    .getActionFromContextNm(incid_open_just_closed)
                                    .initActivity(getActivity(), INCID_CLOSED_LIST_FLAG.getBundleForKey(true));
                        }
                    },
                    resolucion);
        else {
            controller.modifyResolucion(
                    new ResolucionSingleObserver<Integer>() {
                        @Override
                        public void onSuccess(Integer updatedRows)
                        {
                            Timber.d("modifyResolucion().onSuccess(), updatedRows: %d", updatedRows);
                            routerInitializer.get().getContextRouter()
                                    .getActionFromContextNm(incid_resolucion_just_modified)
                                    .initActivity(
                                            getActivity(),
                                            INCID_RESOLUCION_BUNDLE.getBundleForKey(new IncidAndResolBundle(incidImportancia, true))
                                    );
                        }
                    },
                    resolucion);
        }
    }

    Resolucion makeResolucionFromBean(StringBuilder errorMsg)
    {
        Timber.d("makeResolucionFromBean()");

        makeResolucionBeanFromView(errorMsg);

        if (resolucionBean == null) {
            return null;
        }

        final Incidencia incidencia = new Incidencia.IncidenciaBuilder()
                .incidenciaId(incidImportancia.getIncidencia().getIncidenciaId())
                .comunidad(new Comunidad.ComunidadBuilder()
                        .c_id(incidImportancia.getIncidencia().getComunidad().getC_Id())
                        .build())
                .build();

        List<Avance> avances = null;

        if (resolucionBean.getAvanceDesc() != null && !resolucionBean.getAvanceDesc().trim().isEmpty()) {
            avances = new ArrayList<>(1);
            final Avance avance = new Avance.AvanceBuilder()
                    .avanceDesc(resolucionBean.getAvanceDesc())
                    .build();
            avances.add(avance);
        }

        return new Resolucion.ResolucionBuilder(incidencia)
                .fechaPrevista(new Timestamp(resolucionBean.getFechaPrevista().getTimeInMillis()))
                .costeEstimado(resolucionBean.getCostePrev())
                .avances(avances)
                .buildAsFk();
    }

    void makeResolucionBeanFromView(StringBuilder errorMsg)
    {
        Timber.d("makeResolucionBeanFromView()");

        resolucionBean.setAvanceDesc(((EditText) frView.findViewById(R.id.incid_resolucion_avance_ed)).getText().toString());
        resolucionBean.setCostePrevText(((EditText) frView.findViewById(R.id.incid_resolucion_coste_prev_ed)).getText().toString());
        resolucionBean.setFechaPrevistaText(fechaViewForPicker.getText().toString());

        if (!resolucionBean.validateBeanAvance(errorMsg, getResources(), incidImportancia)) {
            resolucionBean = null;
        }
    }

    abstract class ResolucionSingleObserver<T> extends DisposableSingleObserver<T> {

        @Override
        public void onError(Throwable e)
        {
            Timber.d(e);
            routerInitializer.get().getExceptionRouter()
                    .getActionFromMsg(getUiExceptionFromThrowable(e).getErrorHtppMsg())
                    .initActivity(requireNonNull(getActivity()), COMUNIDAD_ID.getBundleForKey(incidImportancia.getIncidencia().getComunidadId()));
        }
    }
}
