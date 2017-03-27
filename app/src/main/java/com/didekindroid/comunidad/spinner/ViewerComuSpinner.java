package com.didekindroid.comunidad.spinner;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.didekindroid.api.CtrlerSpinnerIf;
import com.didekindroid.api.Viewer;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.api.ViewerSelectableIf;
import com.didekinlib.model.comunidad.Comunidad;

import timber.log.Timber;

import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.comunidad.spinner.CtrlerComuSpinner.newControllerComuSpinner;

/**
 * User: pedro@didekin
 * Date: 16/02/17
 * Time: 10:49
 */

public class ViewerComuSpinner extends Viewer<Spinner, CtrlerSpinnerIf>
        implements ViewerSelectableIf<Spinner, CtrlerSpinnerIf> {

    /**
     * This comunidadId can be set in three ways:
     * 1. The user selects one item in the spinner.
     * 2. The id is retrieved from savedInstanceState.
     * 3. The id is passed from the activity (in FCM notifications).
     */
    long itemSelectedId;

    ViewerComuSpinner(Spinner view, Activity activity, ViewerIf parentViewer)
    {
        super(view, activity, parentViewer);
    }

    public static ViewerComuSpinner newViewerComuSpinner(Spinner view, Activity activity, ViewerIf parentViewer)
    {
        Timber.d("newViewerComuSpinner()");
        ViewerComuSpinner instance = new ViewerComuSpinner(view, activity, parentViewer);
        instance.setController(newControllerComuSpinner(instance));
        return instance;
    }

    // ==================================== ViewerSelectableIf ====================================

    @Override
    public ViewerSelectableIf<Spinner, CtrlerSpinnerIf> initSelectedItem(Bundle savedState)
    {
        Timber.d("initSelectedItem()");

        if (savedState != null) {
            itemSelectedId = savedState.getLong(COMUNIDAD_ID.key, 0);
        } else {
            itemSelectedId = getItemIdInIntent();
        }
        return this;
    }

    @Override
    public void saveState(Bundle savedState)
    {
        Timber.d("saveState()");
        savedState.putLong(COMUNIDAD_ID.key, itemSelectedId);
    }

    /**
     *  @return the comunidadId selected in the spinner.
     */
    @Override
    public long getSelectedItem()
    {
        Timber.d("getSelectedItem()");
        return itemSelectedId;
    }

    @Override
    public long getItemIdInIntent()
    {
        return activity.getIntent().getLongExtra(COMUNIDAD_ID.key, 0);
    }

    // ==================================== ViewerIf ====================================

    @Override
    public void doViewInViewer(Bundle savedState)
    {
        Timber.d("doViewInViewer()");
        view.setOnItemSelectedListener(new ComuSelectedListener());
        initSelectedItem(savedState);
        controller.loadDataInSpinner();
    }

    //  ===================================== HELPERS ============================================

    @SuppressWarnings("WeakerAccess")
    class ComuSelectedListener implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            Timber.d("comunidadSpinner.onItemSelected()");
            Comunidad comunidad = (Comunidad) parent.getItemAtPosition(position);
            itemSelectedId = comunidad.getC_Id();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {
            Timber.d("comunidadSpinner.onNothingSelected()");
        }
    }
}
