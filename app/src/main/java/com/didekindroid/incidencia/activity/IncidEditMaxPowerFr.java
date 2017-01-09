package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.didekin.incidencia.dominio.IncidImportancia;
import com.didekin.incidencia.dominio.Incidencia;
import com.didekinaar.exception.UiException;
import com.didekindroid.R;
import com.didekindroid.incidencia.IncidenciaDataDbHelper;
import com.didekindroid.incidencia.activity.utils.AmbitoSpinnerSettable;
import com.didekindroid.incidencia.activity.utils.ImportanciaSpinnerSettable;
import com.didekindroid.incidencia.dominio.IncidImportanciaBean;
import com.didekindroid.incidencia.dominio.IncidenciaBean;

import java.util.Objects;

import timber.log.Timber;

import static android.view.View.GONE;
import static com.didekinaar.utils.ConnectionUtils.checkInternetConnected;
import static com.didekinaar.utils.UIutils.checkPostExecute;
import static com.didekinaar.utils.UIutils.closeCursor;
import static com.didekinaar.utils.UIutils.getErrorMsgBuilder;
import static com.didekinaar.utils.UIutils.makeToast;
import static com.didekindroid.incidencia.IncidService.IncidenciaServ;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_RESOLUCION_FLAG;
import static com.didekindroid.incidencia.activity.utils.IncidSpinnersHelper.HELPER;

/**
 * User: pedro@didekin
 * Date: 22/01/16
 * Time: 16:16
 */

@SuppressWarnings("ConstantConditions")
public class IncidEditMaxPowerFr extends Fragment implements AmbitoSpinnerSettable,
        ImportanciaSpinnerSettable {

    View fFragmentView;
    IncidImportancia mIncidImportancia;
    Spinner mAmbitoIncidSpinner;
    Spinner mImportanciaSpinner;
    IncidenciaDataDbHelper dbHelper;
    IncidenciaBean mIncidenciaBean;
    IncidImportanciaBean mIncidImportanciaBean;
    Button mButtonModify;
    Button mButtonErase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Timber.d("onCreateView()");
        fFragmentView = inflater.inflate(R.layout.incid_edit_maxpower_fr, container, false);
        return fFragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Timber.d("onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
        mIncidImportancia = (IncidImportancia) getArguments().getSerializable(INCID_IMPORTANCIA_OBJECT.key);
        boolean flagResolucion = getArguments().getBoolean(INCID_RESOLUCION_FLAG.key);

        mIncidenciaBean = new IncidenciaBean();
        mIncidImportanciaBean = new IncidImportanciaBean();
        dbHelper = new IncidenciaDataDbHelper(getActivity());

        // Inicializa comunidadId.
        mIncidenciaBean.setComunidadId(mIncidImportancia.getIncidencia().getComunidad().getC_Id());
        ((TextView) fFragmentView.findViewById(R.id.incid_comunidad_txt)).setText(mIncidImportancia.getIncidencia().getComunidad().getNombreComunidad());
        ((EditText) fFragmentView.findViewById(R.id.incid_reg_desc_ed)).setText(mIncidImportancia.getIncidencia().getDescripcion());

        mAmbitoIncidSpinner = (Spinner) getView().findViewById(R.id.incid_reg_ambito_spinner);
        HELPER.doAmbitoIncidenciaSpinner(this);
        mImportanciaSpinner = (Spinner) getView().findViewById(R.id.incid_reg_importancia_spinner);
        HELPER.doImportanciaSpinner(this);
        HELPER.initUserComusImportanciaView(this);

        mButtonModify = (Button) getView().findViewById(R.id.incid_edit_fr_modif_button);
        mButtonModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("mButtonModify.onClick()");
                modifyIncidenciaAndImportancia();
            }
        });

        // Erase button doesn't appear when user hasn't adm authority or there exists a resolucion for the incidencia.
        mButtonErase = (Button) getView().findViewById(R.id.incid_edit_fr_borrar_button);
        if (!mIncidImportancia.getUserComu().hasAdministradorAuthority() || flagResolucion) {
            mButtonErase.setVisibility(GONE);
            // Texto que acompaña al botón.
            getView().findViewById(R.id.incid_edit_fr_borrar_txt).setVisibility(GONE);
        }
        mButtonErase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Timber.d("mButtonErase.onClick()");
                eraseIncidencia();
            }
        });
    }

    @Override
    public void onDestroy()
    {
        Timber.d("onDestroy()");
        closeCursor(mAmbitoIncidSpinner.getAdapter());
        dbHelper.close();
        super.onDestroy();
    }

