package com.didekindroid.incidencia.comment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.model.incidencia.dominio.IncidComment;
import com.didekinlib.model.incidencia.dominio.Incidencia;

import java.util.List;

import timber.log.Timber;

import static com.didekindroid.incidencia.IncidenciaDao.incidenciaDao;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.lib_one.util.UIutils.checkPostExecute;
import static com.didekindroid.router.LeadRouter.writeNewComment;
import static com.didekindroid.router.UiExceptionRouter.getExceptionRouter;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Timber.d("onCreateView()");
        mView = inflater.inflate(R.layout.incid_comments_see_fr_layout, container, false);
        // Floating button.
        FloatingActionButton fab = mView.findViewById(R.id.incid_new_comment_fab);
        fab.setOnClickListener(v -> writeNewComment.initActivity(getActivity(), getArguments()));
        return mView;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Timber.d("onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

        mAdapter = new IncidCommentSeeAdapter(getActivity());
        mIncidencia = (Incidencia) getArguments().getSerializable(INCIDENCIA_OBJECT.key);
        new IncidCommentLoader().execute(mIncidencia);
        mListView = mView.findViewById(android.R.id.list);
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    @SuppressWarnings("WeakerAccess")
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

        @SuppressWarnings("ConstantConditions")
        @Override
        protected void onPostExecute(List<IncidComment> incidComments)
        {
            Timber.d("onPostExecute()");

            if (checkPostExecute(getActivity())) return;

            if (uiException != null) {
                Timber.d("onPostExecute(): uiException != null");
                getExceptionRouter(uiException.getErrorHtppMsg()).initActivity(getActivity());
            }
            if (incidComments != null && !incidComments.isEmpty()) {
                Timber.d("onPostExecute(): incidComments != null");
                mAdapter.clear();
                mAdapter.addAll(incidComments);
                mListView.setAdapter(mAdapter);
            } else {
                mListView.setEmptyView(mView.findViewById(android.R.id.empty));
            }
        }
    }
}
