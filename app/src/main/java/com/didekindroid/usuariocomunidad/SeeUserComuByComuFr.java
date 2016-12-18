package com.didekindroid.usuariocomunidad;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.didekin.comunidad.Comunidad;
import com.didekin.usuariocomunidad.UsuarioComunidad;
import com.didekinaar.R;
import com.didekindroid.comunidad.ComuBundleKey;
import com.didekinaar.exception.UiException;

import java.util.List;

import timber.log.Timber;

import static com.didekinaar.utils.UIutils.checkPostExecute;
import static com.didekindroid.comunidad.ComunidadService.AppComuServ;


/**
 * User: pedro@didekin
 * Date: 25/08/15
 * Time: 16:38
 */

/**
 * Preconditions:
 * 1. a long comunidadId is passed as an intent key.
 */
public class SeeUserComuByComuFr extends Fragment {

    public SeeUserComuByComuListAdapter mAdapter;
    View mView;
    public ListView fragmentListView;
    public TextView nombreComuView;
    long comunidadId;

    public SeeUserComuByComuFr()
    {
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

        comunidadId = getActivity().getIntent().getExtras().getLong(ComuBundleKey.COMUNIDAD_ID.key);
        // Loading the data...
        new UserComuByComuLoader().execute(comunidadId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Timber.d("onCreateView()");
        mView = inflater.inflate(R.layout.see_usercomu_by_comu_list_fr, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Timber.d("onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

        fragmentListView = (ListView) mView.findViewById(android.R.id.list);
        // To get visible a divider on top of the list.
        fragmentListView.addHeaderView(new View(getContext()), null, true);
        nombreComuView = (TextView) mView.findViewById(R.id.see_usercomu_by_comu_list_header);
    }

    @Override
    public void onStart()
    {
        Timber.d("onStart()");
        super.onStart();
    }

    @Override
    public void onResume()
    {
        Timber.d("onResume()");
        super.onResume();
    }

    @Override
    public void onPause()
    {
        Timber.d("onPause()");
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
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

    View getFragmentView()
    {
        Timber.d("getFragmentView()");
        return fragmentListView;
    }

    class UserComuByComuLoader extends AsyncTask<Long, Void, List<UsuarioComunidad>> {

        private Comunidad comunidadIn;
        private UiException uiException;

        @Override
        protected List<UsuarioComunidad> doInBackground(Long... comunidadId)
        {
            Timber.d("doInBackground()");

            List<UsuarioComunidad> usuarioComunidades = null;
            try {
                comunidadIn = AppComuServ.getComuData(comunidadId[0]);
                usuarioComunidades = UserComuService.AppUserComuServ.seeUserComusByComu(comunidadId[0]);
            } catch (UiException e) {
                uiException = e;
            }
            return usuarioComunidades;
        }

        @Override
        protected void onPostExecute(List<UsuarioComunidad> userComuList)
        {
            if (checkPostExecute(getActivity())) return;

            Timber.d("onPostExecute(); userComuList size = %d%n", userComuList.size());

            if (uiException != null) {
                uiException.processMe(getActivity(), new Intent());
            } else {
                // Adapter.
                mAdapter = new SeeUserComuByComuListAdapter(SeeUserComuByComuFr.this.getActivity());
                mAdapter.addAll(userComuList);
                fragmentListView.setAdapter(mAdapter);
                nombreComuView.setText(comunidadIn.getNombreComunidad());
            }
        }
    }
}
