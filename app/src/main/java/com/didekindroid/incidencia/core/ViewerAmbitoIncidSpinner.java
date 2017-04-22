package com.didekindroid.incidencia.core;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.didekindroid.api.CtrlerSelectionList;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.api.ViewerSelectionList;

import java.io.Serializable;
import java.util.List;

import timber.log.Timber;

import static com.didekindroid.incidencia.utils.IncidBundleKey.AMBITO_INCIDENCIA_POSITION;

/**
 * User: pedro@didekin
 * Date: 30/03/17
 * Time: 15:36
 */

public final class ViewerAmbitoIncidSpinner extends
        ViewerSelectionList<Spinner, CtrlerSelectionList<AmbitoIncidValueObj>, AmbitoIncidValueObj> {

    @SuppressWarnings("WeakerAccess")
    IncidenciaBean incidenciaBean;

    private ViewerAmbitoIncidSpinner(Spinner view, Activity activity, ViewerIf parentViewer)
    {
        super(view, activity, parentViewer);
    }

    public static ViewerAmbitoIncidSpinner newViewerAmbitoIncidSpinner(Spinner view, Activity activity, ViewerIf parentViewer)
    {
        Timber.d("newViewerAmbitoIncidSpinner()");
        ViewerAmbitoIncidSpinner viewer = new ViewerAmbitoIncidSpinner(view, activity, parentViewer);
        viewer.setController(CtrlerAmbitoIncidSpinner.newCtrlerAmbitoIncidSpinner(viewer));
        return viewer;
    }

    // ==================================== ViewerSelectionListIf ====================================

    @Override
    public void onSuccessLoadItems(List<AmbitoIncidValueObj> incidCloseList)
    {
        Timber.d("onSuccessLoadItems()");

        ArrayAdapter<AmbitoIncidValueObj> adapter = ViewerSelectionList.getArrayAdapterForSpinner(AmbitoIncidValueObj.class, activity);
        adapter.addAll(incidCloseList);
        view.setAdapter(adapter);
        view.setSelection(getSelectedPositionFromItemId(itemSelectedId));
    }

    @Override
    public void initSelectedItemId(Bundle savedState)
    {
        Timber.d("initSelectedItemId()");
        if (savedState != null && savedState.containsKey(AMBITO_INCIDENCIA_POSITION.key)) {
            itemSelectedId = (int) savedState.getLong(AMBITO_INCIDENCIA_POSITION.key, 0);
        } else if (incidenciaBean.getCodAmbitoIncid() > 0) {
            itemSelectedId = incidenciaBean.getCodAmbitoIncid();
        } else {
            itemSelectedId = 0;
        }
    }

    // ==================================== ViewerIf ====================================

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        incidenciaBean = IncidenciaBean.class.cast(viewBean);
        view.setOnItemSelectedListener(new ViewerAmbitoIncidSpinner.AmbitoIncidSelectedListener());
        initSelectedItemId(savedState);
        CtrlerSelectionList.class.cast(controller).loadItemsByEntitiyId();
    }

    @Override
    public void saveState(Bundle savedState)
    {
        Timber.d("saveState()");
        if (itemSelectedId > 0) {
            savedState.putLong(AMBITO_INCIDENCIA_POSITION.key, itemSelectedId);
        }
    }

    //  ===================================== HELPERS ============================================

    @SuppressWarnings("WeakerAccess")
    public class AmbitoIncidSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            Timber.d("importanciaSpinner.onItemSelected()");
            itemSelectedId = position;
            incidenciaBean.setCodAmbitoIncid((short) itemSelectedId);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {
            Timber.d("importanciaSpinner.onNothingSelected()");
        }
    }
}
