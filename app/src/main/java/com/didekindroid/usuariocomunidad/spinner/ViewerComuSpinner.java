package com.didekindroid.usuariocomunidad.spinner;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.didekindroid.api.CtrlerSpinnerIf;
import com.didekindroid.api.ViewBean;
import com.didekindroid.api.Viewer;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.api.ViewerSelectableIf;
import com.didekindroid.incidencia.core.IncidenciaBean;
import com.didekinlib.model.comunidad.Comunidad;

import timber.log.Timber;

import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.usuariocomunidad.spinner.CtrlerComuSpinner.newControllerComuSpinner;
import static com.didekindroid.util.CommonAssertionMsg.class_cast_unallowed;
import static com.didekindroid.util.UIutils.assertTrue;

/**
 * User: pedro@didekin
 * Date: 16/02/17
 * Time: 10:49
 */

@SuppressWarnings("ClassWithOnlyPrivateConstructors")
public class ViewerComuSpinner extends Viewer<Spinner, CtrlerSpinnerIf>
        implements ViewerSelectableIf<Spinner, CtrlerSpinnerIf> {

    /**
     * This comunidadId can be set in three ways:
     * 1. The user selects one item in the spinner.
     * 2. The id is retrieved from savedInstanceState.
     * 3. The id is passed from the activity (in FCM notifications).
     */
    @SuppressWarnings("WeakerAccess")
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
    public void initSelectedItemId(Bundle savedState)
    {
        Timber.d("initSelectedItemId()");

        if (savedState != null) {
            itemSelectedId = savedState.getLong(COMUNIDAD_ID.key, 0);
        } else {
            itemSelectedId = getItemIdInIntent();
        }
    }

    @Override
    public void saveState(Bundle savedState)
    {
        Timber.d("saveState()");
        if (savedState == null){
            savedState = new Bundle(1);
        }
        savedState.putLong(COMUNIDAD_ID.key, itemSelectedId);
    }

    /**
     *  @return the comunidadId selected in the spinner.
     */
    @Override
    public long getSelectedItemId()
    {
        Timber.d("getSelectedItemId()");
        return itemSelectedId;
    }

    @Override
    public long getItemIdInIntent()
    {
        Timber.d("getItemIdInIntent()");
        return activity.getIntent().getLongExtra(COMUNIDAD_ID.key, 0);
    }

    // ==================================== ViewerIf ====================================

    @Override
    public void doViewInViewer(Bundle savedState, ViewBean viewBean)
    {
        Timber.d("doViewInViewer()");
        assertTrue(viewBean instanceof IncidenciaBean, class_cast_unallowed);
        view.setOnItemSelectedListener(new ComuSelectedListener((IncidenciaBean) viewBean));
        initSelectedItemId(savedState);
        controller.loadDataInSpinner();
    }

    //  ===================================== HELPERS ============================================

    @SuppressWarnings("WeakerAccess")
    class ComuSelectedListener implements AdapterView.OnItemSelectedListener {

        private final IncidenciaBean bean;

        public ComuSelectedListener(IncidenciaBean viewBean)
        {
            bean = viewBean;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            Timber.d("comunidadSpinner.onItemSelected()");
            Comunidad comunidad = (Comunidad) parent.getItemAtPosition(position);
            itemSelectedId = comunidad.getC_Id();
            bean.setComunidadId(itemSelectedId);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {
            Timber.d("comunidadSpinner.onNothingSelected()");
        }
    }
}
