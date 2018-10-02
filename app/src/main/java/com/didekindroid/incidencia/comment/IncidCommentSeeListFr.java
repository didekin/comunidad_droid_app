package com.didekindroid.incidencia.comment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.didekindroid.R;
import com.didekinlib.model.incidencia.dominio.IncidComment;
import com.didekinlib.model.incidencia.dominio.Incidencia;

import java.util.List;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.incidencia.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.IncidContextualName.to_register_new_incid_comment;
import static com.didekindroid.incidencia.comment.CtrlerIncidComment.doErrorInCtrler;
import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static java.util.Objects.requireNonNull;

/**
 * Preconditions:
 * 1. The fragment is attached to an activity from which it receives an IncidenciaUser instance.
 * <p/>
 * Postconditions:
 */
public class IncidCommentSeeListFr extends Fragment {

    IncidCommentSeeAdapter adapter;
    View view;
    ListView listView;
    Incidencia incidencia;
    CtrlerIncidComment controller;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Timber.d("onCreateView()");
        view = inflater.inflate(R.layout.incid_comments_see_fr_layout, container, false);
        // Floating button.
        FloatingActionButton fab = view.findViewById(R.id.incid_new_comment_fab);
        fab.setOnClickListener(
                v -> routerInitializer.get()
                        .getContextRouter()
                        .getActionFromContextNm(to_register_new_incid_comment)
                        .initActivity(requireNonNull(getActivity()), getArguments())
        );
        return view;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Timber.d("onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

        adapter = new IncidCommentSeeAdapter(getActivity());
        incidencia = (Incidencia) getArguments().getSerializable(INCIDENCIA_OBJECT.key);
        listView = view.findViewById(android.R.id.list);
        controller = new CtrlerIncidComment();

        controller.loadItemsByEntitiyId(new DisposableSingleObserver<List<IncidComment>>() {
            @Override
            public void onSuccess(List<IncidComment> incidComments)
            {
                Timber.d("onSuccess()");
                if (incidComments != null && !incidComments.isEmpty()) {
                    Timber.d("onPostExecute(): incidComments != null");
                    adapter.clear();
                    adapter.addAll(incidComments);
                    listView.setAdapter(adapter);
                } else {
                    listView.setEmptyView(view.findViewById(android.R.id.empty));
                }
            }

            @Override
            public void onError(Throwable e)
            {
                Timber.d("onError()");
                doErrorInCtrler(e, getActivity());
            }
        }, incidencia.getIncidenciaId());
    }

    @Override
    public void onStop()
    {
        Timber.d("onStop()");
        super.onStop();
        controller.clearSubscriptions();
    }
}
