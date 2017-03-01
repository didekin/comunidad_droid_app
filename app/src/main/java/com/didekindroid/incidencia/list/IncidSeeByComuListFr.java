package com.didekindroid.incidencia.list;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf;
import com.didekindroid.incidencia.list.ManagerIncidSeeIf.ControllerIncidSeeIf;
import com.didekindroid.incidencia.list.ManagerIncidSeeIf.ViewerIncidSeeIf;
import com.didekindroid.incidencia.spinner.ManagerComuSpinnerIf;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.incidencia.dominio.Incidencia;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;

import timber.log.Timber;

import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.spinner.ViewerComuSpinner.newComuSpinnerViewer;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCIDENCIA_LIST_INDEX;

/**
 * Preconditions:
 * A list of IncidenciaUser instances is retrieved with the incidencia and the registering user data.
 * <p/>
 * Postconditions:
 */
public class IncidSeeByComuListFr<B> extends Fragment implements ViewerIncidSeeIf<B>, ManagerComuSpinnerIf<B> {

    ControllerIncidSeeIf controller;
    // The fragment itself.
    ViewerIncidSeeIf<B> seeIncidByComuViewer;
    ManagerIncidSeeIf<B> incidListManager;

    // The fragment itself.
    ManagerComuSpinnerIf<Object> comuSpinnerManager;
    ViewerComuSpinnerIf comuSpinnerViewer;

    View rootFrgView;
    ListView listView;
    int incidenciaSelectedIndex;

    /**
     * This index can be set in three ways:
     * 1. The user selects one item in the spinner.
     * 2. The index is retrieved from savedInstanceState.
     * 3. The index is passed from the activity (in FCM notifications).
     */
    int comunidadSelectedIndex;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedState)
    {
        Timber.d("onCreateView()");

        rootFrgView = inflater.inflate(R.layout.incid_see_generic_fr_layout, container, false);
        // Initialization of viewers and views.
        comuSpinnerViewer = newComuSpinnerViewer(comuSpinnerManager).initSelectedIndex(savedState);
        seeIncidByComuViewer = this.initSelectedIndex(savedState);
        return rootFrgView;
    }

    @Override
    public void onActivityCreated(Bundle savedState)
    {
        Timber.d("onActivityCreated()");
        super.onActivityCreated(savedState);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onStart(){
        super.onStart();
        // Initialization of managers and controllers.
        incidListManager = (ManagerIncidSeeIf<B>) getActivity();
        controller = incidListManager.getController();
        comuSpinnerManager = (ManagerComuSpinnerIf<Object>) this;
        comuSpinnerViewer.setDataInView();
        seeIncidByComuViewer.doIncidListView();
    }

    @Override
    public void onSaveInstanceState(Bundle savedState)
    {
        Timber.d("onSaveInstanceState()");
        seeIncidByComuViewer.saveSelectedIndex(savedState);
        comuSpinnerViewer.saveSelectedIndex(savedState);
        super.onSaveInstanceState(savedState);
    }

    @Override
    public void onStop()
    {
        Timber.d("onStop()");
        super.onStop();
        // Clear subscriptions in controller and viewers' controllers.
        clearControllerSubscriptions();
    }

//    ============================================================================
//    ....................... COMUNIDAD SPINNER MANAGER .........................
//    ============================================================================

    @Override
    public long getComunidadIdInIntent()
    {
        Timber.d("getComunidadIdInIntent()");
        return getActivity().getIntent().getLongExtra(COMUNIDAD_ID.key, 0);
    }

    @Override
    public UiExceptionIf.ActionForUiExceptionIf processViewerError(UiException ui)
    {
        Timber.d("processViewerError()");
        return incidListManager.processViewerError(ui);
    }

    @Override
    public void replaceRootView(Object initParamsForView)
    {
        Timber.d("replaceRootView()");
        throw new UnsupportedOperationException();
    }

    @Override
    public Spinner initSpinnerView()
    {
        Timber.d("initSpinnerView()");

        Spinner spinnerToInit = getSpinnerViewInManager();
        spinnerToInit.setOnItemSelectedListener(getSpinnerListener());
        return spinnerToInit;
    }

    @Override
    public Spinner getSpinnerViewInManager()
    {
        return (Spinner) rootFrgView.findViewById(R.id.incid_reg_comunidad_spinner);
    }

    @Override
    public AdapterView.OnItemSelectedListener getSpinnerListener()
    {
        return new ComuSelectedListener();
    }

    // ...................... HELPERS ....................

    class ComuSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            Timber.d("comunidadSpinner.onItemSelected()");
            comunidadSelectedIndex = position;
            Comunidad comunidad = (Comunidad) parent.getItemAtPosition(position);
            controller.loadIncidsByComu(comunidad.getC_Id());
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {
            Timber.d("comunidadSpinner.onNothingSelected()");
        }
    }

//    ============================================================================
//    ......................... INCIDENCIAS LIST VIEWER ..........................
//    ============================================================================

    @Override
    public void doIncidListView()
    {
        Timber.d("doIncidListView()");

        listView = (ListView) rootFrgView.findViewById(android.R.id.list);
        // To get visible a divider on top of the list.
        listView.addHeaderView(new View(getContext()), null, true);
        listView.setEmptyView(rootFrgView.findViewById(android.R.id.empty));
        listView.setSelection(incidenciaSelectedIndex);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Timber.d("onListItemClick()");
                listView.setItemChecked(position, true);
                view.setSelected(true);
                Incidencia incidencia = ((IncidenciaUser) listView.getItemAtPosition(position)).getIncidencia();
                incidenciaSelectedIndex = position;
                controller.dealWithIncidSelected(incidencia);
            }
        });
    }

    @Override
    public void replaceView(B initParams)
    {
        incidListManager.replaceRootView(initParams);
    }

    public ListView getViewInViewer()
    {
        Timber.d("getListView()");
        return listView;
    }

    @Override
    public UiExceptionIf.ActionForUiExceptionIf processControllerError(UiException ui)
    {
        Timber.d("processControllerError()");
        return incidListManager.processViewerError(ui);
    }

    @Override
    public int clearControllerSubscriptions()
    {
        Timber.d("clearControllerSubscriptions()");
        return controller.clearSubscriptions()
                + comuSpinnerViewer.clearControllerSubscriptions();
    }

    @Override
    public ViewerIncidSeeIf<B> initSelectedIndex(Bundle savedInstanceState)
    {
        Timber.d("initIncidenciaSelectedIndex()");
        if (savedInstanceState != null) {
            incidenciaSelectedIndex = savedInstanceState.getInt(INCIDENCIA_LIST_INDEX.key, 0);
        }
        return this;
    }

    @Override
    public void saveSelectedIndex(Bundle savedState)
    {
        Timber.d("saveIncidSelectedIndex()");
        savedState.putInt(INCIDENCIA_LIST_INDEX.key, incidenciaSelectedIndex);
    }
}
