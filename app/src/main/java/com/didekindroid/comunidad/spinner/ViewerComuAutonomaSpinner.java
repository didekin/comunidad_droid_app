package com.didekindroid.comunidad.spinner;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.didekindroid.api.CtrlerSelectionList;
import com.didekindroid.api.SpinnerClickHandler;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.api.ViewerSelectionList;
import com.didekinlib.model.comunidad.ComunidadAutonoma;

import java.io.Serializable;

import timber.log.Timber;

import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_AUTONOMA_ID;
import static com.didekindroid.comunidad.spinner.CtrlerComAutonomaSpinner.newCtrlerComAutonomaSpinner;

/**
 * User: pedro@didekin
 * Date: 03/05/17
 * Time: 18:53
 */

final class ViewerComuAutonomaSpinner extends
        ViewerSelectionList<Spinner, CtrlerSelectionList<ComunidadAutonoma>, ComunidadAutonoma> {

    ComunidadAutonoma comunidadIn;

    private ViewerComuAutonomaSpinner(Spinner view, Activity activity, ViewerIf parentViewer)
    {
        super(view, activity, parentViewer);
    }

    static ViewerComuAutonomaSpinner newViewerComuAutonomaSpinner(Spinner spinner, Activity activity, ViewerIf parentViewer)
    {
        Timber.d("newViewerComuAutonomaSpinner()");
        ViewerComuAutonomaSpinner instance = new ViewerComuAutonomaSpinner(spinner, activity, parentViewer);
        instance.setController(newCtrlerComAutonomaSpinner(instance));
        return instance;
    }

    // ==================================== ViewerSelectionListIf ====================================

    @Override
    public void initSelectedItemId(Bundle savedState)
    {
        Timber.d("initSelectedItemId()");
        if (savedState != null && savedState.containsKey(COMUNIDAD_AUTONOMA_ID.key)) {
            itemSelectedId = savedState.getLong(COMUNIDAD_AUTONOMA_ID.key, 0);
        } else if (comunidadIn != null && comunidadIn.getCuId() > 0) {
            itemSelectedId = comunidadIn.getCuId();
        } else {
            itemSelectedId = 0;
        }
    }

    // ==================================== ViewerIf ====================================

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        comunidadIn = viewBean != null ? ComunidadAutonoma.class.cast(viewBean) : null;
        initSelectedItemId(savedState);
        view.setOnItemSelectedListener(new ComuAutonomaSelectedListener());
        CtrlerSelectionList.class.cast(controller).loadItemsByEntitiyId();
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
            ComunidadAutonoma comunidadAutonoma = (ComunidadAutonoma) parent.getItemAtPosition(position);
            comunidadIn = comunidadAutonoma;
            itemSelectedId = comunidadAutonoma.getCuId();
            // Event passed to parent viewer for futher action.
            SpinnerClickHandler.class.cast(parentViewer).doOnClickItemId(itemSelectedId, ViewerComuAutonomaSpinner.this.getClass());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {
            Timber.d("onNothingSelected()");
        }
    }
}
