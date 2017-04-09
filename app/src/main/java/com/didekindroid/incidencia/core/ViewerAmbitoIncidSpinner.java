package com.didekindroid.incidencia.core;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.didekindroid.api.CtrlerSpinnerIf;
import com.didekindroid.api.Viewer;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.api.ViewerSelectableIf;

import java.io.Serializable;

import timber.log.Timber;

import static com.didekindroid.incidencia.utils.IncidBundleKey.AMBITO_INCIDENCIA_POSITION;

/**
 * User: pedro@didekin
 * Date: 30/03/17
 * Time: 15:36
 */

public final class ViewerAmbitoIncidSpinner extends Viewer<Spinner, CtrlerSpinnerIf>
        implements ViewerSelectableIf<Spinner, CtrlerSpinnerIf> {

    /**
     * This id can be set in two ways:
     * 1. The user selects one item in the spinner.
     * 2. The id is retrieved from savedInstanceState.
     * 3. The id is retrieved from an activity intent passed on a viewBean.
     */
    @SuppressWarnings("WeakerAccess")
    int itemSelectedId;
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

    @Override
    public void saveState(Bundle savedState)
    {
        Timber.d("saveState()");
        if (itemSelectedId > 0) {
            savedState.putLong(AMBITO_INCIDENCIA_POSITION.key, itemSelectedId);
        }
    }

    @Override
    public long getSelectedItemId()
    {
        Timber.d("getSelectedItemId()");
        return itemSelectedId;
    }

    /* Mainly for tests */
    public void setItemSelectedId(int itemSelectedId)
    {
        this.itemSelectedId = itemSelectedId;
    }

    // ==================================== ViewerIf ====================================

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        incidenciaBean = IncidenciaBean.class.cast(viewBean);
        view.setOnItemSelectedListener(new ViewerAmbitoIncidSpinner.AmbitoIncidSelectedListener());
        initSelectedItemId(savedState);
        controller.loadDataInSpinner();
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
