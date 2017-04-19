package com.didekindroid.incidencia.list.importancia;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.incidencia.dominio.ImportanciaUser;
import com.didekinlib.model.incidencia.dominio.Incidencia;

import java.util.List;

import timber.log.Timber;

import static com.didekindroid.incidencia.IncidDaoRemote.incidenciaDao;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidenciaAssertionMsg.incidImportanciaUsers_list_should_be_initialized;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekindroid.util.UIutils.checkPostExecute;

/**
 * Preconditions:
 * 1. The fragment is initialized with an incidencia argument.
 * <p/>
 * Postconditions:
 * 1. The list of neighbour alias and importancia ratings are shown.
 */
public class IncidSeeUserComuImportanciaFr extends Fragment {

    AdapterIncidImportanciaSee mAdapter;
    View mFrView;
    ListView mListView;
    Incidencia mIncidencia;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Timber.d("onCreateView()");
        mFrView = inflater.inflate(R.layout.incid_see_usercomu_importancia_fr, container, false);
        return mFrView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Timber.d("onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

        mAdapter = new AdapterIncidImportanciaSee(getActivity());
        mIncidencia = (Incidencia) getActivity().getIntent().getSerializableExtra(INCIDENCIA_OBJECT.key);
        new IncidImportanciaLoader().execute(mIncidencia);

        mListView = (ListView) mFrView.findViewById(android.R.id.list);
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    @SuppressWarnings("WeakerAccess")
    class IncidImportanciaLoader extends AsyncTask<Incidencia, Void, List<ImportanciaUser>> {

        UiException uiException;

        @Override
        protected List<ImportanciaUser> doInBackground(Incidencia... params)
        {
            Timber.d("doInBackground()");

            List<ImportanciaUser> importanciaUsers = null;
            try {
                importanciaUsers = incidenciaDao.seeUserComusImportancia(params[0].getIncidenciaId());
            } catch (UiException ui) {
                uiException = ui;
            }
            return importanciaUsers;
        }

        @Override
        protected void onPostExecute(List<ImportanciaUser> importanciaUsers)
        {
            Timber.d("onPostExecute()");

            if (checkPostExecute(getActivity())) return;

            if (uiException != null) {
                Timber.d("onPostExecute(): uiException != null");
                assertTrue(importanciaUsers == null, incidImportanciaUsers_list_should_be_initialized);
                uiException.processMe(getActivity(), new Intent());
            }
            if (importanciaUsers != null && !importanciaUsers.isEmpty()) {
                Timber.d("onPostExecute(): importanciaUsers != null");
                mAdapter.clear();
                mAdapter.addAll(importanciaUsers);
                mListView.setAdapter(mAdapter);
            } else {
                mListView.setEmptyView(mFrView.findViewById(android.R.id.empty));
            }
        }
    }
}
