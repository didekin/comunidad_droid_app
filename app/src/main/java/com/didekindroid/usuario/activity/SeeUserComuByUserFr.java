package com.didekindroid.usuario.activity;

import android.app.ListFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.didekin.serviceone.domain.UsuarioComunidad;
import com.didekindroid.exception.UiException;

import java.util.List;

import static com.didekindroid.utils.ViewsIDs.SEE_USER_COMU_BY_USER;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static com.google.common.base.Preconditions.checkState;

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
public class SeeUserComuByUserFr extends ListFragment {

    private static final String TAG = SeeUserComuByUserFr.class.getCanonicalName();

    private SeeUserComuByUserFrListener mListener;
    private SeeUserComuByUserAdapter mAdapter;
    ListView fragmentView;

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

        mListener = (SeeUserComuByUserFrListener) getActivity();
        // Adapter
        mAdapter = new SeeUserComuByUserAdapter(getActivity());
        // Loading the data...
        new UserComuByUserLoader().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView()");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Log.d(TAG, "onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

        fragmentView = getListView();
        fragmentView.setId(SEE_USER_COMU_BY_USER.idView);
        fragmentView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    @Override
    public void onStart()
    {
        Log.d(TAG, "onStart()");
        super.onStart();
    }

    @Override
    public void onResume()
    {
        Log.d(TAG, "onResume()");
        super.onResume();
    }

    @Override
    public void onPause()
    {
        Log.d(TAG, "onPause()");
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        Log.d(TAG, "onSaveInstanceState()");
        super.onSaveInstanceState(outState);
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

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        Log.d(TAG, "onListItemClick()");
        fragmentView.setItemChecked(position, true);
        v.setSelected(true);

        if (mListener != null) {
            UsuarioComunidad intentUserComuData = (UsuarioComunidad) fragmentView.getItemAtPosition(position);
            mListener.onUserComuSelected(intentUserComuData, position);
        }
    }

// .......... Interface to communicate with the Activity ...................

    public interface SeeUserComuByUserFrListener {
        void onUserComuSelected(UsuarioComunidad userComu, int position);
    }

//    ============================================================
//    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
//    ============================================================

    View getFragmentView()
    {
        Log.d(TAG, "getFragmentView()");
        return fragmentView;
    }

    class UserComuByUserLoader extends AsyncTask<Void, Void, List<UsuarioComunidad>> {

        UiException uiException;

        @Override
        protected List<UsuarioComunidad> doInBackground(Void... aVoid)
        {
            Log.d(TAG, "UserComuByUserLoader.doInBackground()");

            List<UsuarioComunidad> usuarioComunidades = null;
            try {
                usuarioComunidades = ServOne.seeUserComusByUser();
            } catch (UiException e) {
                uiException = e;
            }
            return usuarioComunidades;
        }

        @Override
        protected void onPostExecute(List<UsuarioComunidad> usuarioComunidades)
        {
            if (usuarioComunidades != null) {
                Log.d(TAG, "UserComuByUserLoader.onPostExecute(): usuarioComunidades != null");
                mAdapter.addAll(usuarioComunidades);
                setListAdapter(mAdapter);
            }
            if (uiException != null) {  // action: LOGIN.
                Log.d(TAG, "UserComuByUserLoader.onPostExecute(): uiException != null");
                checkState(usuarioComunidades == null);
                uiException.getAction().doAction(getActivity(),uiException.getResourceId());
            }
        }
    }
}
