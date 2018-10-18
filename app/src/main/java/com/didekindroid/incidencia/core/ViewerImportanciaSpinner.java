package com.didekindroid.incidencia.core;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.CtrlerSelectList;
import com.didekindroid.lib_one.api.ViewerIf;
import com.didekindroid.lib_one.api.ViewerSelectList;

import java.io.Serializable;
import java.util.List;

import io.reactivex.functions.Function;
import timber.log.Timber;

import static com.didekindroid.incidencia.IncidBundleKey.INCID_IMPORTANCIA_NUMBER;
import static java.util.Arrays.asList;

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
        return new ViewerImportanciaSpinner(view, parentViewer);
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

    @Override
    public Function<String, Long> getBeanIdFunction()
    {
        Timber.d("getBeanIdFunction()");
        throw new UnsupportedOperationException();
    }

    @Override
    public void onSuccessLoadItemList(List<String> itemsList)
    {
        Timber.d("onSuccessLoadItemList()");
        ArrayAdapter<String> adapter = getArrayAdapterForSpinner(activity);
        adapter.addAll(itemsList);
        view.setAdapter(adapter);
        view.setSelection((int) itemSelectedId);
    }

    /* ==================================== ViewerIf ====================================*/

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        bean = IncidImportanciaBean.class.cast(viewBean);
        view.setOnItemSelectedListener(new ImportanciaSelectedListener());
        initSelectedItemId(savedState);
        onSuccessLoadItemList(asList(activity.getResources().getStringArray(R.array.IncidImportanciaArray)));
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
