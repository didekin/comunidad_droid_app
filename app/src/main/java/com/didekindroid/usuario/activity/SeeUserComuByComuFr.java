package com.didekindroid.usuario.activity;

import android.app.ListFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.VisibleForTesting;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.didekin.serviceone.domain.Comunidad;
import com.didekin.serviceone.domain.UsuarioComunidad;
import com.didekindroid.R;
import com.didekindroid.usuario.activity.utils.UserIntentExtras;

import java.util.List;

import static com.didekindroid.uiutils.ViewsIDs.SEE_USERCOMU_BY_COMU;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;


/**
 * User: pedro@didekin
 * Date: 25/08/15
 * Time: 16:38
 */

/**
 * Preconditions:
 * 1. a long comunidadId is passed as an intent extra.
 */
public class SeeUserComuByComuFr extends ListFragment {

    private static final String TAG = SeeUserComuByComuFr.class.getCanonicalName();

    @VisibleForTesting
    public static final String HEADER = "HEADER";

    SeeUserComuByComuAc mActivity;
    SeeUserComutByComuListAdapter mAdapter;
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

        mActivity = (SeeUserComuByComuAc) getActivity();
        // Preconditions: an existing comunidad passed as intent. The comunidad has necessarily users already signed-up.
        comunidadId = mActivity.getIntent().getExtras().getLong(UserIntentExtras.COMUNIDAD_ID.extra);
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

        fragmentListView = getListView();
        fragmentListView.setId(SEE_USERCOMU_BY_COMU.idView);

        // Header.
        View headerView = getActivity().getLayoutInflater()
                .inflate(R.layout.usercomu_list_header_view, fragmentListView, false);
        nombreComuView = (TextView) headerView.findViewById(R.id.usercomu_list_header_nombrecomu_txt);
        // Adapter.
        mAdapter = new SeeUserComutByComuListAdapter(mActivity);
        // Loading the data...
        new UserComuByComuLoader().execute(comunidadId);
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

    private class UserComuByComuLoader extends AsyncTask<Long, Void, List<UsuarioComunidad>> {

        private final String TAG = UserComuByComuLoader.class.getCanonicalName();

        private Comunidad comunidadIn;

        @Override
        protected List<UsuarioComunidad> doInBackground(Long... comunidadId)
        {
            Log.d(TAG, "doInBackground()");
            comunidadIn = ServOne.getComuData(comunidadId[0]);
            return ServOne.seeUserComusByComu(comunidadId[0]);
        }

        @Override
        protected void onPostExecute(List<UsuarioComunidad> userComuList)
        {
            Log.d(TAG, "onPostExecute(); userComuList size = " + userComuList.size());

            mAdapter.addAll(userComuList);
            setListAdapter(mAdapter);
            nombreComuView.setText(comunidadIn.getNombreComunidad());
            fragmentListView.addHeaderView(nombreComuView);
        }
    }
}