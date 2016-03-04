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

import com.didekin.incidservice.dominio.IncidComment;
import com.didekin.incidservice.dominio.IncidImportancia;
import com.didekin.incidservice.dominio.Incidencia;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;

import java.util.List;

import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.google.common.base.Preconditions.checkState;

/**
 * Preconditions:
 * 1. The fragment is attached to an activity from which it receives an IncidenciaUser instance.
 * <p/>
 * Postconditions:
 */
public class IncidCommentSeeListFr extends ListFragment {

    public static final String TAG = IncidCommentSeeListFr.class.getCanonicalName();

    IncidCommentSeeAdapter mAdapter;
    View mView;
    ListView mListView;
    IncidImportanciaGiver mIncidImportanciaGiver;
    IncidImportancia mIncidImportancia;

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
        mView = inflater.inflate(R.layout.incid_comments_see_fr_layout, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Log.d(TAG, "onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

        mAdapter = new IncidCommentSeeAdapter(getActivity());
        mIncidImportanciaGiver = (IncidImportanciaGiver) getActivity();
        mIncidImportancia = mIncidImportanciaGiver.giveIncidImportancia();
        new IncidCommentLoader().execute(mIncidImportancia.getIncidencia());

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
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        Log.d(TAG, "onSaveInstanceState()");
        super.onSaveInstanceState(savedInstanceState);
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

//    ........................ INTERFACE METHODS ..........................

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        Log.d(TAG, "onListItemClick()");
    }

    public interface IncidImportanciaGiver {
        IncidImportancia giveIncidImportancia();
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    class IncidCommentLoader extends AsyncTask<Incidencia,Void,List<IncidComment>>{

        private final String TAG = IncidCommentLoader.class.getCanonicalName();
        UiException uiException;

        @Override
        protected List<IncidComment> doInBackground(Incidencia... params)
        {
            Log.d(TAG, "doInBackground()");
            List<IncidComment> comments = null;
            try{
                comments = IncidenciaServ.seeCommentsByIncid(params[0].getIncidenciaId());
            } catch (UiException ue){
                uiException = ue;
            }
            return comments;
        }

        @Override
        protected void onPostExecute(List<IncidComment> incidComments)
        {
            Log.d(TAG, "onPostExecute()");
            if (incidComments != null && incidComments.size() > 0) {
                Log.d(TAG, "onPostExecute(): incidComments != null");
                mAdapter.clear();
                mAdapter.addAll(incidComments);
                setListAdapter(mAdapter);
            }
            if (uiException != null) {
                Log.d(TAG, "onPostExecute(): uiException != null");
                checkState(incidComments == null);
                uiException.processMe(getActivity(), new Intent());
            }
        }
    }

}
