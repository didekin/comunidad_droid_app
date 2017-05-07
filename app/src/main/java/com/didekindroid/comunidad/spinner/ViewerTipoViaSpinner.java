package com.didekindroid.comunidad.spinner;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.didekindroid.api.CtrlerSelectionList;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.api.ViewerSelectionList;
import com.didekindroid.comunidad.ComunidadBean;

import java.io.Serializable;
import java.util.List;

import timber.log.Timber;

import static com.didekindroid.comunidad.ComuBundleKey.TIPO_VIA_ID;
import static com.didekindroid.comunidad.spinner.CtrlerTipoViaSpinner.newCtrlerTipoViaSpinner;

/**
 * User: pedro@didekin
 * Date: 03/05/17
 * Time: 10:09
 */

final class ViewerTipoViaSpinner extends
        ViewerSelectionList<Spinner, CtrlerSelectionList<TipoViaValueObj>, TipoViaValueObj> {

    ComunidadBean comunidadBean;

    private ViewerTipoViaSpinner(Spinner view, Activity activity, ViewerIf parentViewer)
    {
        super(view, activity, parentViewer);
    }

    static ViewerTipoViaSpinner newViewerTipoViaSpinner(Spinner spinner, Activity activity, ViewerIf parentViewer)
    {
        Timber.d("newViewerTipoViaSpinner()");
        ViewerTipoViaSpinner instance = new ViewerTipoViaSpinner(spinner, activity, parentViewer);
        instance.setController(newCtrlerTipoViaSpinner(instance));
        return instance;
    }

    // ==================================== ViewerSelectionListIf ====================================

    @Override
    public void initSelectedItemId(Bundle savedState)
    {
        Timber.d("initSelectedItemId()");
        if (savedState != null && savedState.containsKey(TIPO_VIA_ID.key)) {
            itemSelectedId = savedState.getLong(TIPO_VIA_ID.key, 0);
        } else if (comunidadBean.getTipoVia() != null && comunidadBean.getTipoVia().getPk() > 0) {
            itemSelectedId = comunidadBean.getTipoVia().getPk();
        } else {
            itemSelectedId = 0;
        }
    }

    // ==================================== ViewerIf ====================================

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        comunidadBean = viewBean == null ? new ComunidadBean() : ComunidadBean.class.cast(viewBean);
        initSelectedItemId(savedState);
        view.setOnItemSelectedListener(new TipoViaSelectedListener());
        CtrlerSelectionList.class.cast(controller).loadItemsByEntitiyId();
    }

    @Override
    public void saveState(Bundle savedState)
    {
        Timber.d("saveState()");
        if (savedState == null) {
            savedState = new Bundle(1);
        }
        savedState.putLong(TIPO_VIA_ID.key, itemSelectedId);
    }

    //  ===================================== HELPERS ============================================

    @SuppressWarnings("WeakerAccess")
    class TipoViaSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            Timber.d("onItemSelected()");
            TipoViaValueObj tipoViaObj = (TipoViaValueObj) parent.getItemAtPosition(position);
            itemSelectedId = tipoViaObj.getPk();
            comunidadBean.setTipoVia(tipoViaObj);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {
            Timber.d("In tipoViaSpinner.setOnItemSelectedListener, onNothingSelected()");
        }
    }
}
