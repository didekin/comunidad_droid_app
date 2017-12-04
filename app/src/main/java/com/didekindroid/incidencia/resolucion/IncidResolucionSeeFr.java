package com.didekindroid.incidencia.resolucion;

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

import com.didekindroid.R;
import com.didekindroid.router.ActivityInitiatorIf;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import timber.log.Timber;

import static com.didekindroid.incidencia.utils.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_OBJECT;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.incidencia.utils.IncidenciaAssertionMsg.resolucion_should_be_initialized;
import static com.didekindroid.util.AppBundleKey.IS_MENU_IN_FRAGMENT_FLAG;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekindroid.util.UIutils.formatTimeStampToString;
import static com.didekindroid.util.UIutils.getStringFromInteger;

/**
 * User: pedro@didekin
 * Date: 13/11/15
 * Time: 15:52
 */
public class IncidResolucionSeeFr extends Fragment implements ActivityInitiatorIf {

    View frView;
    Resolucion resolucion;

    static IncidResolucionSeeFr newInstance(IncidImportancia incidImportancia, Resolucion resolucion)
    {
        Timber.d("newInstance()");
        IncidResolucionSeeFr fr = new IncidResolucionSeeFr();
        Bundle args = new Bundle(1);
        args.putSerializable(INCID_IMPORTANCIA_OBJECT.key, incidImportancia);
        args.putSerializable(INCID_RESOLUCION_OBJECT.key, resolucion);
        fr.setArguments(args);
        return fr;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Timber.d("onCreateView()");
        frView = inflater.inflate(R.layout.incid_resolucion_see_fr, container, false);
        return frView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        Timber.d("onActivityCreated()");
        super.onActivityCreated(savedInstanceState);

        resolucion = (Resolucion) getArguments().getSerializable(INCID_RESOLUCION_OBJECT.key);
        assertTrue(resolucion != null, resolucion_should_be_initialized);
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
                Bundle bundle = new Bundle(1);
                bundle.putSerializable(INCIDENCIA_OBJECT.key, getArguments().getSerializable(INCIDENCIA_OBJECT.key));
                initAcFromMenu(bundle, resourceId);
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
        mAdapter.addAll(resolucion.getAvances());

        // Fecha estimada.
        ((TextView) frView.findViewById(R.id.incid_resolucion_fecha_view)).setText(formatTimeStampToString(resolucion.getFechaPrev()));
        // Coste estimado.
        ((TextView) frView.findViewById(R.id.incid_resolucion_coste_prev_view)).setText(getStringFromInteger(resolucion.getCosteEstimado()));
        // Plan.
        ((TextView) frView.findViewById(R.id.incid_resolucion_txt)).setText(resolucion.getDescripcion());
        // Lista de avances.
        ListView mListView = frView.findViewById(android.R.id.list);
        mListView.setEmptyView(frView.findViewById(android.R.id.empty));
        mListView.setAdapter(mAdapter);
    }
}
