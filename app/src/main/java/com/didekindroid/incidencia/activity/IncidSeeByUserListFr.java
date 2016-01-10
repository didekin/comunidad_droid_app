package com.didekindroid.incidencia.activity;

import android.app.ListFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.didekin.incidservice.domain.IncidUserComu;
import com.didekindroid.R;
import com.didekindroid.common.UiException;
import com.didekindroid.common.utils.ViewsIDs;

import java.util.List;

import static com.didekindroid.incidencia.webservices.IncidService.IncidenciaServ;
import static com.google.common.base.Preconditions.checkState;

/**
 * Preconditions:
 * <p/>
 * Postconditions:
 */
public class IncidSeeByUserListFr extends ListFragment {

    public static final String TAG = IncidSeeByUserListFr.class.getCanonicalName();

    //The Adapter which will be used to populate the ListView.
    IncidSeeByUserAdapter mAdapter;
    // The listener for dealing with the selection event of a line item (comunidad).
    IncidListListener mListener;
    ListView mView;

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

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Log.d(TAG, "onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

        mListener = (IncidListListener) getActivity();
        mAdapter = new IncidSeeByUserAdapter(getActivity());
        // Loading data ...
//        new IncidByUserComuLoader().execute();

        mView = getListView();
        mView.setId(ViewsIDs.INCID_SEE_BY_USER.idView);
        mView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        //Text for no result.
        setEmptyText(getResources().getText(R.string.no_incidencia_by_user));
        //View for no result
        /*mView.setEmptyView(myView);*/
    }

    @Override
    public void onStart()
    {
        Log.d(TAG, "Enters onStart()");
        super.onStart();
    }

    @Override
    public void onResume()
    {
        Log.d(TAG, "Enters onResume()");
        super.onResume();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        Log.d(TAG, "onListItemClick()");

        mView.setItemChecked(position, true);
        v.setSelected(true);
        if (mListener != null) {
            IncidUserComu incidUserComu = (IncidUserComu) mView.getItemAtPosition(position);
            mListener.onIncidenciaSelected(incidUserComu, position);
        }
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

    // .......... Interface to communicate with the Activity ...................

    public interface IncidListListener {
        void onIncidenciaSelected(IncidUserComu incidencia, int position);
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    class IncidByUserComuLoader extends AsyncTask<Void, Void, List<IncidUserComu>> {

        private final String TAG = IncidByUserComuLoader.class.getCanonicalName();
        UiException uiException;

        @Override
        protected List<IncidUserComu> doInBackground(Void... aVoid)
        {
            Log.d(TAG, "doInBackground()");
            List<IncidUserComu> incidUserComuList = null;
            try {
                incidUserComuList = IncidenciaServ.incidSeeByUser();
            } catch (UiException e) {
                uiException = e;
            }
            return incidUserComuList;
        }

        @Override
        protected void onPostExecute(List<IncidUserComu> incidUserComuList)
        {
            Log.d(TAG, "onPostExecute()");
            if (incidUserComuList != null && incidUserComuList.size() > 0) {
                Log.d(TAG, "onPostExecute(): incidUserComuList != null");
                mAdapter.addAll(incidUserComuList);
                setListAdapter(mAdapter);
            }
            if (uiException != null) {  // action: LOGIN.
                Log.d(TAG, "onPostExecute(): uiException != null");
                checkState(incidUserComuList == null);
                uiException.getAction().doAction(getActivity(), uiException.getResourceId());
            }
        }
    }
}
