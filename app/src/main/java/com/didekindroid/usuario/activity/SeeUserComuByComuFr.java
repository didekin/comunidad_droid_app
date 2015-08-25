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

import java.util.List;

/**
 * User: pedro@didekin
 * Date: 25/08/15
 * Time: 16:38
 */
public class SeeUserComuByComuFr extends ListFragment {

    Comunidad mComunidad;

    private static final String TAG = SeeUserComuByComuFr.class.getCanonicalName();

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

    private class UserComuByComuLoader extends AsyncTask<UsuarioComunidad, Void, List<UsuarioComunidad>> {

        private final String TAG = UserComuByComuLoader.class.getCanonicalName();

        @Override
        protected List<UsuarioComunidad> doInBackground(UsuarioComunidad... usuarioComunidad)
        {
            Log.d(TAG, "doInBackground()");
                /*int rowInserted = ServOne.regUserComu(usuarioComunidad[0]);
                if (rowInserted != 1) {
                    Log.e(TAG, getResources().getString(R.string.error_action_in_DB));
                }*/
            return null;
        }

        @Override
        protected void onPostExecute(List<UsuarioComunidad> userComuList)
        {
            Log.d(TAG, "onPostExecute()");
            // añadir lista al adapter.
        }
    }
}
