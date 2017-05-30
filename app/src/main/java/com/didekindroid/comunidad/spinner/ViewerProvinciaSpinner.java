package com.didekindroid.comunidad.spinner;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.didekindroid.api.SpinnerEventListener;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.api.ViewerSelectList;
import com.didekinlib.model.comunidad.ComunidadAutonoma;
import com.didekinlib.model.comunidad.Provincia;

import java.io.Serializable;

import timber.log.Timber;

import static com.didekindroid.comunidad.utils.ComuBundleKey.PROVINCIA_ID;

/**
 * User: pedro@didekin
 * Date: 05/05/17
 * Time: 16:31
 */
@SuppressWarnings("WeakerAccess")
public final class ViewerProvinciaSpinner extends
        ViewerSelectList<Spinner, CtrlerProvinciaSpinner, Provincia> {

    static final ProvinciaSpinnerEventItemSelect default_spinnerEvent =
            new ProvinciaSpinnerEventItemSelect(new Provincia(new ComunidadAutonoma((short) 0), (short) 0, null));
    final SpinnerEventListener eventListener;
    ProvinciaSpinnerEventItemSelect spinnerEvent;


    private ViewerProvinciaSpinner(Spinner view, Activity activity, ViewerIf parentViewer)
    {
        super(view, activity, parentViewer);
        eventListener = (SpinnerEventListener) parentViewer;
        spinnerEvent = default_spinnerEvent;
    }

    public static ViewerProvinciaSpinner newViewerProvinciaSpinner(Spinner spinner, Activity activity, ViewerIf parentViewer)
    {
        Timber.d("newViewerProvinciaSpinner()");
        ViewerProvinciaSpinner instance = new ViewerProvinciaSpinner(spinner, activity, parentViewer);
        instance.setController(new CtrlerProvinciaSpinner());
        return instance;
    }

    // ==================================== ViewerSelectListIf ====================================

    @Override
    public void initSelectedItemId(Bundle savedState)
    {
        Timber.d("initSelectedItemId()");
        if (savedState != null && savedState.containsKey(PROVINCIA_ID.key)) {
            itemSelectedId = savedState.getLong(PROVINCIA_ID.key, 0);
        } else {
            itemSelectedId = spinnerEvent.getSpinnerItemIdSelect();
        }
    }

    @Override
    public int getSelectedPositionFromItemId(long itemId)
    {
        Timber.d("getSelectedPositionFromItemId()");
        int position = 0;
        boolean isFound = false;
        if (itemId > 0L) {
            short provinciaInId;
            do {
                provinciaInId = ((Provincia) view.getItemAtPosition(position)).getProvinciaId();
                if (provinciaInId == (short) itemId) {
                    isFound = true;
                    break;
                }
            } while (++position < view.getCount());
        }
        // Si no encontramos la comuidad, index = 0.
        return isFound ? position : 0;
    }

    /* ==================================== ViewerIf ====================================*/

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        if (viewBean != null) {
            spinnerEvent = ProvinciaSpinnerEventItemSelect.class.cast(viewBean);
        }
        initSelectedItemId(savedState);
        view.setOnItemSelectedListener(new ProvinciaSelectedListener());
        // No cargamos datos hasta saber comunidad autónoma.
    }

    @Override
    public void saveState(Bundle savedState)
    {
        Timber.d("saveState()");
        if (savedState == null) {
            savedState = new Bundle(1);
        }
        savedState.putLong(PROVINCIA_ID.key, itemSelectedId);
    }

    //  ===================================== HELPERS ============================================

    public ProvinciaSpinnerEventItemSelect getProvinciaEventSelect()
    {
        Timber.d("getProvinciaIn()");
        return spinnerEvent;
    }

    @SuppressWarnings("WeakerAccess")
    class ProvinciaSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            Timber.d("onItemSelected()");
            Provincia provinciaIn = (Provincia) parent.getItemAtPosition(position);
            spinnerEvent = new ProvinciaSpinnerEventItemSelect(provinciaIn);
            itemSelectedId = spinnerEvent.getSpinnerItemIdSelect();
            eventListener.doOnClickItemId(spinnerEvent);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {
            Timber.d("onNothingSelected()");
        }
    }
}
