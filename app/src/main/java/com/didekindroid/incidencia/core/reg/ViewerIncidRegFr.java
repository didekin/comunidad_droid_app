package com.didekindroid.incidencia.core.reg;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.didekindroid.R;
import com.didekindroid.incidencia.core.IncidImportanciaBean;
import com.didekindroid.incidencia.core.ViewerImportanciaSpinner;
import com.didekindroid.lib_one.api.Controller;
import com.didekindroid.lib_one.api.ControllerIf;
import com.didekindroid.lib_one.api.ParentViewerIf;
import com.didekindroid.lib_one.api.SpinnerEventItemSelectIf;
import com.didekindroid.lib_one.api.SpinnerEventListener;
import com.didekindroid.lib_one.api.Viewer;
import com.didekindroid.lib_one.incidencia.IncidenciaBean;
import com.didekindroid.lib_one.incidencia.spinner.ViewerAmbitoIncidSpinner;
import com.didekindroid.usuariocomunidad.spinner.ComuSpinnerEventItemSelect;
import com.didekindroid.usuariocomunidad.spinner.ViewerComuSpinner;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;

import timber.log.Timber;

import static com.didekindroid.incidencia.core.ViewerImportanciaSpinner.newViewerImportanciaSpinner;
import static com.didekindroid.lib_one.incidencia.spinner.ViewerAmbitoIncidSpinner.newViewerAmbitoIncidSpinner;
import static com.didekindroid.usuariocomunidad.spinner.ViewerComuSpinner.newViewerComuSpinner;

/**
 * User: pedro@didekin
 * Date: 30/03/17
 * Time: 19:14
 */
class ViewerIncidRegFr extends Viewer<View, ControllerIf> implements SpinnerEventListener {

    AtomicReference<IncidenciaBean> atomIncidBean;
    AtomicReference<IncidImportanciaBean> atomIncidImportBean;
    ViewerAmbitoIncidSpinner viewerAmbitoIncidSpinner;
    ViewerImportanciaSpinner viewerImportanciaSpinner;
    ViewerComuSpinner viewerComuSpinner;


    @SuppressWarnings("WeakerAccess")
    ViewerIncidRegFr(View view, AppCompatActivity activity, @NonNull ParentViewerIf parentViewer)
    {
        super(view, activity, parentViewer);
        atomIncidBean = new AtomicReference<>(null);
        atomIncidImportBean = new AtomicReference<>(null);
    }

    static ViewerIncidRegFr newViewerIncidRegFr(View view, @NonNull ParentViewerIf parentViewer)
    {
        Timber.d("newViewerIncidRegFr()");

        AppCompatActivity activity = parentViewer.getActivity();
        ViewerIncidRegFr instance = new ViewerIncidRegFr(view, activity, parentViewer);
        instance.setController(new Controller());
        instance.viewerAmbitoIncidSpinner =
                newViewerAmbitoIncidSpinner(instance.getViewInViewer().findViewById(R.id.incid_reg_ambito_spinner), instance);
        instance.viewerImportanciaSpinner =
                newViewerImportanciaSpinner(instance.getViewInViewer().findViewById(R.id.incid_reg_importancia_spinner), instance);
        instance.viewerComuSpinner =
                newViewerComuSpinner(instance.getViewInViewer().findViewById(R.id.incid_comunidad_spinner), instance);
        return instance;
    }

    // .............................. ViewerIf ..................................

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        atomIncidBean.compareAndSet(null, new IncidenciaBean());
        viewerAmbitoIncidSpinner.doViewInViewer(savedState, atomIncidBean.get());
        viewerComuSpinner.doViewInViewer(savedState, viewBean);
        atomIncidImportBean.compareAndSet(null, new IncidImportanciaBean());
        viewerImportanciaSpinner.doViewInViewer(savedState, atomIncidImportBean.get());
    }

    @Override
    public int clearSubscriptions()
    {
        Timber.d("clearSubscriptions()");
        return controller.clearSubscriptions() +
                viewerComuSpinner.clearSubscriptions()
                + viewerImportanciaSpinner.clearSubscriptions()
                + viewerAmbitoIncidSpinner.clearSubscriptions();
    }

    @Override
    public void saveState(Bundle savedState)
    {
        Timber.d("saveState()");
        viewerAmbitoIncidSpinner.saveState(savedState);
        viewerImportanciaSpinner.saveState(savedState);
        viewerComuSpinner.saveState(savedState);
    }

    /**
     * @param errorMsg StringBuilder to write error messages.
     * @return null if the data are not valid.
     */
    IncidImportancia doIncidImportanciaFromView(StringBuilder errorMsg)
    {
        Timber.d("doIncidImportanciaFromView()");
        return atomIncidImportBean.get().makeIncidImportancia(errorMsg, activity.getResources(), view, atomIncidBean.get());
    }

    @Override
    public void doOnClickItemId(SpinnerEventItemSelectIf spinnerEventItemSelect)
    {
        Timber.d("doOnClickItemId()");
        if (ComuSpinnerEventItemSelect.class.isInstance(spinnerEventItemSelect)) {
            atomIncidBean.get().setComunidadId(spinnerEventItemSelect.getSpinnerItemIdSelect());
        }
    }
}
