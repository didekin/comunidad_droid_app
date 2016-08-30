package com.didekindroid.usuario.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.didekin.usuario.dominio.Comunidad;
import com.didekin.usuario.dominio.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.common.activity.BundleKey;
import com.didekindroid.common.activity.UiException;

import java.util.List;

import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;


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

    private static final String TAG = SeeUserComuByComuFr.class.getCanonicalName();

    SeeUserComutByComuListAdapter mAdapter;
    View mView;
    ListView fragmentListView;
    TextView nombreComuView;
    long comunidadId;

    public SeeUserComuByComuFr()
    {
    }

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

        comunidadId = getActivity().getIntent().getExtras().getLong(BundleKey.COMUNIDAD_ID.key);
        // Loading the data...
        new UserComuByComuLoader().execute(comunidadId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView()");
        mView = inflater.inflate(R.layout.see_usercomu_by_comu_list_fr, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Log.d(TAG, "onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

        fragmentListView = (ListView) mView.findViewById(android.R.id.list);
        // To get visible a divider on top of the list.
        fragmentListView.addHeaderView(new View(getContext()), null, true);
        nombreComuView = (TextView) mView.findViewById(R.id.see_usercomu_by_comu_list_header);
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


//    ============================================================
//    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
//    ============================================================

    View getFragmentView()
    {
        Log.d(TAG, "getFragmentView()");
        return fragmentListView;
    }

    class UserComuByComuLoader extends AsyncTask<Long, Void, List<UsuarioComunidad>> {

        private final String TAG = UserComuByComuLoader.class.getCanonicalName();

        private Comunidad comunidadIn;
        private UiException uiException;

        @Override
        protected List<UsuarioComunidad> doInBackground(Long... comunidadId)
        {
            Log.d(TAG, "doInBackground()");

            List<UsuarioComunidad> usuarioComunidades = null;
            try {
                comunidadIn = ServOne.getComuData(comunidadId[0]);
                usuarioComunidades = ServOne.seeUserComusByComu(comunidadId[0]);
            } catch (UiException e) {
                uiException = e;
            }
            return usuarioComunidades;
        }

        @Override
        protected void onPostExecute(List<UsuarioComunidad> userComuList)
        {
            Log.d(TAG, "onPostExecute(); userComuList size = " + userComuList.size());

            if (uiException != null) {
                uiException.processMe(getActivity(), new Intent());
            } else {
                // Adapter.
                mAdapter = new SeeUserComutByComuListAdapter(SeeUserComuByComuFr.this.getActivity());
                mAdapter.addAll(userComuList);
                fragmentListView.setAdapter(mAdapter);
                nombreComuView.setText(comunidadIn.getNombreComunidad());
            }
        }
    }
}
