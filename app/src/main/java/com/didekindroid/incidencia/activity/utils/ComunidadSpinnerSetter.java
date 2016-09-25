package com.didekindroid.incidencia.activity.utils;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.widget.ArrayAdapter;

import com.didekin.usuario.dominio.Comunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;

import java.util.List;
import java.util.Objects;

import timber.log.Timber;

import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;

/**
 * User: pedro@didekin
 * Date: 12/01/16
 * Time: 12:32
 */
public class ComunidadSpinnerSetter<T extends Fragment & ComuSpinnerSettable> extends AsyncTask<Void, Void, List<Comunidad>> {
    // TODO: to persist the task during restarts and properly cancel the task when the activity is destroyed. (Example in Shelves)
    private UiException uiException;
    private T mFragment;

    public ComunidadSpinnerSetter(T mFragment)
    {
        this.mFragment = mFragment;
    }

    @Override
    protected List<Comunidad> doInBackground(Void... aVoid)
    {
        Timber.d("doInBackground()");
        List<Comunidad> comunidadesByUser = null;
        try {
            comunidadesByUser = IncidenciaServ.getComusByUser();
        } catch (UiException e) {
            uiException = e;
        }
        return comunidadesByUser;
    }

    @Override
    protected void onPostExecute(List<Comunidad> comunidades)
    {
        if (comunidades != null) {
            Timber.d("onPostExecute(): comunidades != null");
            ArrayAdapter<Comunidad> comunidadesAdapter = new ArrayAdapter<>(
                    mFragment.getActivity(),
                    R.layout.app_spinner_1_dropdown_item,
                    R.id.app_spinner_1_dropdown_item,
                    comunidades);
            mFragment.setComunidadSpinnerAdapter(comunidadesAdapter);
            mFragment.onComunidadSpinnerLoaded();
        }
        if (uiException != null) {
            Timber.d("onPostExecute(): uiException != null");
            Objects.equals(comunidades == null, true);
            uiException.processMe(mFragment.getActivity(), new Intent());
        }
    }
}
