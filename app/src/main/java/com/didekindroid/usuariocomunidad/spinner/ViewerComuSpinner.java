package com.didekindroid.usuariocomunidad.spinner;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.didekindroid.lib_one.api.ObserverSingleSelectList;
import com.didekindroid.lib_one.api.SpinnerEventListener;
import com.didekindroid.lib_one.api.ViewerIf;
import com.didekindroid.lib_one.api.ViewerSelectList;
import com.didekindroid.lib_one.api.router.UiExceptionRouterIf;
import com.didekinlib.model.comunidad.Comunidad;

import java.io.Serializable;

import timber.log.Timber;

import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_ID;

/**
 * User: pedro@didekin
 * Date: 16/02/17
 * Time: 10:49
 */
@SuppressWarnings("WeakerAccess")
public class ViewerComuSpinner extends
        ViewerSelectList<Spinner, CtrlerComuSpinner, Comunidad> {

    final SpinnerEventListener eventListener;
    ComuSpinnerEventItemSelect spinnerEvent;

    ViewerComuSpinner(Spinner view, @NonNull ViewerIf parentViewer)
    {
        super(view, parentViewer.getActivity(), parentViewer);
        eventListener = (SpinnerEventListener) parentViewer;
    }

    public static ViewerComuSpinner newViewerComuSpinner(Spinner view, @NonNull ViewerIf parentViewer)
    {
        Timber.d("newViewerComuSpinner()");
        ViewerComuSpinner instance = new ViewerComuSpinner(view, parentViewer);
        instance.setController(new CtrlerComuSpinner());
        return instance;
    }

    // ==================================== ViewerSelectListIf ====================================

    @Override
    public void initSelectedItemId(@Nullable Bundle savedState)
    {
        Timber.d("initSelectedItemId()");

        if (savedState != null && savedState.containsKey(COMUNIDAD_ID.key) && savedState.getLong(COMUNIDAD_ID.key) > 0) {
            itemSelectedId = savedState.getLong(COMUNIDAD_ID.key, 0);
        } else if (spinnerEvent != null && spinnerEvent.getSpinnerItemIdSelect() > 0) {
            itemSelectedId = spinnerEvent.getSpinnerItemIdSelect();
        } else {
            itemSelectedId = 0;
        }
    }

    @Override
    public int getSelectedPositionFromItemId(long itemId)
    {
        Timber.d("getSelectedPositionFromItemId()");

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
    public UiExceptionRouterIf getExceptionRouter()
    {
        Timber.d("getExceptionRouter()");
        return getParentViewer().getExceptionRouter();
    }

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        if (viewBean != null){
            spinnerEvent = new ComuSpinnerEventItemSelect(Comunidad.class.cast(viewBean));
        }
        view.setOnItemSelectedListener(new ComuSelectedListener());
        initSelectedItemId(savedState);
        controller.loadItemsByEntitiyId(new ObserverSingleSelectList<>(this));
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


            Comunidad comunidadIn = ((Comunidad) parent.getItemAtPosition(position));
            spinnerEvent = new ComuSpinnerEventItemSelect(comunidadIn);
            itemSelectedId = spinnerEvent.getSpinnerItemIdSelect();
            // Event passed to parent viewer for futher action.
            eventListener.doOnClickItemId(spinnerEvent);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {
            Timber.d("comunidadSpinner.onNothingSelected()");
        }
    }
}
