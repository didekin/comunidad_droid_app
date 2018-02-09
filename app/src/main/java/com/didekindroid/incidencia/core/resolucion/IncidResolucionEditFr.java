package com.didekindroid.incidencia.core.resolucion;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.exception.UiException;
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

import timber.log.Timber;

import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.IncidenciaDao.incidenciaDao;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_CLOSED_LIST_FLAG;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_BUNDLE;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.incidencia.utils.IncidenciaAssertionMsg.incidencia_should_be_cancelled;
import static com.didekindroid.incidencia.utils.IncidenciaAssertionMsg.resolucion_fechaPrev_should_be_initialized;
import static com.didekindroid.incidencia.utils.IncidenciaAssertionMsg.resolucion_should_be_initialized;
import static com.didekindroid.incidencia.utils.IncidenciaAssertionMsg.resolucion_should_be_modified;
import static com.didekindroid.lib_one.util.UIutils.assertTrue;
import static com.didekindroid.lib_one.util.UIutils.checkPostExecute;
import static com.didekindroid.lib_one.util.UIutils.formatTimeStampToString;
import static com.didekindroid.lib_one.util.UIutils.getCalendarFromTimeMillis;
import static com.didekindroid.lib_one.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.lib_one.util.UIutils.getStringFromInteger;
import static com.didekindroid.lib_one.util.UIutils.makeToast;
import static com.didekindroid.router.LeadRouter.closeIncidencia;
import static com.didekindroid.router.LeadRouter.modifyResolucion;
import static com.didekindroid.router.UiExceptionRouter.getExceptionRouter;

/**
 * User: pedro@didekin
 * Date: 13/11/15
 * Time: 15:52
 */
public class IncidResolucionEditFr extends Fragment {

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
    Resolucion resolucion;
    IncidImportancia incidImportancia;
    ResolucionBean resolucionBean;
    TextView fechaViewForPicker;
    View frView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Timber.d("onCreateView()");
        frView = inflater.inflate(R.layout.incid_resolucion_edit_fr, container, false);
        resolucionBean = new ResolucionBean();

        fechaViewForPicker = frView.findViewById(R.id.incid_resolucion_fecha_view);
        fechaViewForPicker.setOnClickListener(clickListener -> {
            FechaPickerFr fechaPicker = FechaPickerFr.newInstance(new FechaPickerResolucion(fechaViewForPicker, resolucionBean));
            fechaPicker.show(getActivity().getFragmentManager(), "fechaPicker");
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

    //  ================================ HELPER METHODS =======================================

    void modifyResolucion(boolean isToBeClosed)
    {
        Timber.d("modifyResolucion()");

        StringBuilder errorMsg = getErrorMsgBuilder(getActivity());
        Resolucion resolucion = makeResolucionFromBean(errorMsg);

        if (resolucion == null) {
            makeToast(getActivity(), errorMsg.toString());
        } else if (!ConnectionUtils.isInternetConnected(getActivity())) {
            makeToast(getActivity(), R.string.no_internet_conn_toast);
        } else if (isToBeClosed) {
            new IncidenciaCloser().execute(resolucion);
        } else {
            new ResolucionModifyer().execute(resolucion);
        }
    }

    @SuppressWarnings("ConstantConditions")
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

//    ============================================================
//    ..................... INNER CLASSES  .......................
/*    ============================================================*/

    @SuppressWarnings("WeakerAccess")
    class ResolucionModifyer extends AsyncTask<Resolucion, Void, Integer> {

        UiException uiException;

        @Override
        protected Integer doInBackground(Resolucion... params)
        {
            Timber.d("doInBackground()");
            int rowModified = 0;

            try {
                rowModified = incidenciaDao.modifyResolucion(params[0]);
            } catch (UiException e) {
                uiException = e;
            }
            return rowModified;
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        protected void onPostExecute(Integer rowModified)
        {
            if (checkPostExecute(getActivity())) return;

            Timber.d("onPostExecute()");

            if (uiException != null) {
                Bundle bundle = new Bundle(1);
                bundle.putLong(COMUNIDAD_ID.key, incidImportancia.getIncidencia().getComunidadId());
                getExceptionRouter(uiException.getErrorHtppMsg()).initActivity(getActivity(), bundle);
            } else {
                assertTrue(rowModified >= 1, resolucion_should_be_modified);
                Bundle bundle = new Bundle(1);
                bundle.putSerializable(INCID_RESOLUCION_BUNDLE.key, new IncidAndResolBundle(incidImportancia, true));
                modifyResolucion.initActivity(getActivity(), bundle);
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    class IncidenciaCloser extends AsyncTask<Resolucion, Void, Integer> {

        UiException uiException;

        @Override
        protected Integer doInBackground(Resolucion... params)
        {
            Timber.d("doInBackground()");
            int incidenciaCancelled = 0;

            try {
                incidenciaCancelled = incidenciaDao.closeIncidencia(params[0]);
            } catch (UiException e) {
                uiException = e;
            }
            return incidenciaCancelled;
        }

        @SuppressWarnings("ConstantConditions")
        @Override
        protected void onPostExecute(Integer incidenciaCancelled)
        {
            if (checkPostExecute(getActivity())) return;

            Timber.d("onPostExecute()");

            if (uiException != null) {
                Bundle bundle = new Bundle(1);
                bundle.putLong(COMUNIDAD_ID.key, incidImportancia.getIncidencia().getComunidadId());
                getExceptionRouter(uiException.getErrorHtppMsg()).initActivity(getActivity(), bundle);
            } else {
                assertTrue(incidenciaCancelled >= 2, incidencia_should_be_cancelled);
                closeIncidencia.initActivity(getActivity(), INCID_CLOSED_LIST_FLAG.getBundleForKey(true));
            }
        }
    }
}
