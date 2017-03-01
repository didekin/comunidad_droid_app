package com.didekindroid.usuariocomunidad;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.util.List;

import timber.log.Timber;

import static com.didekindroid.usuariocomunidad.UserComuAssertionMsg.usercomu_list_should_be_initialized;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekindroid.util.UIutils.checkPostExecute;

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

    public SeeUserComuByUserAdapter mAdapter;
    SeeUserComuByUserFrListener mListener;
    ListView fragmentView;

    public SeeUserComuByUserFr()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Timber.d("onCreate()");
        super.onCreate(savedInstanceState);
        // Loading the data...
        new UserComuByUserLoader().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Timber.d("onCreateView()");
        fragmentView = (ListView) inflater.inflate(R.layout.see_user_by_user_list_fr, container, false);
        // To get visible a divider on top of the list.
//        fragmentView.addHeaderView(new View(getActivity()), null, true);
        fragmentView.setItemsCanFocus(true);
        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Timber.d("onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
        mListener = (SeeUserComuByUserFrListener) getActivity();
        fragmentView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Timber.d("onItemClick()");
                fragmentView.setItemChecked(position, true);
                view.setSelected(true);

                if (mListener != null) {
                    UsuarioComunidad intentUserComuData = (UsuarioComunidad) fragmentView.getItemAtPosition(position);
                    mListener.onUserComuSelected(intentUserComuData, position);
                }
            }
        });
    }

// .......... Interface to communicate with the Activity ...................

    public View getFragmentView()
    {
        Timber.d("getFragmentView()");
        return fragmentView;
    }

//    ============================================================
//    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
//    ============================================================

    public interface SeeUserComuByUserFrListener {
        void onUserComuSelected(UsuarioComunidad userComu, int position);
    }

    class UserComuByUserLoader extends AsyncTask<Void, Void, List<UsuarioComunidad>> {

        UiException uiException;

        @Override
        protected List<UsuarioComunidad> doInBackground(Void... aVoid)
        {
            Timber.d("UserComuByUserLoader.doInBackground()");

            List<UsuarioComunidad> usuarioComunidades = null;
            try {
                usuarioComunidades = userComuDaoRemote.seeUserComusByUser();
            } catch (UiException e) {
                uiException = e;
            }
            return usuarioComunidades;
        }

        @Override
        protected void onPostExecute(List<UsuarioComunidad> usuarioComunidades)
        {
            if (checkPostExecute(getActivity())) return;

            if (uiException != null) {  // action: LOGIN.
                Timber.d("UserComuByUserLoader.onPostExecute(): uiException != null");
                assertTrue(usuarioComunidades == null, usercomu_list_should_be_initialized);
                uiException.processMe(getActivity(), new Intent());
            }
            if (usuarioComunidades != null) {
                Timber.d("UserComuByUserLoader.onPostExecute(): usuarioComunidades != null");
                mAdapter = new SeeUserComuByUserAdapter(getActivity());
                mAdapter.addAll(usuarioComunidades);
                fragmentView.setAdapter(mAdapter);
            }
        }
    }
}
