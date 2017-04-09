package com.didekindroid.incidencia.core;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.didekindroid.api.CtrlerSpinnerIf;
import com.didekindroid.api.Viewer;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.api.ViewerSelectableIf;

import java.io.Serializable;

import timber.log.Timber;

import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_NUMBER;

/**
 * User: pedro@didekin
 * Date: 29/03/17
 * Time: 11:57
 */

public final class ViewerImportanciaSpinner extends Viewer<Spinner, CtrlerSpinnerIf>
        implements ViewerSelectableIf<Spinner, CtrlerSpinnerIf> {

    /**
     * This id can be set in two ways:
     * 1. The user selects one item in the spinner.
     * 2. The id is retrieved from savedInstanceState.
     */
    @SuppressWarnings("WeakerAccess")
    short itemSelectedId;
    @SuppressWarnings("WeakerAccess")
    IncidImportanciaBean bean;

    ViewerImportanciaSpinner(@NonNull Spinner view, @NonNull Activity activity, @NonNull ViewerIf parentViewer)
    {
        super(view, activity, parentViewer);
    }

    public static ViewerImportanciaSpinner newViewerImportanciaSpinner(@NonNull Spinner view, @NonNull Activity activity, @NonNull ViewerIf parentViewer)
    {
        Timber.d("newViewerImportanciaSpinner()");
        ViewerImportanciaSpinner instance = new ViewerImportanciaSpinner(view, activity, parentViewer);
        instance.setController(new CtrlerImportanciaSpinner(instance));
        return instance;
    }

    @Override
    public void initSelectedItemId(Bundle savedState)
    {
        Timber.d("initSelectedItemId()");

        if (savedState != null && savedState.containsKey(INCID_IMPORTANCIA_NUMBER.key)) {
            itemSelectedId = (short) savedState.getLong(INCID_IMPORTANCIA_NUMBER.key, 0);
        } else if (bean.getImportancia() > 0) {
            itemSelectedId = bean.getImportancia();
        } else {
            itemSelectedId = (short) 0;
        }
    }

    @Override
    public void saveState(Bundle savedState)
    {
        Timber.d("saveState()");

        if (itemSelectedId > 0) {
            if (savedState == null) {
                savedState = new Bundle(1);
            }
            savedState.putLong(INCID_IMPORTANCIA_NUMBER.key, itemSelectedId);
        }
    }

    @Override
    public long getSelectedItemId()
    {
        Timber.d("getSelectedItemId()");
        return itemSelectedId;
    }

    /* Mainly for tests */
    public void setItemSelectedId(short itemSelectedId)
    {
        this.itemSelectedId = itemSelectedId;
    }

    // ==================================== ViewerIf ====================================

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        bean = IncidImportanciaBean.class.cast(viewBean);
        view.setOnItemSelectedListener(new ImportanciaSelectedListener());
        initSelectedItemId(savedState);
        controller.loadDataInSpinner();
    }

    //  ===================================== HELPERS ============================================

    @SuppressWarnings("WeakerAccess")
    public class ImportanciaSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            Timber.d("importanciaSpinner.onItemSelected()");
            itemSelectedId = (short) position;
            bean.setImportancia(itemSelectedId);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {
            Timber.d("importanciaSpinner.onNothingSelected()");
        }
    }
}
