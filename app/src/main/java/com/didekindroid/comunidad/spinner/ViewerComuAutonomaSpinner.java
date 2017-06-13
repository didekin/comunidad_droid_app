package com.didekindroid.comunidad.spinner;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.didekindroid.api.ObserverSingleSelectList;
import com.didekindroid.api.SpinnerEventListener;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.api.ViewerSelectList;
import com.didekinlib.model.comunidad.ComunidadAutonoma;

import java.io.Serializable;

import timber.log.Timber;

import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_AUTONOMA_ID;

/**
 * User: pedro@didekin
 * Date: 03/05/17
 * Time: 18:53
 */
public final class ViewerComuAutonomaSpinner extends
        ViewerSelectList<Spinner, CtrlerComAutonomaSpinner, ComunidadAutonoma> {

    static final ComuAutonomaSpinnerEventItemSelect spinnerEvent_default = new ComuAutonomaSpinnerEventItemSelect(new ComunidadAutonoma((short) 0));

    final SpinnerEventListener eventListener;
    ComuAutonomaSpinnerEventItemSelect spinnerEvent;

    private ViewerComuAutonomaSpinner(Spinner view, AppCompatActivity activity, ViewerIf parentViewer)
    {
        super(view, activity, parentViewer);
        eventListener = (SpinnerEventListener) parentViewer;
        spinnerEvent = spinnerEvent_default;
    }

    public static ViewerComuAutonomaSpinner newViewerComuAutonomaSpinner(Spinner spinner, AppCompatActivity activity, ViewerIf parentViewer)
    {
        Timber.d("newViewerComuAutonomaSpinner()");
        ViewerComuAutonomaSpinner instance = new ViewerComuAutonomaSpinner(spinner, activity, parentViewer);
        instance.setController(new CtrlerComAutonomaSpinner());
        return instance;
    }

    // ==================================== ViewerSelectListIf ====================================

    @Override
    public void initSelectedItemId(Bundle savedState)
    {
        Timber.d("initSelectedItemId()");
        if (savedState != null && savedState.containsKey(COMUNIDAD_AUTONOMA_ID.key)) {
            itemSelectedId = savedState.getLong(COMUNIDAD_AUTONOMA_ID.key, 0);
        } else {
            itemSelectedId = spinnerEvent.getSpinnerItemIdSelect();
        }
    }

    // ==================================== ViewerIf ====================================

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        if (viewBean != null) {
            spinnerEvent = ComuAutonomaSpinnerEventItemSelect.class.cast(viewBean);
        }
        initSelectedItemId(savedState);
        view.setOnItemSelectedListener(new ComuAutonomaSelectedListener());
        controller.loadItemsByEntitiyId(new ObserverSingleSelectList<>(this));
    }

    @Override
    public void saveState(Bundle savedState)
    {
        Timber.d("saveState()");
        if (savedState == null) {
            savedState = new Bundle(1);
        }
        savedState.putLong(COMUNIDAD_AUTONOMA_ID.key, itemSelectedId);
    }

    //  ===================================== HELPERS ============================================

    @SuppressWarnings("WeakerAccess")
    class ComuAutonomaSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            Timber.d("onItemSelected()");
            ComunidadAutonoma comuAutonomaIn = (ComunidadAutonoma) parent.getItemAtPosition(position);
            spinnerEvent = new ComuAutonomaSpinnerEventItemSelect(comuAutonomaIn);
            itemSelectedId = spinnerEvent.getSpinnerItemIdSelect();
            // Event passed to parent viewer for futher action.
            eventListener.doOnClickItemId(spinnerEvent);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {
            Timber.d("onNothingSelected()");
        }
    }
}
