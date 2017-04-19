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

import static com.didekindroid.incidencia.utils.IncidBundleKey.INCIDENCIA_LIST_INDEX;
import static com.didekindroid.usuariocomunidad.spinner.ViewerComuSpinner.newViewerComuSpinner;

/**
 * User: pedro@didekin
 * Date: 18/03/17
 * Time: 11:01
 */
public class ViewerIncidSeeClose extends
        ViewerSelectionList<ListView, CtrlerSelectableItemIf<IncidenciaUser, Bundle>, IncidenciaUser>
        implements ComponentReplacerIf, OnSpinnerClick {

    private final ArrayAdapter<IncidenciaUser> adapter;
    protected ViewerComuSpinner spinnerViewer;
    @SuppressWarnings("WeakerAccess")
    long incidenciaSelectedIndex;

    protected ViewerIncidSeeClose(View frView, Activity activity)
    {
        super((ListView) frView.findViewById(android.R.id.list), activity, null);
        adapter = new AdapterIncidSeeClosedByComu(activity);
    }

    static ViewerIncidSeeClose newViewerIncidSeeClose(View view, Activity activity)
    {
        Timber.d("newViewerIncidSeeClose()");
        ViewerIncidSeeClose parentInstance = new ViewerIncidSeeClose(view, activity);
        parentInstance.setController(new CtrlerIncidSeeCloseByComu(parentInstance));
        parentInstance.spinnerViewer = newViewerComuSpinner((Spinner) view.findViewById(R.id.incid_reg_comunidad_spinner), activity, parentInstance);
        return parentInstance;
    }

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        spinnerViewer.doViewInViewer(savedState, viewBean);
        doListViewer(savedState);
    }

    @Override
    public int clearSubscriptions()
    {
        Timber.d("clearSubscriptions()");
        return spinnerViewer.clearSubscriptions()
                + controller.clearSubscriptions();
    }

    @Override
    public void saveState(Bundle savedState)
    {
        Timber.d("saveState()");
        if (savedState == null) {
            savedState = new Bundle();
        }
        savedState.putLong(INCIDENCIA_LIST_INDEX.key, incidenciaSelectedIndex);
        spinnerViewer.saveState(savedState);
    }

    @Override
    public long doOnClickItemId(long itemId)
    {
        Timber.d("doOnClickItemId()");
        controller.loadItemsByEntitiyId(itemId);
        return itemId;
    }

    @Override
    public void replaceComponent(@NonNull Bundle bundle)
    {
        Timber.d("initActivityWithBundle()");
        ComponentReplacerIf.class.cast(activity).replaceComponent(bundle);
    }

    public void onSuccessLoadItems(List<IncidenciaUser> incidCloseList)
    {
        Timber.d("onSuccessLoadItems()");
        adapter.clear();
        adapter.addAll(incidCloseList);
        view.setAdapter(adapter);
    }

    @Override
    public void initSelectedItemId(Bundle savedState)
    {
        Timber.d("initSelectedItemId()");
        if (savedState != null) {
            incidenciaSelectedIndex = savedState.getInt(INCIDENCIA_LIST_INDEX.key, 0);
        }
    }

    /**
     * @return the list index of the incidencia selected.
     */
    @Override
    public long getSelectedItemId()
    {
        Timber.d("getSelectedItemId()");
        return incidenciaSelectedIndex;
    }

    // ==================================  HELPERS  =================================

    private void doListViewer(Bundle savedState)
    {
        // To get visible a divider on top of the list.
        view.addHeaderView(new View(activity), null, true);
        view.setEmptyView(view.findViewById(android.R.id.empty));

        // Initialized previous selection.
        initSelectedItemId(savedState);
        view.setSelection((int) incidenciaSelectedIndex);
        view.setOnItemClickListener(new ListItemOnClickListener());
    }

    @SuppressWarnings("WeakerAccess")
    class ListItemOnClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View viewClick, int position, long id)
        {
            Timber.d("onListItemClick()");
            view.setItemChecked(position, true);
            viewClick.setSelected(true);
            IncidenciaUser incidenciaUser = (IncidenciaUser) view.getItemAtPosition(position);
            incidenciaSelectedIndex = position;
            controller.selectItem(incidenciaUser);
        }
    }
}
