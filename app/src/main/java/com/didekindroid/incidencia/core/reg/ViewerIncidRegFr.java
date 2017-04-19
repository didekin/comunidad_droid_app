package com.didekindroid.incidencia.core.reg;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;

import com.didekindroid.R;
import com.didekindroid.api.OnSpinnerClick;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.incidencia.core.IncidImportanciaBean;
import com.didekindroid.incidencia.core.IncidenciaBean;
import com.didekindroid.incidencia.core.ViewerAmbitoIncidSpinner;
import com.didekindroid.incidencia.core.ViewerImportanciaSpinner;
import com.didekindroid.incidencia.core.ViewerIncidRegEdit;
import com.didekindroid.usuariocomunidad.spinner.ViewerComuSpinner;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;

import timber.log.Timber;

import static com.didekindroid.incidencia.core.ViewerAmbitoIncidSpinner.newViewerAmbitoIncidSpinner;
import static com.didekindroid.incidencia.core.ViewerImportanciaSpinner.newViewerImportanciaSpinner;
import static com.didekindroid.usuariocomunidad.spinner.ViewerComuSpinner.newViewerComuSpinner;

/**
 * User: pedro@didekin
 * Date: 30/03/17
 * Time: 19:14
 */
class ViewerIncidRegFr extends ViewerIncidRegEdit implements OnSpinnerClick {

    AtomicReference<IncidenciaBean> atomIncidBean;
    AtomicReference<IncidImportanciaBean> atomIncidImportBean;
    ViewerAmbitoIncidSpinner viewerAmbitoIncidSpinner;
    ViewerImportanciaSpinner viewerImportanciaSpinner;
    ViewerComuSpinner viewerComuSpinner;


    @SuppressWarnings("WeakerAccess")
    ViewerIncidRegFr(View view, Activity activity, ViewerIf parentViewer)
    {
        super(view, activity, parentViewer);
        controller = null;  // Just for documentation.
        atomIncidBean = new AtomicReference<>(null);
        atomIncidImportBean = new AtomicReference<>(null);
    }

    static ViewerIncidRegFr newViewerIncidRegFr(View view, ViewerIf parentViewer)
    {
        Timber.d("newViewerIncidRegFr()");

        Activity activity = parentViewer.getActivity();
        ViewerIncidRegFr instance = new ViewerIncidRegFr(view, activity, parentViewer);
        instance.viewerAmbitoIncidSpinner =
                newViewerAmbitoIncidSpinner((Spinner) instance.getViewInViewer().findViewById(R.id.incid_reg_ambito_spinner), activity, instance);
        instance.viewerImportanciaSpinner =
                newViewerImportanciaSpinner((Spinner) instance.getViewInViewer().findViewById(R.id.incid_reg_importancia_spinner), activity, instance);
        instance.viewerComuSpinner =
                newViewerComuSpinner((Spinner) instance.getViewInViewer().findViewById(R.id.incid_reg_comunidad_spinner), activity, instance);
        return instance;
    }

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        atomIncidBean.compareAndSet(null, new IncidenciaBean());
        viewerAmbitoIncidSpinner.doViewInViewer(savedState, atomIncidBean.get());
        viewerComuSpinner.doViewInViewer(savedState, atomIncidBean.get());
        atomIncidImportBean.compareAndSet(null, new IncidImportanciaBean());
        viewerImportanciaSpinner.doViewInViewer(savedState, atomIncidImportBean.get());
    }

    @Override
    public int clearSubscriptions()
    {
        Timber.d("clearSubscriptions()");
        return viewerComuSpinner.clearSubscriptions()
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
    public long doOnClickItemId(long itemId)
    {
        Timber.d("doOnClickItemId()");
        // do nothing.
        return itemId;
    }
}