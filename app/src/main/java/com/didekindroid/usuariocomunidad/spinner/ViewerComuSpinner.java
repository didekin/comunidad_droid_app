package com.didekindroid.usuariocomunidad.spinner;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.didekindroid.api.CtrlerSelectionList;
import com.didekindroid.api.SpinnerClickHandler;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.api.ViewerSelectionList;
import com.didekinlib.model.comunidad.Comunidad;

import java.io.Serializable;

import timber.log.Timber;

import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.usuariocomunidad.spinner.CtrlerComuSpinner.newControllerComuSpinner;

/**
 * User: pedro@didekin
 * Date: 16/02/17
 * Time: 10:49
 */
public class ViewerComuSpinner extends
        ViewerSelectionList<Spinner, CtrlerSelectionList<Comunidad>, Comunidad> {

    @SuppressWarnings("WeakerAccess")
    ComuSpinnerBean spinnerBean;

    ViewerComuSpinner(Spinner view, Activity activity, ViewerIf parentViewer)
    {
        super(view, activity, parentViewer);
    }

    public static ViewerComuSpinner newViewerComuSpinner(Spinner view, Activity activity, ViewerIf parentViewer)
    {
        Timber.d("newViewerComuSpinner()");
        ViewerComuSpinner instance = new ViewerComuSpinner(view, activity, parentViewer);
        instance.setController(newControllerComuSpinner(instance));
        return instance;
    }

    // ==================================== ViewerSelectionListIf ====================================

    @Override
    public void initSelectedItemId(Bundle savedState)
    {
        Timber.d("initSelectedItemId()");

        if (savedState != null && savedState.containsKey(COMUNIDAD_ID.key)) {
            itemSelectedId = savedState.getLong(COMUNIDAD_ID.key, 0);
        } else if (spinnerBean.getComunidadId() > 0) {
            itemSelectedId = spinnerBean.getComunidadId();
        } else {
            itemSelectedId = 0;
        }
    }

    @Override
    public int getSelectedPositionFromItemId(long itemId)
    {
        int position = 0;
        boolean isFound = false;
        if (itemId > 0L) {
            long comunidadIdIn;
            do {
                comunidadIdIn = ((Comunidad) view.getItemAtPosition(position)).getC_Id();
                if (comunidadIdIn == itemId) {
                    isFound = true;
                    break;
                }
            } while (++position < view.getCount());
        }
        // Si no encontramos la comuidad, index = 0.
        return isFound ? position : 0;
    }

    // ==================================== ViewerIf ====================================

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        spinnerBean = ComuSpinnerBean.class.cast(viewBean);
        view.setOnItemSelectedListener(new ComuSelectedListener());
        initSelectedItemId(savedState);
        CtrlerSelectionList.class.cast(controller).loadItemsByEntitiyId();
    }

    @Override
    public void saveState(Bundle savedState)
    {
        Timber.d("saveState()");
        if (savedState == null) {
            savedState = new Bundle(1);
        }
        savedState.putLong(COMUNIDAD_ID.key, itemSelectedId);
    }

    //  ===================================== HELPERS ============================================

    @SuppressWarnings("WeakerAccess")
    class ComuSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            Timber.d("comunidadSpinner.onItemSelected()");
            Comunidad comunidad = (Comunidad) parent.getItemAtPosition(position);
            itemSelectedId = comunidad.getC_Id();
            spinnerBean.setComunidadId(itemSelectedId);
            // Event passed to parent viewer for futher action.
            SpinnerClickHandler.class.cast(parentViewer).doOnClickItemId(itemSelectedId, ViewerComuSpinner.this.getClass());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {
            Timber.d("comunidadSpinner.onNothingSelected()");
        }
    }
}
