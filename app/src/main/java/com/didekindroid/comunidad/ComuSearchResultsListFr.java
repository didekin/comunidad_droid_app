package com.didekindroid.comunidad;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.didekin.common.exception.ErrorBean;
import com.didekin.comunidad.Comunidad;
import com.didekinaar.R;
import com.didekinaar.exception.UiException;
import com.didekinaar.utils.UIutils;

import java.io.IOException;
import java.util.List;

import timber.log.Timber;

import static com.didekinaar.utils.UIutils.checkPostExecute;
import static com.didekinaar.security.TokenIdentityCacher.TKhandler.isRegisteredUser;
import static com.didekindroid.comunidad.ComunidadService.AppComuServ;
import static com.didekindroid.usuariocomunidad.UserComuMenu.REG_COMU_USERCOMU_AC;
import static com.didekindroid.usuariocomunidad.UserComuMenu.REG_COMU_USER_USERCOMU_AC;

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
        new SearchComunidadesLoader().execute(mComuListListener.getComunidadToSearch());

        mAdapter = new ComuSearchResultsListAdapter(getActivity());
        mListView = (ListView) mView.findViewById(android.R.id.list);
        //TextView for no result.
        mListView.setEmptyView(mView.findViewById(android.R.id.empty));
        mListView.setItemsCanFocus(true);

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
        Comunidad getComunidadToSearch();
        Activity getActivity();
    }

    //    ============================================================
    //    .......... ASYNC TASKS CLASSES AND AUXILIARY METHODS .......
    //    ============================================================

    class SearchComunidadesLoader extends AsyncTask<Comunidad, Void, List<Comunidad>> {

        private UiException uiException;

        @Override
        protected List<Comunidad> doInBackground(Comunidad... comunidades)
        {
            Timber.d("doInBackground()");
            List<Comunidad> comunidadesList = null;
            try {
                comunidadesList = AppComuServ.searchComunidades(comunidades[0]).execute().body();
            } catch (IOException e) {
                uiException = new UiException(ErrorBean.GENERIC_ERROR);
            }
            return comunidadesList;
        }

        @Override
        protected void onPostExecute(List<Comunidad> comunidadList)
        {
            if (checkPostExecute(getActivity())) return;

            Timber.d("onPostExecute(); comunidadList.size = %s%n", comunidadList != null ? String.valueOf(comunidadList.size()) : "null");

            if (uiException != null) {
                Timber.d("onPostExecute(), uiException = %s%n", uiException.getErrorBean().getMessage());
                uiException.processMe(mComuListListener.getActivity(), new Intent());
                return;
            }
            if (comunidadList != null && comunidadList.size() > 0) {
                mAdapter.clear();
                mAdapter.addAll(comunidadList);
                mListView.setAdapter(mAdapter);
            } else {
                UIutils.makeToast(mComuListListener.getActivity(), R.string.no_result_search_comunidad);
                if (TKhandler.isRegisteredUser(mComuListListener.getActivity())) {
                    REG_COMU_USERCOMU_AC.doMenuItem(mComuListListener.getActivity());
                } else {
                    REG_COMU_USER_USERCOMU_AC.doMenuItem(mComuListListener.getActivity());
                }
                mComuListListener.getActivity().finish();
            }
        }
    }
}
