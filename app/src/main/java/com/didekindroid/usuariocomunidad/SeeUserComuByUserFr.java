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

import com.didekin.usuariocomunidad.UsuarioComunidad;
import com.didekindroid.exception.UiException;
import com.didekindroid.R;

import java.util.List;
import java.util.Objects;

import timber.log.Timber;

import static com.didekindroid.util.UIutils.checkPostExecute;
import static com.didekindroid.usuariocomunidad.UserComuService.AppUserComuServ;

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

    SeeUserComuByUserFrListener mListener;
    public SeeUserComuByUserAdapter mAdapter;
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
//        fragmentView.addHeaderView(new View(getContext()), null, true);
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

    public interface SeeUserComuByUserFrListener {
        void onUserComuSelected(UsuarioComunidad userComu, int position);
    }

//    ============================================================
//    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
//    ============================================================

    public View getFragmentView()
    {
        Timber.d("getFragmentView()");
        return fragmentView;
    }

    class UserComuByUserLoader extends AsyncTask<Void, Void, List<UsuarioComunidad>> {

        UiException uiException;

        @Override
        protected List<UsuarioComunidad> doInBackground(Void... aVoid)
        {
            Timber.d("UserComuByUserLoader.doInBackground()");

            List<UsuarioComunidad> usuarioComunidades = null;
            try {
                usuarioComunidades = AppUserComuServ.seeUserComusByUser();
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
                Objects.equals(usuarioComunidades == null, true);
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
