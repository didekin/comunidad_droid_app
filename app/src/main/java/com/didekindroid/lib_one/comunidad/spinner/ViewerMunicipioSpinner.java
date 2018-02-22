package com.didekindroid.lib_one.comunidad.spinner;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.didekindroid.lib_one.api.SpinnerEventListener;
import com.didekindroid.lib_one.api.ViewerIf;
import com.didekindroid.lib_one.api.ViewerSelectList;
import com.didekinlib.model.comunidad.Municipio;
import com.didekinlib.model.comunidad.Provincia;

import java.io.Serializable;

import timber.log.Timber;

import static com.didekindroid.lib_one.comunidad.spinner.ComunidadSpinnerKey.MUNICIPIO_SPINNER_EVENT;
import static com.didekindroid.lib_one.util.CommonAssertionMsg.bean_fromView_should_be_initialized;
import static com.didekindroid.lib_one.util.UiUtil.assertTrue;

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
        ViewerSelectList<Spinner, CtrlerMunicipioSpinner, Municipio> {

    public final static MunicipioSpinnerEventItemSelect spinnerEvent_default = new MunicipioSpinnerEventItemSelect(new Municipio((short) 0, new Provincia((short) 0)));
    final SpinnerEventListener eventListener;
    MunicipioSpinnerEventItemSelect spinnerEvent;

    private ViewerMunicipioSpinner(Spinner view, Activity activity, @NonNull ViewerIf parentViewer)
    {
        super(view, activity, parentViewer);
        eventListener = (SpinnerEventListener) parentViewer;
        spinnerEvent = spinnerEvent_default;
    }

    public static ViewerMunicipioSpinner newViewerMunicipioSpinner(Spinner spinner, Activity activity, ViewerIf parentViewer)
    {
        Timber.d("newViewerMunicipioSpinner()");
        ViewerMunicipioSpinner instance = new ViewerMunicipioSpinner(spinner, activity, parentViewer);
        instance.setController(new CtrlerMunicipioSpinner());
        return instance;
    }

    // ==================================== ViewerSelectListIf ====================================

    @Override
    public void initSelectedItemId(Bundle savedState)
    {
        Timber.d("initSelectedItemId()");
        if (savedState != null && savedState.containsKey(MUNICIPIO_SPINNER_EVENT.key)) {
            itemSelectedId = MunicipioSpinnerEventItemSelect.class.cast(savedState.getSerializable(MUNICIPIO_SPINNER_EVENT.key)).getSpinnerItemIdSelect();
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
        if (viewBean != null) {
            spinnerEvent = MunicipioSpinnerEventItemSelect.class.cast(viewBean);
        }
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
        if (spinnerEvent.getSpinnerItemIdSelect() > 0L) {
            savedState.putSerializable(MUNICIPIO_SPINNER_EVENT.key, spinnerEvent);
        }
    }

    //  ===================================== HELPERS ============================================

    public MunicipioSpinnerEventItemSelect getSpinnerEvent()
    {
        Timber.d("getSpinnerEvent()");
        return spinnerEvent;
    }

    public void setSpinnerEvent(Municipio municipio)
    {
        Timber.d("getMunicipioIn()");
        assertTrue(municipio.getProvincia() != null, bean_fromView_should_be_initialized);
        spinnerEvent = new MunicipioSpinnerEventItemSelect(municipio);
    }

    @SuppressWarnings("WeakerAccess")
    class MunicipioSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            Timber.d("onItemSelected()");
            Municipio municipioIn = (Municipio) parent.getItemAtPosition(position);
            spinnerEvent = new MunicipioSpinnerEventItemSelect(municipioIn);
            itemSelectedId = spinnerEvent.getSpinnerItemIdSelect();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {
            Timber.d("onNothingSelected()");
        }
    }
}
