package com.didekindroid.lib_one.incidencia.spinner;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.didekindroid.lib_one.incidencia.IncidenciaBean;
import com.didekindroid.lib_one.api.ObserverSingleSelectList;
import com.didekindroid.lib_one.api.ViewerIf;
import com.didekindroid.lib_one.api.ViewerSelectList;
import com.didekindroid.lib_one.api.router.UiExceptionRouterIf;

import java.io.Serializable;

import timber.log.Timber;

import static com.didekindroid.lib_one.incidencia.spinner.IncidenciaSpinnerKey.AMBITO_INCIDENCIA_POSITION;

/**
 * User: pedro@didekin
 * Date: 30/03/17
 * Time: 15:36
 */

public final class ViewerAmbitoIncidSpinner extends
        ViewerSelectList<Spinner, CtrlerAmbitoIncidSpinner, AmbitoIncidValueObj> {

    @SuppressWarnings("WeakerAccess")
    IncidenciaBean incidenciaBean;

    private ViewerAmbitoIncidSpinner(Spinner view, @NonNull ViewerIf parentViewer)
    {
        super(view, parentViewer.getActivity(), parentViewer);
    }

    public static ViewerAmbitoIncidSpinner newViewerAmbitoIncidSpinner(Spinner view, @NonNull ViewerIf parentViewer)
    {
        Timber.d("newViewerAmbitoIncidSpinner()");
        ViewerAmbitoIncidSpinner viewer = new ViewerAmbitoIncidSpinner(view, parentViewer);
        viewer.setController(new CtrlerAmbitoIncidSpinner());
        return viewer;
    }

    // ==================================== ViewerSelectListIf ====================================

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
    public UiExceptionRouterIf getExceptionRouter()
    {
        Timber.d("getExceptionRouter()");
        return getParentViewer().getExceptionRouter();
    }

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        incidenciaBean = IncidenciaBean.class.cast(viewBean);
        initSelectedItemId(savedState);
        view.setOnItemSelectedListener(new ViewerAmbitoIncidSpinner.AmbitoIncidSelectedListener());
        controller.loadItemsByEntitiyId(new ObserverSingleSelectList<>(this));
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
