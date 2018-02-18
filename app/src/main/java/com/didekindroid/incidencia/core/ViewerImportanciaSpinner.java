package com.didekindroid.incidencia.core;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.CtrlerSelectList;
import com.didekindroid.lib_one.api.ViewerIf;
import com.didekindroid.lib_one.api.ViewerSelectList;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.incidencia.IncidBundleKey.INCID_IMPORTANCIA_NUMBER;

/**
 * User: pedro@didekin
 * Date: 29/03/17
 * Time: 11:57
 */

public final class ViewerImportanciaSpinner extends
        ViewerSelectList<Spinner, CtrlerSelectList<String>, String> {

    @SuppressWarnings("WeakerAccess")
    IncidImportanciaBean bean;

    private ViewerImportanciaSpinner(@NonNull Spinner view, @NonNull ViewerIf parentViewer)
    {
        super(view, parentViewer.getActivity(), parentViewer);
    }

    public static ViewerImportanciaSpinner newViewerImportanciaSpinner(@NonNull Spinner view, @NonNull ViewerIf parentViewer)
    {
        Timber.d("newViewerImportanciaSpinner()");
        ViewerImportanciaSpinner instance = new ViewerImportanciaSpinner(view, parentViewer);
        instance.setController(new CtrlerSelectList<String>() {
            @Override
            public boolean loadItemsByEntitiyId(DisposableSingleObserver<List<String>> observer, Long... entityId)
            {
                throw new UnsupportedOperationException();
            }
        });
        return instance;
    }

    // ==================================== ViewerSelectListIf ====================================

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
        onSuccessLoadItemList(Arrays.asList(activity.getResources().getStringArray(R.array.IncidImportanciaArray)));
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
