package com.didekindroid.incidencia.comment;

import android.content.Context;
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
import com.didekinlib.model.incidencia.dominio.IncidComment;
import com.didekinlib.model.incidencia.dominio.Incidencia;

import java.util.List;

import timber.log.Timber;

import static com.didekindroid.incidencia.IncidDaoRemote.incidenciaDao;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.util.UIutils.checkPostExecute;

/**
 * Preconditions:
 * 1. The fragment is attached to an activity from which it receives an IncidenciaUser instance.
 * <p/>
 * Postconditions:
 */
public class IncidCommentSeeListFr extends Fragment {

    IncidCommentSeeAdapter mAdapter;
    View mView;
    ListView mListView;
    Incidencia mIncidencia;

    public static IncidCommentSeeListFr newInstance(Incidencia incidencia)
    {
        Timber.d("newInstance()");
        IncidCommentSeeListFr commentSeeListFr = new IncidCommentSeeListFr();
        Bundle args = new Bundle();
        args.putSerializable(INCIDENCIA_OBJECT.key, incidencia);
        commentSeeListFr.setArguments(args);
        return commentSeeListFr;
    }

    @Override
    public void onAttach(Context context)
    {
        Timber.d("onAttach()");
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Timber.d("onCreateView()");
        mView = inflater.inflate(R.layout.incid_comments_see_fr_layout, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Timber.d("onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

        mAdapter = new IncidCommentSeeAdapter(getActivity());
        mIncidencia = (Incidencia) getArguments().getSerializable(INCIDENCIA_OBJECT.key);
        new IncidCommentLoader().execute(mIncidencia);

        mListView = (ListView) mView.findViewById(android.R.id.list);
        //TextView for no result.
        mListView.setEmptyView(mView.findViewById(android.R.id.empty));
    }

    @Override
    public void onStart()
    {
        Timber.d("Enters onStart()");
        super.onStart();
    }

    @Override
    public void onResume()
    {
        Timber.d("Enters onResume()");
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        Timber.d("onSaveInstanceState()");
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onPause()
    {
        Timber.d("onPause()");
        super.onPause();
    }

    @Override
    public void onStop()
    {
        Timber.d("onStop()");
        super.onStop();
    }

    @Override
    public void onDestroyView()
    {
        Timber.d("onDestroyView()");
        super.onDestroyView();
    }

    @Override
    public void onDestroy()
    {
        Timber.d("onDestroy()");
        super.onDestroy();
    }

    @Override
    public void onDetach()
    {
        Timber.d("onDetach()");
        super.onDetach();
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    // TODO: to persist the task during restarts and properly cancel the task when the activity is destroyed. (Example in Shelves)
    class IncidCommentLoader extends AsyncTask<Incidencia, Void, List<IncidComment>> {

        UiException uiException;

        @Override
        protected List<IncidComment> doInBackground(Incidencia... params)
        {
            Timber.d("doInBackground()");
            List<IncidComment> comments = null;
            try {
                comments = incidenciaDao.seeCommentsByIncid(params[0].getIncidenciaId());
            } catch (UiException ue) {
                uiException = ue;
            }
            return comments;
        }

        @Override
        protected void onPostExecute(List<IncidComment> incidComments)
        {
            if (checkPostExecute(getActivity())) return;

            Timber.d("onPostExecute()");
            if (incidComments != null && incidComments.size() > 0) {
                Timber.d("onPostExecute(): incidComments != null");
                mAdapter.clear();
                mAdapter.addAll(incidComments);
                mListView.setAdapter(mAdapter);
            }
            if (uiException != null) {
                Timber.d("onPostExecute(): uiException != null");
                uiException.processMe(getActivity(), new Intent());
            }
        }
    }
}
