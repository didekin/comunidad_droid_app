package com.didekindroid.incidencia.core.reg;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.didekindroid.api.CtrlerSpinnerIf;
import com.didekindroid.api.ViewBean;
import com.didekindroid.api.Viewer;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.api.ViewerSelectableIf;
import com.didekindroid.incidencia.core.IncidenciaBean;

import timber.log.Timber;

import static com.didekindroid.incidencia.core.reg.CtrlerAmbitoIncidSpinner.newCtrlerAmbitoIncidSpinner;
import static com.didekindroid.incidencia.utils.IncidBundleKey.AMBITO_INCIDENCIA_POSITION;
import static com.didekindroid.util.CommonAssertionMsg.class_cast_unallowed;
import static com.didekindroid.util.UIutils.assertTrue;

/**
 * User: pedro@didekin
 * Date: 30/03/17
 * Time: 15:36
 */

final class ViewerAmbitoIncidSpinner extends Viewer<Spinner, CtrlerSpinnerIf>
        implements ViewerSelectableIf<Spinner, CtrlerSpinnerIf> {

    /**
     * This id can be set in two ways:
     * 1. The user selects one item in the spinner.
     * 2. The id is retrieved from savedInstanceState.
     */
    @SuppressWarnings("WeakerAccess")
    int itemSelectedId;

    private ViewerAmbitoIncidSpinner(Spinner view, Activity activity, ViewerIf parentViewer)
    {
        super(view, activity, parentViewer);
    }

    static ViewerAmbitoIncidSpinner newViewerAmbitoIncidSpinner(Spinner view, Activity activity, ViewerIf parentViewer)
    {
        Timber.d("newViewerAmbitoIncidSpinner()");
        ViewerAmbitoIncidSpinner viewer = new ViewerAmbitoIncidSpinner(view, activity, parentViewer);
        viewer.setController(newCtrlerAmbitoIncidSpinner(viewer));
        return viewer;
    }

    @Override
    public void initSelectedItemId(Bundle savedState)
    {
        Timber.d("initSelectedItemId()");
        if (savedState != null) {
            itemSelectedId = (int) savedState.getLong(AMBITO_INCIDENCIA_POSITION.key, 0);
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

    @Override
    public long getItemIdInIntent()
    {
        Timber.d("getItemIdInIntent()");
        throw new UnsupportedOperationException();
    }

    // ==================================== ViewerIf ====================================

    @Override
    public void doViewInViewer(Bundle savedState, ViewBean viewBean)
    {
        Timber.d("doViewInViewer()");
        assertTrue(viewBean instanceof IncidenciaBean, class_cast_unallowed);
        view.setOnItemSelectedListener(new ViewerAmbitoIncidSpinner.AmbitoIncidSelectedListener((IncidenciaBean) viewBean));
        initSelectedItemId(savedState);
        controller.loadDataInSpinner();
    }


    //  ===================================== HELPERS ============================================

    @SuppressWarnings("WeakerAccess")
    class AmbitoIncidSelectedListener implements AdapterView.OnItemSelectedListener {

        private final IncidenciaBean bean;

        public AmbitoIncidSelectedListener(IncidenciaBean viewBean)
        {
            bean = viewBean;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            Timber.d("importanciaSpinner.onItemSelected()");
            itemSelectedId = position;
            bean.setCodAmbitoIncid((short) itemSelectedId);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {
            Timber.d("importanciaSpinner.onNothingSelected()");
        }
    }
}
