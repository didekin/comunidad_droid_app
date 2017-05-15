package com.didekindroid.comunidad.spinner;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.didekindroid.api.CtrlerSelectionList;
import com.didekindroid.api.SpinnerEventListener;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.api.ViewerSelectionList;
import com.didekinlib.model.comunidad.Municipio;

import java.io.Serializable;

import timber.log.Timber;

import static com.didekindroid.comunidad.spinner.CtrlerMunicipioSpinner.newCtrlerMunicipioSpinner;
import static com.didekindroid.comunidad.utils.ComuBundleKey.MUNICIPIO_SPINNER_EVENT;

/**
 * User: pedro@didekin
 * Date: 05/05/17
 * Time: 16:32
 * <p>
 * The spinner with municipios is loaded with 'local' data, with a SqLite Pk, not necessarily the same as
 * the Mysql PK in remote. To avoid conflicts, the municipio code inside its provincia is used for itemSelectedId.
 */

@SuppressWarnings("WeakerAccess")
public final class ViewerMunicipioSpinner extends
        ViewerSelectionList<Spinner, CtrlerSelectionList<Municipio>, Municipio> {

    final SpinnerEventListener eventListener;
    MunicipioSpinnerEventItemSelect spinnerEvent;

    private ViewerMunicipioSpinner(Spinner view, Activity activity, ViewerIf parentViewer)
    {
        super(view, activity, parentViewer);
        eventListener = (SpinnerEventListener) parentViewer;
    }

    public static ViewerMunicipioSpinner newViewerMunicipioSpinner(Spinner spinner, Activity activity, ViewerIf parentViewer)
    {
        Timber.d("newViewerMunicipioSpinner()");
        ViewerMunicipioSpinner instance = new ViewerMunicipioSpinner(spinner, activity, parentViewer);
        instance.setController(newCtrlerMunicipioSpinner(instance));
        return instance;
    }

    // ==================================== ViewerSelectionListIf ====================================

    @Override
    public void initSelectedItemId(Bundle savedState)
    {
        Timber.d("initSelectedItemId()");
        if (savedState != null && savedState.containsKey(MUNICIPIO_SPINNER_EVENT.key)) {
            itemSelectedId = MunicipioSpinnerEventItemSelect.class.cast(savedState.getSerializable(MUNICIPIO_SPINNER_EVENT.key)).getSpinnerItemIdSelect();
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
            int municipioInId;
            do {
                municipioInId = ((Municipio) view.getItemAtPosition(position)).getCodInProvincia();
                if (municipioInId == (int) itemId) {
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
        spinnerEvent = viewBean != null ? MunicipioSpinnerEventItemSelect.class.cast(viewBean) : null;
        initSelectedItemId(savedState);
        view.setOnItemSelectedListener(new MunicipioSelectedListener());
        // No cargamos datos hasta saber provincia.
    }

    @Override
    public void saveState(Bundle savedState)
    {
        Timber.d("saveState()");

        if (savedState == null) {
            savedState = new Bundle(1);
        }
        if (spinnerEvent != null) {
            savedState.putSerializable(MUNICIPIO_SPINNER_EVENT.key, spinnerEvent);
        }
    }

    //  ===================================== HELPERS ============================================

    public void setSpinnerEvent(Municipio municipio)
    {
        Timber.d("getMunicipioIn()");
        spinnerEvent.setSpinnerItemIdSelect(municipio);
    }

    public MunicipioSpinnerEventItemSelect getSpinnerEvent()
    {
        Timber.d("getSpinnerEvent()");
        return spinnerEvent;
    }

    @SuppressWarnings("WeakerAccess")
    class MunicipioSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            Timber.d("onItemSelected()");
            Municipio municipioIn = (Municipio) parent.getItemAtPosition(position);
            spinnerEvent.setSpinnerItemIdSelect(municipioIn);
            itemSelectedId = spinnerEvent.getSpinnerItemIdSelect();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {
            Timber.d("onNothingSelected()");
        }
    }
}
