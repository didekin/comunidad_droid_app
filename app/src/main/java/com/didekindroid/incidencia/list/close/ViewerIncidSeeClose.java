package com.didekindroid.incidencia.list.close;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Spinner;

import com.didekindroid.R;
import com.didekindroid.api.ControllerIf;
import com.didekindroid.api.RootViewReplacerIf;
import com.didekindroid.api.Viewer;
import com.didekindroid.incidencia.list.ViewerIncidListByComu;
import com.didekindroid.usuariocomunidad.spinner.ViewerComuSpinner;

import java.io.Serializable;

import timber.log.Timber;

import static com.didekindroid.incidencia.list.ViewerIncidListByComu.newListViewer;
import static com.didekindroid.usuariocomunidad.spinner.ViewerComuSpinner.newViewerComuSpinner;
import static com.didekindroid.util.CommonAssertionMsg.activity_should_be_instance_RootViewReplacer;
import static com.didekindroid.util.UIutils.assertTrue;

/**
 * User: pedro@didekin
 * Date: 18/03/17
 * Time: 11:01
 */
public class ViewerIncidSeeClose extends Viewer<View, ControllerIf> implements RootViewReplacerIf {

    protected ViewerComuSpinner spinnerViewer;
    protected ViewerIncidListByComu listViewer;

    protected ViewerIncidSeeClose(View view, Activity activity)
    {
        super(view, activity, null);
        assertTrue(activity instanceof RootViewReplacerIf, activity_should_be_instance_RootViewReplacer);
        controller = null; // Just for documentation.
    }

    static ViewerIncidSeeClose newViewerIncidSeeClose(View view, Activity activity)
    {
        ViewerIncidSeeClose parentInstance = new ViewerIncidSeeClose(view, activity);
        parentInstance.spinnerViewer = newViewerComuSpinner((Spinner) view.findViewById(R.id.incid_reg_comunidad_spinner), activity, parentInstance);
        parentInstance.listViewer = newListViewer(view, activity, parentInstance);
        parentInstance.listViewer.setController(new CtrlerIncidSeeCloseByComu(parentInstance.listViewer));
        return parentInstance;
    }

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        spinnerViewer.doViewInViewer(savedState, viewBean);
        listViewer.doViewInViewer(savedState, viewBean);
    }

    @Override
    public int clearSubscriptions()
    {
        Timber.d("clearSubscriptions()");
        return spinnerViewer.clearSubscriptions() + listViewer.clearSubscriptions();
    }

    @Override
    public void saveState(Bundle savedState)
    {
        Timber.d("saveState()");
        if (savedState == null) {
            savedState = new Bundle();
        }
        spinnerViewer.saveState(savedState);
        listViewer.saveState(savedState);
    }

    @Override
    public void replaceRootView(@NonNull Bundle bundle)
    {
        Timber.d("replaceRootView()");
        RootViewReplacerIf.class.cast(activity).replaceRootView(bundle);
    }
}
