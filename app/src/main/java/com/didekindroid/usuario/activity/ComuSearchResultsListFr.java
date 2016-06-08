package com.didekindroid.usuario.activity;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.didekin.common.exception.ErrorBean;
import com.didekin.usuario.dominio.Comunidad;
import com.didekindroid.common.activity.UiException;

import java.io.IOException;
import java.util.List;

import static com.didekindroid.common.activity.BundleKey.COMUNIDAD_SEARCH;
import static com.didekindroid.common.activity.ViewsIDs.COMU_SEARCH_RESULTS;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;

/**
 * Preconditions:
 * 1. An intent extra with a comunidad object encapsulating the comunidad to search is received.
 * <p/>
 * 2. Every comunidad in the list supplied to the adapter has the following fields:
 * -- comunidadId.
 * -- nombreComunidad (composed internally with tipoVia,nombreVia, numero and sufijoNumero).
 * -- municipio, with codInProvincia and nombre.
 * -- provincia, with provinciaId and nombre.
 * <p/>
 * 3. An object comunidad, used as search criterium, is received as an intent key with the following fields:
 * -- tipoVia.
 * -- nombreVia.
 * -- numero.
 * -- sufijoNumero (it can be an empty string).
 * -- municipio with codInProvincia and provinciaId.
 * <p/>
 * Postconditions:
 * <p/>
 * 1. An object comunidad is passed to the listener activity with the fields:
 * -- comunidadId.
 * -- nombreComunidad (with tipoVia,nombreVia, numero and sufijoNumero).
 * -- municipio, with codInProvincia and nombre.
 * -- provincia, with provinciaId and nombre.
 */
public class ComuSearchResultsListFr extends ListFragment {

    public static final String TAG = ComuSearchResultsListFr.class.getCanonicalName();

    //The Adapter which will be used to populate the ListView.
    private ComuSearchResultsListAdapter mAdapter;

    // The listener for dealing with the selection event of a line item (comunidad).
    private ComuListListener mComuListListener;

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

        mComuListListener = (ComuListListener) getActivity();
        mAdapter = new ComuSearchResultsListAdapter(getActivity());
        Comunidad comunidad = (Comunidad) getActivity().getIntent()
                .getSerializableExtra(COMUNIDAD_SEARCH.key);
        new SearchComunidadesLoader().execute(comunidad);

        final ListView listView = getListView();

        listView.setId(COMU_SEARCH_RESULTS.idView);
        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        /* Text for no result OR view for no result.
        setEmptyText(getResources().getText(R.string.no_result_search_comunidad));
        listView.setEmptyView();   */
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

        getListView().setItemChecked(position, true);
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

    class SearchComunidadesLoader extends AsyncTask<Comunidad, Void, List<Comunidad>> {

        private final String TAG = SearchComunidadesLoader.class.getCanonicalName();
        private UiException uiException;

        @Override
        protected List<Comunidad> doInBackground(Comunidad... comunidades)
        {
            Log.d(TAG, "doInBackground()");
            List<Comunidad> comunidadesList = null;
            try {
                comunidadesList = ServOne.searchComunidades(comunidades[0]).execute().body();
            } catch (IOException e) {
               uiException = new UiException(ErrorBean.GENERIC_ERROR);
            }
            return comunidadesList;
        }

        @Override
        protected void onPostExecute(List<Comunidad> comunidadList)
        {
            Log.d(TAG, "onPostExecute(); comunidadList.size = " +
                    (comunidadList != null ? String.valueOf(comunidadList.size()) : "null"));

            if (uiException != null) {
                uiException.processMe(getActivity(), new Intent());
                return;
            }
            mAdapter.addAll(comunidadList);
            setListAdapter(mAdapter);
            mComuListListener.onComunidadListLoaded(comunidadList != null ? comunidadList.size() : 0);
        }
    }
}
