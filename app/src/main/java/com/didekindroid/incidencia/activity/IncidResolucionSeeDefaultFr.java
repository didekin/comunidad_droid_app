package com.didekindroid.incidencia.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.didekindroid.R;

import java.util.Objects;

import timber.log.Timber;

import static com.didekindroid.util.AppBundleKey.IS_MENU_IN_FRAGMENT_FLAG;
import static com.didekindroid.util.ItemMenu.mn_handler;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.activity.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.util.MenuRouter.routerMap;

/**
 * User: pedro@didekin
 * Date: 13/11/15
 * Time: 15:52
 */
public class IncidResolucionSeeDefaultFr extends Fragment {

    View mFragmentView;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        Timber.d("onAttach()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Timber.d("onCreateView()");
        mFragmentView = inflater.inflate(R.layout.incid_resolucion_see_default_fr, container, false);
        return mFragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Timber.d("onActivityCreated()");
        super.onActivityCreated(savedInstanceState);
        Objects.equals(getArguments().getSerializable(INCID_RESOLUCION_OBJECT.key) == null, true);
        // Activamos el menú.
        setHasOptionsMenu(getArguments().getBoolean(IS_MENU_IN_FRAGMENT_FLAG.key, false));
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
            case R.id.incid_comments_see_ac_mn:
                Intent intent = new Intent();
                intent.putExtra(INCID_IMPORTANCIA_OBJECT.key, getArguments().getSerializable(INCID_IMPORTANCIA_OBJECT.key));
                getActivity().setIntent(intent);
                mn_handler.doMenuItem(getActivity(), routerMap.get(resourceId));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
