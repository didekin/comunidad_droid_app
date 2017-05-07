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
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.comunidad.Provincia;

import java.io.Serializable;

import timber.log.Timber;

import static com.didekindroid.comunidad.ComuBundleKey.PROVINCIA_ID;
import static com.didekindroid.comunidad.spinner.CtrlerProvinciaSpinner.newCtrlerProvinciaSpinner;

/**
 * User: pedro@didekin
 * Date: 05/05/17
 * Time: 16:31
 */
final class ViewerProvinciaSpinner extends
        ViewerSelectionList<Spinner, CtrlerSelectionList<Provincia>, Provincia> {

    Provincia provinciaIn;

    private ViewerProvinciaSpinner(Spinner view, Activity activity, ViewerIf parentViewer)
    {
        super(view, activity, parentViewer);
    }

    static ViewerProvinciaSpinner newViewerProvinciaSpinner(Spinner spinner, Activity activity, ViewerIf parentViewer)
    {
        Timber.d("newViewerProvinciaSpinner()");
        ViewerProvinciaSpinner instance = new ViewerProvinciaSpinner(spinner, activity, parentViewer);
        instance.setController(newCtrlerProvinciaSpinner(instance));
        return instance;
    }

    // ==================================== ViewerSelectionListIf ====================================

    @Override
    public void initSelectedItemId(Bundle savedState)
    {
        Timber.d("initSelectedItemId()");
        if (savedState != null && savedState.containsKey(PROVINCIA_ID.key)) {
            itemSelectedId = savedState.getLong(PROVINCIA_ID.key, 0);
        } else if (provinciaIn != null && provinciaIn.getProvinciaId() > 0) {
            itemSelectedId = provinciaIn.getProvinciaId();
        } else {
            itemSelectedId = 0;
        }
    }

    @Override
    public int getSelectedPositionFromItemId(long itemId)
    {
        int position = 0;
        boolean isFound = false;
        if (itemId > 0L) {
            short provinciaInId;
            do {
                provinciaInId = ((Provincia) view.getItemAtPosition(position)).getProvinciaId();
                if (provinciaInId == (short)itemId) {
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
        provinciaIn = viewBean != null ? Provincia.class.cast(viewBean) : null;
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

    @SuppressWarnings("WeakerAccess")
    class ProvinciaSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            Timber.d("onItemSelected()");
            Provincia provincia = (Provincia) parent.getItemAtPosition(position);
            provinciaIn = provincia;
            itemSelectedId = provincia.getProvinciaId();
            SpinnerClickHandler.class.cast(parentViewer).doOnClickItemId(itemSelectedId, ViewerProvinciaSpinner.this.getClass());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {
            Timber.d("onNothingSelected()");
        }
    }
}
