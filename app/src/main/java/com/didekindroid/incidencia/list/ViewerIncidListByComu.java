package com.didekindroid.incidencia.list;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.didekindroid.api.CtrlerSelectableItemIf;
import com.didekindroid.router.ActivityInitiatorIf;
import com.didekindroid.api.Viewer;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.api.ViewerSelectableIf;
import com.didekinlib.model.incidencia.dominio.IncidenciaUser;

import java.io.Serializable;

import timber.log.Timber;

import static com.didekindroid.incidencia.utils.IncidBundleKey.INCIDENCIA_LIST_INDEX;

/**
 * User: pedro@didekin
 * Date: 17/03/17
 * Time: 18:05
 */
@SuppressWarnings("WeakerAccess")
public class ViewerIncidListByComu
        extends
        Viewer<ListView, CtrlerSelectableItemIf<IncidenciaUser, Bundle>>
        implements
        ViewerSelectableIf<ListView, CtrlerSelectableItemIf<IncidenciaUser, Bundle>>,
        ActivityInitiatorIf {

    private final View emptyListView;
    long incidenciaSelectedIndex;

    public ViewerIncidListByComu(ListView view, View emptyListView, Activity activity, ViewerIf parentViewer)
    {
        super(view, activity, parentViewer);
        this.emptyListView = emptyListView;
    }

    public static ViewerIncidListByComu newListViewer(View view, Activity activity, @NonNull ViewerIf parentViewer)
    {
        Timber.d("newListViewer()");
        ListView listView = (ListView) view.findViewById(android.R.id.list);
        View emptyListView = view.findViewById(android.R.id.empty);
        return new ViewerIncidListByComu(listView, emptyListView, activity, parentViewer);
    }

    @Override
    public void initSelectedItemId(Bundle savedState)
    {
        Timber.d("initSelectedItemId()");
        if (savedState != null) {
            incidenciaSelectedIndex = savedState.getInt(INCIDENCIA_LIST_INDEX.key, 0);
        }
    }

    @Override
    public void saveState(Bundle savedState)
    {
        Timber.d("saveIncidSelectedIndex()");
        if (savedState == null) {
            savedState = new Bundle(1);
        }
        savedState.putLong(INCIDENCIA_LIST_INDEX.key, incidenciaSelectedIndex);
    }

    /**
     * @return the list index of the incidencia selected.
     */
    @Override
    public long getSelectedItemId()
    {
        return incidenciaSelectedIndex;
    }

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");

        initSelectedItemId(savedState);

        // view is intialized in parent viewer.
        // To get visible a divider on top of the list.
        view.addHeaderView(new View(activity), null, true);
        view.setEmptyView(emptyListView);
        initSelectedItemId(savedState);
        view.setSelection((int) incidenciaSelectedIndex);

        view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        });
    }

    @Override
    public void initActivity(Bundle bundle)
    {
        Timber.d("initActivityWithBundle()");
        ActivityInitiatorIf.class.cast(parentViewer).initActivity(bundle);
    }
}
