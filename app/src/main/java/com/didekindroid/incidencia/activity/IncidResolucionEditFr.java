package com.didekindroid.incidencia.activity;

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

import com.didekin.comunidad.Comunidad;
import com.didekin.incidencia.dominio.Avance;
import com.didekin.incidencia.dominio.IncidImportancia;
import com.didekin.incidencia.dominio.Incidencia;
import com.didekin.incidencia.dominio.Resolucion;
import com.didekinaar.exception.UiException;
import com.didekinaar.utils.ConnectionUtils;
import com.didekindroid.R;
import com.didekindroid.incidencia.dominio.ResolucionBean;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

import static com.didekinaar.utils.FechaPickerFr.FechaPickerHelper.initFechaSpinnerView;
import static com.didekinaar.utils.UIutils.checkPostExecute;
import static com.didekinaar.utils.UIutils.formatTimeStampToString;
import static com.didekinaar.utils.UIutils.getErrorMsgBuilder;
import static com.didekinaar.utils.UIutils.getStringFromInteger;
import static com.didekinaar.utils.UIutils.makeToast;
import static com.didekindroid.incidencia.IncidService.IncidenciaServ;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;

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
        Objects.equals(mResolucion != null, true);
        // Inicialización de la fecha en BD en el bean, para manternela si no la modifica.
        Objects.equals(mResolucion.getFechaPrev() != null, true);
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
            makeToast(getActivity(), errorMsg.toString(), R.color.deep_purple_100);
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

    class ResolucionModifyer extends AsyncTask<Resolucion, Void, Integer> {

        UiException uiException;

        @Override
        protected Integer doInBackground(Resolucion... params)
        {
            Timber.d("doInBackground()");
            int rowModified = 0;

            try {
                rowModified = IncidenciaServ.modifyResolucion(params[0]);
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
                Objects.equals(rowModified >= 1, true);
                Intent intent = new Intent(getActivity(), IncidEditAc.class);
                intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, mIncidImportancia);
                startActivity(intent);
            }
        }
    }

    class IncidenciaCloser extends AsyncTask<Resolucion, Void, Integer> {

        UiException uiException;

        @Override
        protected Integer doInBackground(Resolucion... params)
        {
            Timber.d("doInBackground()");
            int incidenciaCancelled = 0;

            try {
                incidenciaCancelled = IncidenciaServ.closeIncidencia(params[0]);
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
                Objects.equals(incidenciaCancelled >= 2, true);
                Intent intent = new Intent(getActivity(), IncidSeeClosedByComuAc.class);
                startActivity(intent);
            }
        }
    }
}
