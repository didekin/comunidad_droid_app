package com.didekindroid.usuario.activity;

import android.app.ListFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.didekindroid.R;
import com.didekindroid.usuario.dominio.Comunidad;
import com.didekindroid.usuario.dominio.UsuarioComunidad;
import com.didekindroid.usuario.webservices.ServiceOne;

import java.util.List;

import static com.didekindroid.common.ui.ViewsIDs.SEE_USERCOMU_BY_COMU;
import static com.didekindroid.usuario.common.UserIntentExtras.COMUNIDAD_LIST_OBJECT;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;


/**
 * User: pedro@didekin
 * Date: 25/08/15
 * Time: 16:38
 */
public class SeeUserComuByComuFr extends ListFragment {

    private static final String TAG = SeeUserComuByComuFr.class.getCanonicalName();

    SeeUserComuByComuAc mActivity;
    UserComuListByComuAdapter mAdapter;

    public SeeUserComuByComuFr()
    {
    }

    @Override
    public void onAttach(Context context)
    {
        Log.d(TAG, "onAttach()");
        super.onAttach(context);
        mActivity = (SeeUserComuByComuAc) context;
        mAdapter = new UserComuListByComuAdapter(context);

        // Preconditions: an existing comunidad passed as intent. The comunidad has necessarily users already signed-up.
        Comunidad mComunidad = (Comunidad) mActivity.getIntent().getExtras()
                .getSerializable(COMUNIDAD_LIST_OBJECT.extra);
        new UserComuByComuLoader().execute(mComunidad);
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
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public View getFragmentView()
    {
        Log.d(TAG, "getFragmentView()");
        return getListView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Log.d(TAG, "onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
        getListView().setId(SEE_USERCOMU_BY_COMU.idView);
        setListAdapter(mAdapter);
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

    private class UserComuByComuLoader extends AsyncTask<Comunidad, Void, List<UsuarioComunidad>> {

        private final String TAG = UserComuByComuLoader.class.getCanonicalName();

        @Override
        protected List<UsuarioComunidad> doInBackground(Comunidad... comunidad)
        {
            Log.d(TAG, "doInBackground()");
            return ServOne.seeUserComuByComu(comunidad[0].getC_Id());
        }

        @Override
        protected void onPostExecute(List<UsuarioComunidad> userComuList)
        {
            Log.d(TAG, "onPostExecute()");
            mAdapter.addAll(userComuList);
        }
    }
}
