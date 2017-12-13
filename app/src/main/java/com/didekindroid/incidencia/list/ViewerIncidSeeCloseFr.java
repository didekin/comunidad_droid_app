package com.didekindroid.incidencia.list;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.didekindroid.R;
import com.didekindroid.api.CtrlerSelectListIf;
import com.didekindroid.api.ObserverSingleSelectItem;
import com.didekindroid.api.ObserverSingleSelectList;
import com.didekindroid.api.SpinnerEventItemSelectIf;
import com.didekindroid.api.SpinnerEventListener;
import com.didekindroid.api.ViewerSelectList;
import com.didekindroid.incidencia.core.resolucion.IncidResolucionSeeFr;
import com.didekindroid.api.router.FragmentInitiatorIf;
import com.didekindroid.usuariocomunidad.spinner.ViewerComuSpinner;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;

import java.io.Serializable;
import java.util.List;

import timber.log.Timber;

import static com.didekindroid.incidencia.utils.IncidBundleKey.INCIDENCIA_ID_LIST_SELECTED;
import static com.didekindroid.usuariocomunidad.spinner.ViewerComuSpinner.newViewerComuSpinner;
import static com.didekindroid.util.CommonAssertionMsg.item_selected_in_list_should_not_be_zero;
import static com.didekindroid.util.UIutils.assertTrue;

/**
 * User: pedro@didekin
 * Date: 18/03/17
 * Time: 11:01
 *
 * Preconditions:
 * 1. The user is NOW registered in the comunidad whose incidencias are shown.
 * 2. The incidencias shown have been registered in the last 24 months and are closed.
 * 3. All the incidencias closed in a comunidad where the user is NOW registered are shown,
 * even is the user was not registered in the comunidad when incidencia was open or closed.
 * 4. All incidencias closed MUST HAVE a bundleWithResolucion.
 * 5. An intent may be passed with a comunidadId, when a notification is sent when the
 * incidencia has been closed or from a comuSpinner instance in a previous activity or fragment.
 * Postconditions:
 * 1. A list of IncidenciaUSer instances are shown.
 * 2. The incidencias are shown in chronological order, from the most recent to the oldest one.
 * 3. If an incidencia is selected, the bundleWithResolucion data are shown.
 * -- Arguments with incidImportancia, bundleWithResolucion and a toShowMenu flag are passed to the bundleWithResolucion
 * fragment.
 */
public class ViewerIncidSeeCloseFr extends
        ViewerSelectList<ListView, CtrlerSelectListIf<IncidenciaUser>, IncidenciaUser>
        implements SpinnerEventListener, FragmentInitiatorIf<IncidResolucionSeeFr> {

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
        if (!isListClosed){
            return ViewerIncidSeeOpenFr.newViewerIncidSeeOpen(view, activity);
        }
        ViewerIncidSeeCloseFr parentInstance = new ViewerIncidSeeCloseFr(view, activity);
        parentInstance.setController(new CtrlerIncidSeeCloseByComu());
        parentInstance.comuSpinnerViewer = newViewerComuSpinner(view.findViewById(R.id.incid_reg_comunidad_spinner), parentInstance);
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

    /* ==================================  ViewerSelectionIf  =================================*/

    @Override
    public void initSelectedItemId(Bundle savedState)
    {
        Timber.d("initSelectedItemId()");
        if (savedState != null) {
            itemSelectedId = savedState.getLong(INCIDENCIA_ID_LIST_SELECTED.key, 0L);
        }
    }

    @Override
    public int getSelectedPositionFromItemId(long itemSelectedId)
    {
        Timber.d("getSelectedItemId()");
        assertTrue(itemSelectedId > 0, item_selected_in_list_should_not_be_zero);

        // Position set to take account header view in position 0, ...
        int position = view.getHeaderViewsCount();
        boolean isFound = false;
        if (itemSelectedId > 0L) {
            long incidenciaIdIn;
            do {
                incidenciaIdIn = ((IncidenciaUser) view.getItemAtPosition(position)).getIncidencia().getIncidenciaId();
                if (incidenciaIdIn == itemSelectedId) {
                    isFound = true;
                    break;
                }
            } while (++position < view.getCount());
        }
        // Si no encontramos la incidencia, index = 0.
        return isFound ? position : 0;
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
        initReplaceFragmentTx(bundle, IncidResolucionSeeFr.newInstance(bundle));
    }

    void onSuccessLoadItems(List<IncidenciaUser> incidCloseList, ArrayAdapter<IncidenciaUser> adapter)
    {
        Timber.d("onSuccessLoadItems_protected()");
        adapter.addAll(incidCloseList);
        view.setAdapter(adapter);
        view.setEmptyView(emptyListView);
        if (view.getCount() > view.getHeaderViewsCount() && itemSelectedId > 0L) {
            view.setItemChecked(getSelectedPositionFromItemId(itemSelectedId), true);
        }
    }

    // ==================================  FragmentInitiatorIf  =================================

    @Override
    public int getContainerId()
    {
        return R.id.incid_see_by_comu_ac;
    }


    // ==================================  SpinnerEventListener  =================================

    /**
     * This method is called when the comunidades spinner is loaded and one of them selected.
     * It loads the list data.
     *
     * @param spinnerEventItemSelect: comunidad selected in comunidades spinner.
     */
    @Override
    public void doOnClickItemId(SpinnerEventItemSelectIf spinnerEventItemSelect)
    {
        Timber.d("doOnClickItemId()");
        controller.loadItemsByEntitiyId(new ObserverSingleSelectList<>(this), spinnerEventItemSelect.getSpinnerItemIdSelect());
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
            itemSelectedId = incidenciaUser.getIncidencia().getIncidenciaId();
            controller.selectItem(new ObserverSingleSelectItem<>(ViewerIncidSeeCloseFr.this), incidenciaUser);
        }
    }
}