//    ============================================================
//    ...................... HELPER METHODS ......................
//    ============================================================

    void modifyIncidenciaAndImportancia()
    {
        Timber.d("modifyIncidenciaAndImportancia()");
        StringBuilder errorMsg = getErrorMsgBuilder(getActivity());
        Objects.equals(mIncidImportancia != null, true);

        try {
            IncidImportancia incidImportancia = mIncidImportanciaBean.makeIncidImportancia(
                    errorMsg, getResources(), fFragmentView, mIncidenciaBean, mIncidImportancia);
            if (checkInternetConnected(getActivity())) {
                new IncidenciaModifyer().execute(incidImportancia);
            }
        } catch (IllegalStateException e) {
            Timber.e(e.getMessage());
            makeToast(getActivity(), errorMsg.toString(), R.color.deep_purple_100);
        }
    }

    void eraseIncidencia()
    {
        Timber.d("eraseIncidencia()");

        if (checkInternetConnected(getActivity())) {
            Objects.equals(mIncidImportancia.getUserComu().hasAdministradorAuthority(),true);
            new IncidenciaEraser().execute(mIncidImportancia.getIncidencia());
        }
    }

//    ============================================================
//    ..................... INTERFACE METHODS ....................
//    ============================================================

    @Override
    public void onAmbitoIncidSpinnerLoaded()
    {
        Timber.d("onAmbitoIncidSpinnerLoaded()");
        mAmbitoIncidSpinner.setSelection(mIncidImportancia.getIncidencia().getAmbitoIncidencia().getAmbitoId());
    }

    @Override
    public void setAmbitoSpinnerAdapter(CursorAdapter cursorAdapter)
    {
        Timber.d("setAmbitoSpinnerAdapter()");
        mAmbitoIncidSpinner.setAdapter(cursorAdapter);
    }

    @Override
    public IncidenciaDataDbHelper getDbHelper()
    {
        Timber.d("getDbHelper()");
        return dbHelper;
    }

    @Override
    public Spinner getAmbitoSpinner()
    {
        Timber.d("getAmbitoSpinner()");
        return mAmbitoIncidSpinner;
    }

    @Override
    public Incidencia getIncidencia()
    {
        Timber.d("getIncidencia()");
        return mIncidImportancia.getIncidencia();
    }

    @Override
    public IncidenciaBean getIncidenciaBean()
    {
        Timber.d("getIncidenciaBean()");
        return mIncidenciaBean;
    }

    @Override
    public IncidImportanciaBean getIncidImportanciaBean()
    {
        Timber.d("getIncidImportanciaBean()");
        return mIncidImportanciaBean;
    }

    @Override
    public Spinner getImportanciaSpinner()
    {
        Timber.d("getImportanciaSpinner()");
        return mImportanciaSpinner;
    }

    @Override
    public void onImportanciaSpinnerLoaded()
    {
        Timber.d("onImportanciaSpinnerLoaded(), importancia= %d%n", mIncidImportancia.getImportancia());
        mImportanciaSpinner.setSelection(mIncidImportancia.getImportancia());
    }

//    ============================================================
//    ..................... INNER CLASSES  .......................
//    ============================================================

    class IncidenciaModifyer extends AsyncTask<IncidImportancia, Void, Integer> {

        UiException uiException;

        @Override
        protected Integer doInBackground(IncidImportancia... incidImportancias)
        {
            Timber.d("doInBackground()");
            int rowInserted = 0;

            try {
                rowInserted = IncidenciaServ.modifyIncidImportancia(incidImportancias[0]);
            } catch (UiException e) {
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
                uiException.processMe(getActivity(), new Intent());
            } else {
                Objects.equals(rowInserted >= 1, true);
                Intent intent = new Intent(getActivity(), IncidSeeOpenByComuAc.class);
                startActivity(intent);
            }
        }
    }

    class IncidenciaEraser extends AsyncTask<Incidencia, Void, Integer> {

        UiException uiException;

        @Override
        protected Integer doInBackground(Incidencia... params)
        {
            Timber.d("doInBackground()");
            int rowsDeleted = 0;
            try {
                rowsDeleted = IncidenciaServ.deleteIncidencia(params[0].getIncidenciaId());
            } catch (UiException e) {
                uiException = e;
            }
            return rowsDeleted;
        }

        @Override
        protected void onPostExecute(Integer rowsDeleted)
        {
            if (checkPostExecute(getActivity())) return;

            Timber.d("onPostExecute()");

            if (uiException != null) {
                uiException.processMe(getActivity(), new Intent());
            } else {
                Objects.equals(rowsDeleted == 1,true);
                Intent intent = new Intent(getActivity(), IncidSeeOpenByComuAc.class);
                startActivity(intent);
            }
        }
    }
}
