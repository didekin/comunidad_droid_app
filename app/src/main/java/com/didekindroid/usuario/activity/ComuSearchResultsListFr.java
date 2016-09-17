package com.didekindroid.usuario.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.didekin.usuario.dominio.Comunidad;
import com.didekindroid.R;

import java.util.List;

import timber.log.Timber;

/**
 * Preconditions:
 * <p/>
 * 1. Every comunidad in the list supplied to the adapter has the following fields:
 * -- comunidadId.
 * -- nombreComunidad (composed internally with tipoVia,nombreVia, numero and sufijoNumero).
 * -- municipio, with codInProvincia and nombre.
 * -- provincia, with provinciaId and nombre.
 * <p/>
 * 2. An object comunidad, used as search criterium, is received as an intent key with the following fields:
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
public class ComuSearchResultsListFr extends Fragment {

    ComuSearchResultsListAdapter mAdapter;
    /**
     * The listener for dealing with the selection event of a line item (comunidad).
     */
    ComuListListener mComuListListener;
    ListView mListView;
    View mView;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Timber.d("onCreateView()");
        mView = inflater.inflate(R.layout.comu_search_results_fr_layout, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Timber.d("onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

        mComuListListener = (ComuListListener) getActivity();
        mAdapter = new ComuSearchResultsListAdapter(getActivity());
        mAdapter.addAll(mComuListListener.getResultsList());

        mListView = (ListView) mView.findViewById(android.R.id.list);
        mListView.setItemsCanFocus(true);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Timber.d("onListItemClick()");

                mListView.setItemChecked(position, true);
                view.setSelected(true);

                if (mComuListListener != null) {
                    Comunidad comunidad = (Comunidad) mListView.getItemAtPosition(position);
                    mComuListListener.onComunidadSelected(comunidad, position);
                }
            }
        });
    }

    @Override
    public void onStart()
    {
        Timber.d("Enters onStart()");
        super.onStart();
    }

    @Override
    public void onResume()
    {
        Timber.d("Enters onResume()");
        super.onResume();
    }

    @Override
    public void onPause()
    {
        Timber.d("onPause()");
        super.onPause();
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

    // .......... Interface to communicate with the Activity ...................

    public interface ComuListListener {
        void onComunidadSelected(Comunidad comunidad, int lineItemIndex);
        List<Comunidad> getResultsList();
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================


}
