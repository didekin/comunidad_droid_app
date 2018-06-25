package com.didekindroid.usuariocomunidad.listbyuser;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.didekindroid.R;

import timber.log.Timber;

import static com.didekindroid.usuariocomunidad.listbyuser.ViewerSeeUserComuByUserFr.newViewerSeeUserComuByUserFr;

/**
 * Preconditions:
 * <p/>
 * 1. Every object UsuarioComunidad, in the list supplied to the adapter, has a fully initialized Usuario and
 * Comunidad, as well as the rest of the data.
 * <p/>
 * Postconditions:
 * <p/>
 * 1. An object UsuarioComunidad is passed to the listener activity.
 */
public class SeeUserComuByUserFr extends Fragment {

    ListView frView;
    ViewerSeeUserComuByUserFr viewer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Timber.d("onCreateView()");
        frView = (ListView) inflater.inflate(R.layout.see_user_by_user_list_fr, container, false);
        frView.setItemsCanFocus(true);
        return frView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        Timber.d("onViewCreated()");
        super.onViewCreated(view, savedInstanceState);
        viewer = newViewerSeeUserComuByUserFr(frView, getActivity());
        viewer.doViewInViewer(savedInstanceState, null);
    }

    @Override
    public void onStop()
    {
        Timber.d("onStop()");
        super.onStop();
        viewer.clearSubscriptions();
    }
}
