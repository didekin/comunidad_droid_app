package com.didekindroid.incidencia.core.resolucion;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.didekindroid.lib_one.api.router.MnRouterIf;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.Resolucion;

import timber.log.Timber;

import static com.didekindroid.incidencia.IncidBundleKey.INCIDENCIA_OBJECT;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_RESOLUCION_OBJECT;
import static com.didekindroid.incidencia.IncidenciaAssertionMsg.resolucion_should_be_initialized;
import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.util.UiUtil.assertTrue;
import static com.didekindroid.lib_one.util.UiUtil.formatTimeStampToString;
import static com.didekindroid.lib_one.util.UiUtil.getStringFromInteger;
import static java.util.Objects.requireNonNull;

/**
 * User: pedro@didekin
 * Date: 13/11/15
 * Time: 15:52
 */
public class IncidResolucionSeeFr extends Fragment {

    View frView;
    Resolucion resolucion;

    static IncidResolucionSeeFr newInstance(Incidencia incidencia, Resolucion resolucion)
    {
        Timber.d("newInstance()");
        IncidResolucionSeeFr fr = new IncidResolucionSeeFr();
        Bundle args = new Bundle(1);
        args.putSerializable(INCIDENCIA_OBJECT.key, incidencia);
        args.putSerializable(INCID_RESOLUCION_OBJECT.key, resolucion);
        fr.setArguments(args);
        return fr;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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

        resolucion = (Resolucion) requireNonNull(getArguments()).getSerializable(INCID_RESOLUCION_OBJECT.key);
        assertTrue(resolucion != null, resolucion_should_be_initialized);
        // Activamos el menú.
        setHasOptionsMenu(true);
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

        MnRouterIf mnRouter = routerInitializer.get().getMnRouter();
        int resourceId = item.getItemId();

        switch (resourceId) {
            case android.R.id.home:
                mnRouter.getActionFromMnItemId(resourceId).initActivity(requireNonNull(getActivity()));
                return true;
            case R.id.incid_comments_see_ac_mn:
                mnRouter.getActionFromMnItemId(resourceId)
                        .initActivity(
                                requireNonNull(getActivity()),
                                INCIDENCIA_OBJECT.getBundleForKey(requireNonNull(getArguments()).getSerializable(INCIDENCIA_OBJECT.key))
                        );
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //  ............................. HELPER METHODS ......................................

    protected void paintViewData()
    {
        // Fecha estimada.
        ((TextView) frView.findViewById(R.id.incid_resolucion_fecha_view)).setText(formatTimeStampToString(resolucion.getFechaPrev()));
        // Coste estimado.
        ((TextView) frView.findViewById(R.id.incid_resolucion_coste_prev_view)).setText(getStringFromInteger(resolucion.getCosteEstimado()));
        // Plan.
        ((TextView) frView.findViewById(R.id.incid_resolucion_txt)).setText(resolucion.getDescripcion());

        // Lista de avances.
        ListView listView = frView.findViewById(android.R.id.list);
        if (resolucion.getAvances() != null && resolucion.getAvances().size() > 0) {
            IncidAvanceSeeAdapter adapter = new IncidAvanceSeeAdapter(getActivity());
            adapter.clear();
            adapter.addAll(resolucion.getAvances());
            listView.setAdapter(adapter);
        } else {
            listView.setEmptyView(frView.findViewById(android.R.id.empty));
        }
    }
}
