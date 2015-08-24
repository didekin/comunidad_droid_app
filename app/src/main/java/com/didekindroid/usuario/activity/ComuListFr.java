package com.didekindroid.usuario.activity;

import android.app.Activity;
import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.didekindroid.usuario.comunidad.dominio.Comunidad;

import java.util.List;

import static com.didekindroid.common.ui.ViewsIDs.COMUNIDADES_FOUND;
import static com.didekindroid.usuario.common.UserIntentExtras.COMUNIDAD_SEARCH;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;

public class ComuListFr extends ListFragment {

    public static final String TAG = "ComuListFr";

    //The Adapter which will be used to populate the ListView.
    private ComuListAdapter mAdapter;

    // The listener for dealing with the selection event of a line item (comunidad).
    private ComuListListener mComuListListener;

    @Override
    public void onAttach(Activity activity)
    {
        Log.d(TAG, "onAttach()");
        super.onAttach(activity);
        mComuListListener = (ComuListListener) activity;
        mAdapter = new ComuListAdapter(activity);
        Comunidad comunidadSearch = (Comunidad) activity.getIntent()
                .getSerializableExtra(COMUNIDAD_SEARCH.extra);
        new SearchComunidadesLoader().execute(comunidadSearch);
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

        final ListView listView = getListView();

        listView.setId(COMUNIDADES_FOUND.idView);
        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        // Text for no result OR view for no result.
//        setEmptyText(getResources().getText(R.string.no_result_search_comunidad));
//        listView.setEmptyView();

        setListAdapter(mAdapter);
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

        getListView().setItemChecked(position, true); // ¿Necesito ésta o la sig. línea?
        v.setSelected(true);

        if (mComuListListener != null) {
            Comunidad comunidad = (Comunidad) getListView().getItemAtPosition(position);
            mComuListListener.onComunidadSelected(comunidad, position);
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

    public interface ComuListListener {
        void onComunidadSelected(Comunidad comunidad, int lineItemIndex);
        void onComunidadListLoaded(int listSize);
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    private class SearchComunidadesLoader extends AsyncTask<Comunidad, Void, List<Comunidad>> {

        @Override
        protected List<Comunidad> doInBackground(Comunidad... comunidades)
        {
            Log.d(TAG,"SearchComunidadesLoader.doInBackground(); comunidades_parameter.size = " + comunidades.length);
            return ServOne.searchComunidades(comunidades[0]);
        }

        @Override
        protected void onPostExecute(List<Comunidad> comunidadList)
        {
            Log.d(TAG,"SearchComunidadesLoader.onPostExecute(); comunidadList.size = " +
                    (comunidadList != null ? String.valueOf(comunidadList.size()) : "null"));
            mAdapter.addAll(comunidadList);
            mComuListListener.onComunidadListLoaded(comunidadList.size());
        }
    }
}
