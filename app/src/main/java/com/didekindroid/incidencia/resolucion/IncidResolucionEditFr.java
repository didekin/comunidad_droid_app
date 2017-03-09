package com.didekindroid.incidencia.resolucion;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.core.IncidEditAc;
import com.didekindroid.incidencia.list.IncidSeeClosedByComuAc;
import com.didekindroid.util.ConnectionUtils;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.incidencia.dominio.Avance;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static com.didekindroid.incidencia.IncidDaoRemote.incidenciaDao;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.incidencia.utils.IncidenciaAssertionMsg.incidencia_should_be_cancelled;
import static com.didekindroid.incidencia.utils.IncidenciaAssertionMsg.resolucion_fechaPrev_should_be_initialized;
import static com.didekindroid.incidencia.utils.IncidenciaAssertionMsg.resolucion_should_be_initialized;
import static com.didekindroid.incidencia.utils.IncidenciaAssertionMsg.resolucion_should_be_modified;
import static com.didekindroid.util.FechaPickerFr.FechaPickerHelper.initFechaSpinnerView;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekindroid.util.UIutils.checkPostExecute;
import static com.didekindroid.util.UIutils.formatTimeStampToString;
import static com.didekindroid.util.UIutils.getErrorMsgBuilder;
import static com.didekindroid.util.UIutils.getStringFromInteger;
import static com.didekindroid.util.UIutils.makeToast;

/**
 * User: pedro@didekin
 * Date: 13/11/15
 * Time: 15:52
 */
public class IncidResolucionEditFr extends IncidResolucionFrAbstract {

    Resolucion mResolucion;
    IncidImportancia mIncidImportancia;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Timber.d("onCreateView()");
        mFragmentView = inflater.inflate(R.layout.incid_resolucion_edit_fr, container, false);
        mResolucionBean = new ResolucionBean();
        mFechaView = initFechaSpinnerView(this, (TextView) mFragmentView.findViewById(R.id.incid_resolucion_fecha_view));

        Button mModifyButton = (Button) mFragmentView.findViewById(R.id.incid_resolucion_fr_modif_button);
        mModifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("View.OnClickListener().onClick()");
                modifyResolucion(false);
            }
        });
        Button mCloseIncidButton = (Button) mFragmentView.findViewById(R.id.incid_resolucion_edit_fr_close_button);
        mCloseIncidButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("View.OnClickListener().onClick()");
                modifyResolucion(true);
            }
        });

        return mFragmentView;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Timber.d("onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

        mIncidImportancia = (IncidImportancia) getArguments().getSerializable(INCID_IMPORTANCIA_OBJECT.key);
        mResolucion = (Resolucion) getArguments().getSerializable(INCID_RESOLUCION_OBJECT.key);
        assertTrue(mResolucion != null, resolucion_should_be_initialized);
        // Inicialización de la fecha en BD en el bean, para manternela si no la modifica.
        assertTrue(mResolucion.getFechaPrev() != null, resolucion_fechaPrev_should_be_initialized);
        mResolucionBean.setFechaPrevista(mResolucion.getFechaPrev().getTime());

        IncidAvanceSeeAdapter mAdapter = new IncidAvanceSeeAdapter(getActivity());
        mAdapter.clear();
        mAdapter.addAll(mResolucion.getAvances());

        ListView mListView = (ListView) mFragmentView.findViewById(android.R.id.list);
        mListView.setEmptyView(mFragmentView.findViewById(android.R.id.empty));
        /* To get visible a divider on top of the list.*/
        mListView.addHeaderView(new View(getContext()), null, true);
        mListView.setAdapter(mAdapter);

        // Plan (modo lectura).
        ((TextView) mFragmentView.findViewById(R.id.incid_resolucion_txt)).setText(mResolucion.getDescripcion());
        ((EditText) mFragmentView.findViewById(R.id.incid_resolucion_coste_prev_ed)).setText(getStringFromInteger(mResolucion.getCosteEstimado()));
        // Fecha texto que se corresponde con la que he utilizado para inicializar el bean.
        mFechaView.setText(formatTimeStampToString(mResolucion.getFechaPrev()));
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

        if (mResolucionBean == null) {
            return null;
        }

        final Incidencia incidencia = new Incidencia.IncidenciaBuilder()
                .incidenciaId(mIncidImportancia.getIncidencia().getIncidenciaId())
                .comunidad(new Comunidad.ComunidadBuilder()
                        .c_id(mIncidImportancia.getIncidencia().getComunidad().getC_Id())
                        .build())
                .build();

        List<Avance> avances = null;

        if (mResolucionBean.getAvanceDesc() != null && !mResolucionBean.getAvanceDesc().trim().isEmpty()) {
            avances = new ArrayList<>(1);
            final Avance avance = new Avance.AvanceBuilder()
                    .avanceDesc(mResolucionBean.getAvanceDesc())
                    .build();
            avances.add(avance);
        }

        return new Resolucion.ResolucionBuilder(incidencia)
                .fechaPrevista(new Timestamp(mResolucionBean.getFechaPrevista()))
                .costeEstimado(mResolucionBean.getCostePrev())
                .avances(avances)
                .buildAsFk();
    }

    void makeResolucionBeanFromView(StringBuilder errorMsg)
    {
        Timber.d("makeResolucionBeanFromView()");

        mResolucionBean.setAvanceDesc(((EditText) mFragmentView.findViewById(R.id.incid_resolucion_avance_ed)).getText().toString());
        mResolucionBean.setCostePrevText(((EditText) mFragmentView.findViewById(R.id.incid_resolucion_coste_prev_ed)).getText().toString());
        mResolucionBean.setFechaPrevistaText(mFechaView.getText().toString());

        if (!mResolucionBean.validateBeanAvance(errorMsg, getResources(), mIncidImportancia)) {
            mResolucionBean = null;
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

        @Override
        protected void onPostExecute(Integer rowModified)
        {
            if (checkPostExecute(getActivity())) return;

            Timber.d("onPostExecute()");

            if (uiException != null) {
                Intent intent = new Intent();
                intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, mIncidImportancia);
                uiException.processMe(getActivity(), intent);
            } else {
                assertTrue(rowModified >= 1, resolucion_should_be_modified);
                Intent intent = new Intent(getActivity(), IncidEditAc.class);
                intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, mIncidImportancia);
                startActivity(intent);
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

        @Override
        protected void onPostExecute(Integer incidenciaCancelled)
        {
            if (checkPostExecute(getActivity())) return;

            Timber.d("onPostExecute()");

            if (uiException != null) {
                Intent intent = new Intent();
                uiException.processMe(getActivity(), intent);
            } else {
                assertTrue(incidenciaCancelled >= 2, incidencia_should_be_cancelled);
                Intent intent = new Intent(getActivity(), IncidSeeClosedByComuAc.class);
                startActivity(intent);
            }
        }
    }
}
