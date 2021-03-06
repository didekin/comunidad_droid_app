package com.didekindroid.incidencia.list;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.CtrlerSelectListIf;
import com.didekindroid.lib_one.api.ObserverSingleSelectItem;
import com.didekindroid.lib_one.api.ObserverSingleSelectList;
import com.didekindroid.lib_one.api.SpinnerEventItemSelectIf;
import com.didekindroid.lib_one.api.SpinnerEventListener;
import com.didekindroid.lib_one.api.ViewerSelectList;
import com.didekindroid.usuariocomunidad.spinner.ViewerComuSpinner;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;

import java.io.Serializable;
import java.util.List;

import io.reactivex.functions.Function;
import timber.log.Timber;

import static com.didekindroid.incidencia.IncidBundleKey.INCIDENCIA_ID_LIST_SELECTED;
import static com.didekindroid.incidencia.IncidContextualName.incid_closed_just_selected;
import static com.didekindroid.usuariocomunidad.spinner.ViewerComuSpinner.newViewerComuSpinner;

/**
 * User: pedro@didekin
 * Date: 18/03/17
 * Time: 11:01
 * <p>
 * Preconditions:
 * 1. The user is NOW registered in the comunidad whose incidencias are shown.
 * 2. The incidencias shown have been registered in the last 24 months and are closed.
 * 3. All the incidencias closed in a comunidad where the user is NOW registered are shown,
 * even is the user was not registered in the comunidad when incidencia was open or closed.
 * 4. All incidencias closed MUST HAVE a resolucion.
 * 5. An intent may be passed with a comunidadId, when a notification is sent when the
 * incidencia has been closed or from a comuSpinner instance in a previous activity or fragment.
 * Postconditions:
 * 1. A list of IncidenciaUSer instances are shown.
 * 2. The incidencias are shown in chronological order, from the most recent to the oldest one.
 * 3. If an incidencia is selected, the resolucion data are shown.
 */
public class ViewerIncidSeeCloseFr extends
        ViewerSelectList<ListView, CtrlerSelectListIf<IncidenciaUser>, IncidenciaUser>
        implements SpinnerEventListener {

    ViewerComuSpinner comuSpinnerViewer;
    private View emptyListView;

    ViewerIncidSeeCloseFr(View frView, AppCompatActivity activity)
    {
        super(frView.findViewById(android.R.id.list), activity, null);
        emptyListView = frView.findViewById(android.R.id.empty);
        // To get visible a divider on top of the list: true.
        view.addHeaderView(new View(activity), null, true);
    }

    static ViewerIncidSeeCloseFr newViewerIncidSeeClose(View view, AppCompatActivity activity, boolean isListClosed)
    {
        Timber.d("newViewerIncidSeeClose()");
        if (!isListClosed) {
            return ViewerIncidSeeOpenFr.newViewerIncidSeeOpen(view, activity);
        }
        ViewerIncidSeeCloseFr parentInstance = new ViewerIncidSeeCloseFr(view, activity);
        parentInstance.setController(new CtrlerIncidSeeCloseByComu());
        parentInstance.comuSpinnerViewer = newViewerComuSpinner(view.findViewById(R.id.incid_comunidad_spinner), parentInstance);
        return parentInstance;
    }

    ViewerComuSpinner getComuSpinner()
    {
        Timber.d("getComuSpinner()");
        return comuSpinnerViewer;
    }

    // ==================================  VIEWER  =================================

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        initSelectedItemId(savedState);
        comuSpinnerViewer.doViewInViewer(savedState, viewBean);
        view.setOnItemClickListener(new ListItemOnClickListener());
    }

    @Override
    public int clearSubscriptions()
    {
        Timber.d("clearSubscriptions()");
        return comuSpinnerViewer.clearSubscriptions()
                + controller.clearSubscriptions();
    }

    @Override
    public void saveState(Bundle savedState)
    {
        Timber.d("saveState()");
        if (savedState == null) {
            savedState = new Bundle();
        }
        savedState.putLong(INCIDENCIA_ID_LIST_SELECTED.key, itemSelectedId);
        comuSpinnerViewer.saveState(savedState);
    }

    /* ==================================  ViewerSelectedListIf  =================================*/

    @Override
    public void initSelectedItemId(Bundle savedState)
    {
        Timber.d("initSelectedItemId()");
        if (savedState != null) {
            itemSelectedId = savedState.getLong(INCIDENCIA_ID_LIST_SELECTED.key, 0L);
        }
    }

    @Override
    public Function<IncidenciaUser, Long> getBeanIdFunction()
    {
        return incidenciaUser ->  incidenciaUser != null ? incidenciaUser.getIncidencia().getIncidenciaId() : 0L;
    }

    /**
     * comunidadesSpinner.doViewInViewer() --> comunidadesSpinner.loadItemsByEntitiyId() --> onSuccessLoadItemList()
     * --> view.setSelection() --> ComuSelectedListener --> onItemSelected() --> SpinnerEventListener.doOnClickItemId().
     * <p>
     * This method is called after doOnClickItemId() --> controller.loadItemsByEntitiyId() are executed.
     */
    @Override
    public void onSuccessLoadItemList(List<IncidenciaUser> itemsList)
    {
        Timber.d("onSuccessLoadItemList()");
        onSuccessLoadItems(itemsList, new AdapterIncidSeeClosedByComu(activity));
    }

    @Override
    public void onSuccessLoadSelectedItem(@NonNull Bundle bundle)
    {
        Timber.d("onSuccessLoadSelectedItem()");
        getContextualRouter().getActionFromContextNm(incid_closed_just_selected).initActivity(activity, bundle);
    }

    void onSuccessLoadItems(List<IncidenciaUser> incidCloseList, ArrayAdapter<IncidenciaUser> adapter)
    {
        Timber.d("onSuccessLoadItems_protected()");
        adapter.addAll(incidCloseList);
        view.setAdapter(adapter);
        view.setEmptyView(emptyListView);
        if (view.getCount() > view.getHeaderViewsCount() && itemSelectedId > 0L) {
            view.setItemChecked(getSelectedPositionFromItemId(getBeanIdFunction()), true);
        }
    }

    // ==================================  SpinnerEventListener  =================================

    /**
     * This method is called when the comunidades spinner is loaded and one of them selected.
     * It loads the list data.
     *
     * @param spinnerEventItemSelect: comunidad selected in comunidades spinner.
     */
    @Override
    public void doOnClickItemId(@NonNull SpinnerEventItemSelectIf spinnerEventItemSelect)
    {
        Timber.d("doOnClickItemId()");
        long comunidadIdInSpinner = spinnerEventItemSelect.getSpinnerItemIdSelect();

        controller.loadItemsByEntitiyId(new ObserverSingleSelectList<>(this), comunidadIdInSpinner);
    }

    // ==================================  HELPERS  =================================

    @SuppressWarnings("WeakerAccess")
    public class ListItemOnClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View viewClick, int position, long id)
        {
            Timber.d("onItemClick()");
            // List header increases position in 1: no problems with that, because we use itemSelectedId in saveState.
            view.setItemChecked(position, true);
            viewClick.setSelected(true);
            IncidenciaUser incidenciaUser = (IncidenciaUser) view.getItemAtPosition(position);
            try {
                itemSelectedId = getBeanIdFunction().apply(incidenciaUser);
            } catch (Exception e) {
                Timber.e(e);
            }
            controller.selectItem(new ObserverSingleSelectItem<>(ViewerIncidSeeCloseFr.this), incidenciaUser);
        }
    }
}