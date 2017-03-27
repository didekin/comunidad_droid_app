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
import com.didekindroid.comunidad.spinner.ViewerComuSpinner;
import com.didekindroid.incidencia.list.ViewerIncidListByComu;

import timber.log.Timber;

import static com.didekindroid.comunidad.spinner.ViewerComuSpinner.newViewerComuSpinner;
import static com.didekindroid.incidencia.list.ViewerIncidListByComu.newListViewer;
import static com.didekindroid.util.CommonAssertionMsg.activity_should_be_instance_RootViewReplacer;
import static com.didekindroid.util.UIutils.assertTrue;

/**
 * User: pedro@didekin
 * Date: 18/03/17
 * Time: 11:01
 */
public class ViewerIncidSeeClose extends Viewer<View, ControllerIf> {

    private final RootViewReplacerIf rootViewReplacerIf;
    protected ViewerComuSpinner spinnerViewer;
    protected ViewerIncidListByComu listViewer;

    protected ViewerIncidSeeClose(View view, Activity activity)
    {
        super(view, activity, null);
        assertTrue(activity instanceof RootViewReplacerIf, activity_should_be_instance_RootViewReplacer);
        rootViewReplacerIf = (RootViewReplacerIf) activity;
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
    public void doViewInViewer(Bundle savedState)
    {
        Timber.d("doViewInViewer()");
        spinnerViewer.doViewInViewer(savedState);
        listViewer.doViewInViewer(savedState);
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
        spinnerViewer.saveState(savedState);
        listViewer.saveState(savedState);
    }

    @Override
    public void replaceRootView(@NonNull Bundle bundle)
    {
        Timber.d("replaceRootView()");
        rootViewReplacerIf.replaceRootView(bundle);
    }
}
