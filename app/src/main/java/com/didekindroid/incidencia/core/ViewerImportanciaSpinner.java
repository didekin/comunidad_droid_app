package com.didekindroid.incidencia.core;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.didekindroid.api.CtrlerSelectionList;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.api.ViewerSelectionList;

import java.io.Serializable;
import java.util.List;

import timber.log.Timber;

import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_NUMBER;

/**
 * User: pedro@didekin
 * Date: 29/03/17
 * Time: 11:57
 */

public final class ViewerImportanciaSpinner extends ViewerSelectionList<Spinner, CtrlerSelectionList<String>, String> {

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

    // ==================================== ViewerSelectionListIf ====================================

    @Override
    public void onSuccessLoadItems(List<String> incidCloseList)
    {
        Timber.d("onSuccessLoadItems()");

        ArrayAdapter<String> adapter = ViewerSelectionList.getArrayAdapterForSpinner(String.class, activity);
        adapter.addAll(incidCloseList);
        view.setSelection(getSelectedViewFromItemId(itemSelectedId));
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

    // ==================================== ViewerIf ====================================

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        bean = IncidImportanciaBean.class.cast(viewBean);
        view.setOnItemSelectedListener(new ImportanciaSelectedListener());
        initSelectedItemId(savedState);
        CtrlerSelectionList.class.cast(controller).loadItemsByEntitiyId();
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

    //  ===================================== HELPERS ============================================

    @SuppressWarnings("WeakerAccess")
    public class ImportanciaSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            Timber.d("importanciaSpinner.onItemSelected()");
            itemSelectedId = (short) position;
            bean.setImportancia((short) itemSelectedId);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {
            Timber.d("importanciaSpinner.onNothingSelected()");
        }
    }
}
