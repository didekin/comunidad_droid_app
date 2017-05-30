package com.didekindroid.comunidad.spinner;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.didekindroid.api.ObserverSingleSelectList;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.api.ViewerSelectList;

import java.io.Serializable;
import java.util.List;

import timber.log.Timber;

import static com.didekindroid.comunidad.utils.ComuBundleKey.TIPO_VIA_ID;

/**
 * User: pedro@didekin
 * Date: 03/05/17
 * Time: 10:09
 */

public final class ViewerTipoViaSpinner extends
        ViewerSelectList<Spinner, CtrlerTipoViaSpinner, TipoViaValueObj> {

    TipoViaValueObj tipoViaValueObj;

    private ViewerTipoViaSpinner(Spinner view, Activity activity, ViewerIf parentViewer)
    {
        super(view, activity, parentViewer);
    }

    public static ViewerTipoViaSpinner newViewerTipoViaSpinner(Spinner spinner, Activity activity, ViewerIf parentViewer)
    {
        Timber.d("newViewerTipoViaSpinner()");
        ViewerTipoViaSpinner instance = new ViewerTipoViaSpinner(spinner, activity, parentViewer);
        instance.setController(new CtrlerTipoViaSpinner());
        return instance;
    }

    // ==================================== ViewerSelectListIf ====================================

    @Override
    public void initSelectedItemId(Bundle savedState)
    {
        Timber.d("initSelectedItemId()");
        if (savedState != null && savedState.containsKey(TIPO_VIA_ID.key)) {
            itemSelectedId = savedState.getLong(TIPO_VIA_ID.key, 0);
        } else {
            itemSelectedId = 0;
        }
    }

    @Override
    public void onSuccessLoadItemList(List<TipoViaValueObj> tiposVia)
    {
        Timber.d("onSuccessLoadItemList()");
        ArrayAdapter<TipoViaValueObj> adapter = getArrayAdapterForSpinner(activity);
        adapter.addAll(tiposVia);
        view.setAdapter(adapter);
        view.setSelection(getSelectedPositionFromDesc(tipoViaValueObj != null ? tipoViaValueObj.getTipoViaDesc() : null));
    }

    int getSelectedPositionFromDesc(String tipoViaDesc)
    {
        Timber.d("getSelectedPositionFromDesc()");

        int position = 0;
        boolean isFound = false;
        if (tipoViaDesc != null && !tipoViaDesc.isEmpty()) {
            String tipoViaDescIn;
            do {
                tipoViaDescIn = ((TipoViaValueObj) view.getItemAtPosition(position)).getTipoViaDesc();
                if (tipoViaDesc.equals(tipoViaDescIn)) {
                    isFound = true;
                    break;
                }
            } while (++position < view.getCount());
        }
        return isFound ? position : 0;
    }

    // ==================================== ViewerIf ====================================

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        tipoViaValueObj = viewBean != null ? TipoViaValueObj.class.cast(viewBean) : null;
        initSelectedItemId(savedState);
        view.setOnItemSelectedListener(new TipoViaSelectedListener());
        CtrlerTipoViaSpinner.class.cast(controller).loadItemsByEntitiyId(new ObserverSingleSelectList<>(this));
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

    public TipoViaValueObj getTipoViaValueObj()
    {
        Timber.d("getTipoViaValueObj()");
        return tipoViaValueObj;
    }

    @SuppressWarnings("WeakerAccess")
    class TipoViaSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            Timber.d("onItemSelected()");
            tipoViaValueObj = (TipoViaValueObj) parent.getItemAtPosition(position);
            itemSelectedId = tipoViaValueObj.getPk();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {
            Timber.d("In tipoViaSpinner.setOnItemSelectedListener, onNothingSelected()");
        }
    }
}
