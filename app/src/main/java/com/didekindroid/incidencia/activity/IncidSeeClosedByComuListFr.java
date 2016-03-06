package com.didekindroid.incidencia.activity;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.didekin.incidservice.dominio.Incidencia;
import com.didekin.incidservice.dominio.IncidenciaUser;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;

import java.util.List;

import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.google.common.base.Preconditions.checkState;

/**
 * Preconditions:
 * <p/>
 * Postconditions:
 */
public class IncidSeeClosedByComuListFr extends ListFragment {

    public static final String TAG = IncidSeeClosedByComuListFr.class.getCanonicalName();

    //The Adapter which will be used to populate the ListView.
    IncidSeeByComuAdapter mAdapter;
    // The listener for dealing with the selection event of a line item (comunidad).
    IncidListListener mListener;
    View mView;
    ListView mListView;

    @Override
    public void onAttach(Context context)
    {
        Log.d(TAG, "onAttach()");
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView()");
        mView = inflater.inflate(R.layout.incid_see_generic_fr_layout, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Log.d(TAG, "onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

        mListener = (IncidListListener) getActivity();
        mAdapter = new IncidSeeByComuAdapter(getActivity());
        // Loading data ...
//        new IncidClosedByUserComuLoader().execute();

        mListView = (ListView) mView.findViewById(android.R.id.list);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        //TextView for no result.
        mListView.setEmptyView(mView.findViewById(android.R.id.empty));
    }

    @Override
    public void onStart()
    {
        Log.d(TAG, "Enters onStart()");
        super.onStart();
    }

    @Override
    public void onResume()
    {
        Log.d(TAG, "Enters onResume()");
        super.onResume();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        Log.d(TAG, "onListItemClick()");

        mListView.setItemChecked(position, true);
        v.setSelected(true);
        if (mListener != null) {
            Incidencia incidencia = (Incidencia) mListView.getItemAtPosition(position);
            mListener.onIncidenciaSelected(incidencia, position);
        }
    }

    @Override
    public void onPause()
    {
        Log.d(TAG, "onPause()");
        super.onPause();
    }

    @Override
    public void onStop()
    {
        Log.d(TAG, "onStop()");
        super.onStop();
    }

    @Override
    public void onDestroyView()
    {
        Log.d(TAG, "onDestroyView()");
        super.onDestroyView();
    }

    @Override
    public void onDestroy()
    {
        Log.d(TAG, "onDestroy()");
        super.onDestroy();
    }

    @Override
    public void onDetach()
    {
        Log.d(TAG, "onDetach()");
        super.onDetach();
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    class IncidClosedByUserComuLoader extends AsyncTask<Long, Void, List<IncidenciaUser>> {

        private final String TAG = IncidClosedByUserComuLoader.class.getCanonicalName();
        UiException uiException;

        @Override
        protected List<IncidenciaUser> doInBackground(Long... comunidades)
        {
            Log.d(TAG, "doInBackground()");
            List<IncidenciaUser> incidencias = null;
            try {
                incidencias = IncidenciaServ.seeIncidsClosedByComu(comunidades[0]);
            } catch (UiException e) {
                uiException = e;
            }
            return incidencias;
        }

        @Override
        protected void onPostExecute(List<IncidenciaUser> incidencias)
        {
            Log.d(TAG, "onPostExecute()");
            if (incidencias != null && incidencias.size() > 0) {
                Log.d(TAG, "onPostExecute(): incidUserComuList != null");
                mAdapter.addAll(incidencias);
                setListAdapter(mAdapter);
            }
            if (uiException != null) {  // action: LOGIN.                            Ø
                Log.d(TAG, "onPostExecute(): uiException != null");
                checkState(incidencias == null);
                uiException.processMe(getActivity(), new Intent());
            }
        }
    }
}
