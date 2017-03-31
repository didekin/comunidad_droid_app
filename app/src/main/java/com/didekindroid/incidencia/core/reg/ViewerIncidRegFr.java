package com.didekindroid.incidencia.core.reg;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Spinner;

import com.didekindroid.R;
import com.didekindroid.api.ControllerIf;
import com.didekindroid.api.RootViewReplacerIf;
import com.didekindroid.api.ViewBean;
import com.didekindroid.api.Viewer;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.incidencia.core.IncidImportanciaBean;
import com.didekindroid.incidencia.core.IncidenciaBean;
import com.didekindroid.usuariocomunidad.spinner.ViewerComuSpinner;
import com.didekinlib.model.incidencia.dominio.IncidImportancia;

import timber.log.Timber;

import static com.didekindroid.incidencia.core.reg.ViewerAmbitoIncidSpinner.newViewerAmbitoIncidSpinner;
import static com.didekindroid.incidencia.core.reg.ViewerImportanciaSpinner.newViewerImportanciaSpinner;
import static com.didekindroid.util.CommonAssertionMsg.activity_should_be_instance_RootViewReplacer;
import static com.didekindroid.util.UIutils.assertTrue;

/**
 * User: pedro@didekin
 * Date: 30/03/17
 * Time: 19:14
 */

final class ViewerIncidRegFr extends Viewer<View, ControllerIf> {

    private final IncidenciaBean incidenciaBean;
    private final IncidImportanciaBean incidImportanciaBean;
    private ViewerAmbitoIncidSpinner viewerAmbitoIncidSpinner;
    private ViewerImportanciaSpinner viewerImportanciaSpinner;
    private ViewerComuSpinner viewerComuSpinner;


    private ViewerIncidRegFr(View view, Activity activity, ViewerIf parentViewer)
    {
        super(view, activity, parentViewer);
        assertTrue(activity instanceof RootViewReplacerIf, activity_should_be_instance_RootViewReplacer);
        controller = null;  // Just for documentation.
        incidenciaBean = new IncidenciaBean();
        incidImportanciaBean = new IncidImportanciaBean();
    }

    static ViewerIncidRegFr newViewerIncidReg(View view, Activity activity, ViewerIf parentViewer)
    {
        ViewerIncidRegFr instance = new ViewerIncidRegFr(view, activity, parentViewer);
        instance.viewerAmbitoIncidSpinner = newViewerAmbitoIncidSpinner((Spinner) view.findViewById(R.id.incid_reg_ambito_spinner), activity, instance);
        instance.viewerImportanciaSpinner = newViewerImportanciaSpinner((Spinner) view.findViewById(R.id.incid_reg_importancia_spinner), activity, instance);
        instance.viewerComuSpinner = ViewerComuSpinner.newViewerComuSpinner((Spinner) view.findViewById(R.id.incid_reg_comunidad_spinner), activity, instance);
        return instance;
    }

    @Override
    public void doViewInViewer(Bundle savedState, ViewBean viewBean)
    {
        Timber.d("doViewInViewer()");
        viewerAmbitoIncidSpinner.doViewInViewer(savedState, incidenciaBean);
        viewerComuSpinner.doViewInViewer(savedState, incidenciaBean);
        viewerImportanciaSpinner.doViewInViewer(savedState, incidImportanciaBean);
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
        if (savedState == null) {
            savedState = new Bundle();
        }
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
        return incidImportanciaBean.makeIncidImportancia(errorMsg, activity.getResources(), view, incidenciaBean);
    }
}
