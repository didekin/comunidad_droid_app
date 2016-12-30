package com.didekindroid.incidencia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.didekin.incidencia.dominio.Resolucion;
import com.didekindroid.R;

import java.util.Objects;

import timber.log.Timber;

import static com.didekinaar.utils.AarBundleKey.IS_MENU_IN_FRAGMENT_FLAG;
import static com.didekinaar.utils.AarItemMenu.mn_handler;
import static com.didekinaar.utils.UIutils.formatTimeStampToString;
import static com.didekinaar.utils.UIutils.getStringFromInteger;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.util.AppMenuRouter.routerMap;

/**
 * User: pedro@didekin
 * Date: 13/11/15
 * Time: 15:52
 */
public class IncidResolucionSeeFr extends Fragment {

    View mFragmentView;
    Resolucion mResolucion;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Timber.d("onCreateView()");
        mFragmentView = inflater.inflate(R.layout.incid_resolucion_see_fr, container, false);
        return mFragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Timber.d("onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

        mResolucion = (Resolucion) getArguments().getSerializable(INCID_RESOLUCION_OBJECT.key);
        Objects.equals(mResolucion != null, true);
        // Activamos el menú.
        setHasOptionsMenu(getArguments().getBoolean(IS_MENU_IN_FRAGMENT_FLAG.key, false));
        paintViewData();
    }

    // ============================================================
    //    ..... ACTION BAR ....
    // ============================================================

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        Timber.d("onCreateOptionsMenu()");
        inflater.inflate(R.menu.incid_see_closed_fragments_mn, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected()");

        int resourceId = item.getItemId();

        switch (resourceId) {
            case android.R.id.home:
                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack();
                }
                return true;
            case R.id.incid_comments_see_ac_mn:
                Intent intent = new Intent();
                intent.putExtra(INCIDENCIA_OBJECT.key, getArguments().getSerializable(INCIDENCIA_OBJECT.key));
                getActivity().setIntent(intent);
                mn_handler.doMenuItem(getActivity(), routerMap.get(resourceId));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

//  ............................. HELPER METHODS ......................................

    protected void paintViewData()
    {
        IncidAvanceSeeAdapter mAdapter = new IncidAvanceSeeAdapter(getActivity());
        mAdapter.clear();
        mAdapter.addAll(mResolucion.getAvances());

        // Fecha estimada.
        ((TextView) mFragmentView.findViewById(R.id.incid_resolucion_fecha_view)).setText(formatTimeStampToString(mResolucion.getFechaPrev()));
        // Coste estimado.
        ((TextView) mFragmentView.findViewById(R.id.incid_resolucion_coste_prev_view)).setText(getStringFromInteger(mResolucion.getCosteEstimado()));
        // Plan.
        ((TextView) mFragmentView.findViewById(R.id.incid_resolucion_txt)).setText(mResolucion.getDescripcion());
        // Lista de avances.
        ListView mListView = (ListView) mFragmentView.findViewById(android.R.id.list);
        mListView.setEmptyView(mFragmentView.findViewById(android.R.id.empty));
        mListView.setAdapter(mAdapter);
    }
}
