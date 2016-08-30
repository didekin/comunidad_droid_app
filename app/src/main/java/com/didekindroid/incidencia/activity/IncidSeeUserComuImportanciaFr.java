package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.didekin.incidservice.dominio.ImportanciaUser;
import com.didekin.incidservice.dominio.Incidencia;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;

import java.util.List;

import static android.widget.AbsListView.CHOICE_MODE_NONE;
import static com.didekindroid.common.activity.BundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Preconditions:
 * 1. The fragment is initialized with an incidencia argument.
 * <p/>
 * Postconditions:
 * 1. The list of neighbour alias and importancia ratings are shown.
 */
public class IncidSeeUserComuImportanciaFr extends Fragment {

    private static final String TAG = IncidSeeUserComuImportanciaFr.class.getCanonicalName();

    IncidImportanciaSeeAdapter mAdapter;
    View mFrView;
    ListView mListView;
    Incidencia mIncidencia;

    public static IncidSeeUserComuImportanciaFr newInstance(Incidencia incidencia)
    {
        Log.d(TAG, "newInstance()");
        IncidSeeUserComuImportanciaFr seeImportanByUserFr = new IncidSeeUserComuImportanciaFr();
        Bundle args = new Bundle();
        args.putSerializable(INCIDENCIA_OBJECT.key, incidencia);
        seeImportanByUserFr.setArguments(args);
        return seeImportanByUserFr;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView()");
        mFrView = inflater.inflate(R.layout.incid_see_usercomu_importancia_fr, container, false);
        return mFrView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Log.d(TAG, "onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

        mAdapter = new IncidImportanciaSeeAdapter(getActivity());
        mIncidencia = (Incidencia) getArguments().getSerializable(INCIDENCIA_OBJECT.key);
        new IncidImportanciaLoader().execute(mIncidencia);

        mListView = (ListView) mFrView.findViewById(android.R.id.list);
        mListView.setEmptyView(mFrView.findViewById(android.R.id.empty));
        mListView.addHeaderView(new View(getContext()), null, true);

        // Necesitamos el menú para Up Navigation.
        setHasOptionsMenu(true);
    }

    // ============================================================
    //    ..... ACTION BAR ....
    // ============================================================

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Log.d(TAG, "onOptionsItemSelected()");

        int resourceId = checkNotNull(item.getItemId());

        switch (resourceId) {
            case android.R.id.home:
                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    class IncidImportanciaLoader extends AsyncTask<Incidencia, Void, List<ImportanciaUser>> {

        private final String TAG = IncidImportanciaLoader.class.getCanonicalName();
        UiException uiException;

        @Override
        protected List<ImportanciaUser> doInBackground(Incidencia... params)
        {
            Log.d(TAG, "doInBackground()");
            List<ImportanciaUser> importanciaUsers = null;
            try {
                importanciaUsers = IncidenciaServ.seeUserComusImportancia(params[0].getIncidenciaId());
            } catch (UiException ui) {
                uiException = ui;
            }
            return importanciaUsers;
        }

        @Override
        protected void onPostExecute(List<ImportanciaUser> importanciaUsers)
        {
            Log.d(TAG, "onPostExecute()");
            if (importanciaUsers != null && importanciaUsers.size() > 0) {
                Log.d(TAG, "onPostExecute(): importanciaUsers != null");
                mAdapter.clear();
                mAdapter.addAll(importanciaUsers);
                mListView.setAdapter(mAdapter);
            }
            if (uiException != null) {
                Log.d(TAG, "onPostExecute(): uiException != null");
                checkState(importanciaUsers == null);
                uiException.processMe(getActivity(), new Intent());
            }
        }
    }
}
