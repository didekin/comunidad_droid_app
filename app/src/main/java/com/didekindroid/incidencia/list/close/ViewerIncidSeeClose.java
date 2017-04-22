package com.didekindroid.incidencia.list.close;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.didekindroid.R;
import com.didekindroid.api.CtrlerSelectableItemIf;
import com.didekindroid.api.OnSpinnerClick;
import com.didekindroid.api.ViewerSelectionList;
import com.didekindroid.router.ComponentReplacerIf;
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
 */
public class ViewerIncidSeeClose extends
        ViewerSelectionList<ListView, CtrlerSelectableItemIf<IncidenciaUser, Bundle>, IncidenciaUser>
        implements ComponentReplacerIf, OnSpinnerClick {

    protected ViewerComuSpinner comuSpinnerViewer;

    protected ViewerIncidSeeClose(View frView, Activity activity)
    {
        super((ListView) frView.findViewById(android.R.id.list), activity, null);
    }

    static ViewerIncidSeeClose newViewerIncidSeeClose(View view, Activity activity)
    {
        Timber.d("newViewerIncidSeeClose()");
        ViewerIncidSeeClose parentInstance = new ViewerIncidSeeClose(view, activity);
        parentInstance.setController(new CtrlerIncidSeeCloseByComu(parentInstance));
        parentInstance.comuSpinnerViewer = newViewerComuSpinner((Spinner) view.findViewById(R.id.incid_reg_comunidad_spinner), activity, parentInstance);
        return parentInstance;
    }

    // ==================================  VIEWER  =================================

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        initSelectedItemId(savedState);
        comuSpinnerViewer.doViewInViewer(savedState, viewBean);
        doListViewer(savedState);
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

    /* ==================================  ViewerSelection  =================================*/

    /**
     * comunidadesSpinner.doViewInViewer() --> comunidadesSpinner.loadItemsByEntitiyId() --> onSuccessLoadItems()
     * --> view.setSelection() --> ComuSelectedListener --> onItemSelected() --> OnSpinnerClick.doOnClickItemId().
     * <p>
     * This method is called after doOnClickItemId() --> controller.loadItemsByEntitiyId() are executed.
     */
    public void onSuccessLoadItems(List<IncidenciaUser> incidCloseList)
    {
        Timber.d("onSuccessLoadItems()");
        ArrayAdapter<IncidenciaUser> adapter = new AdapterIncidSeeClosedByComu(activity);
        adapter.addAll(incidCloseList);
        view.setAdapter(adapter);
        if (view.getCount() > 1 && itemSelectedId > 0L) {
            view.setItemChecked(getSelectedPositionFromItemId(itemSelectedId), true);
        }
    }

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

        // Position in 1 to take account header view in position 0.
        int position = 1;
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

    // ==================================  ComponentReplaceIF  =================================

    /**
     * This method is called after the controller loads the data related with the incidencia selected (its resolucion, mainly).
     * Data are passed in a bundle to the next component.
     */
    @Override
    public void replaceComponent(@NonNull Bundle bundle)
    {
        Timber.d("initActivityWithBundle()");
        ComponentReplacerIf.class.cast(activity).replaceComponent(bundle);
    }

    // ==================================  OnSpinnerClick  =================================

    /**
     * This method is called when the comunidades spinner is loadeda and one of them selected.
     * It loads the list data.
     */
    @Override
    public long doOnClickItemId(long itemId)
    {
        Timber.d("doOnClickItemId()");
        controller.loadItemsByEntitiyId(itemId);
        return itemId;
    }

    // ==================================  HELPERS  =================================

    private void doListViewer(Bundle savedState)
    {
        // To get visible a divider on top of the list.
        view.addHeaderView(new View(activity), null, false);
        view.setEmptyView(view.findViewById(android.R.id.empty));
        view.setOnItemClickListener(new ListItemOnClickListener());
    }

    @SuppressWarnings("WeakerAccess")
    class ListItemOnClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View viewClick, int position, long id)
        {
            Timber.d("onListItemClick()");
            // List header increases position in 1: no problems with that, because we use itemSelectedId in saveState.
            view.setItemChecked(position, true);
            viewClick.setSelected(true);
            IncidenciaUser incidenciaUser = (IncidenciaUser) view.getItemAtPosition(position);
            itemSelectedId = incidenciaUser.getIncidencia().getIncidenciaId();
            controller.selectItem(incidenciaUser);
        }
    }
}
