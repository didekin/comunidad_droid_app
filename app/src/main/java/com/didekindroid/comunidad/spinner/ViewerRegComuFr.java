package com.didekindroid.comunidad.spinner;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Spinner;

import com.didekindroid.R;
import com.didekindroid.api.Controller;
import com.didekindroid.api.CtrlerSelectionList;
import com.didekindroid.api.SpinnerClickHandler;
import com.didekindroid.api.Viewer;
import com.didekindroid.api.ViewerIf;
import com.didekindroid.api.ViewerSelectionList;

import java.io.Serializable;

import timber.log.Timber;

import static com.didekindroid.comunidad.spinner.ViewerComuAutonomaSpinner.newViewerComuAutonomaSpinner;
import static com.didekindroid.comunidad.spinner.ViewerProvinciaSpinner.newViewerProvinciaSpinner;
import static com.didekindroid.comunidad.spinner.ViewerTipoViaSpinner.newViewerTipoViaSpinner;

/**
 * User: pedro@didekin
 * Date: 04/05/17
 * Time: 12:28
 */

final class ViewerRegComuFr extends Viewer<View, Controller> implements SpinnerClickHandler {

    private ViewerTipoViaSpinner tipoViaSpinner;
    private ViewerComuAutonomaSpinner comuAutonomaSpinner;
    private ViewerProvinciaSpinner provinciaSpinner;
    private ViewerMunicipioSpinner municipioSpinner;

    private ViewerRegComuFr(View view, Activity activity, ViewerIf parentViewer)
    {
        super(view, activity, parentViewer);
        controller = null;  // Just for documentation.
    }

    static ViewerRegComuFr newViewerRegComuFr(View view, Activity activity, ViewerIf parentViewer)
    {
        Timber.d("newViewerRegComuFr()");
        ViewerRegComuFr instance = new ViewerRegComuFr(view, activity, parentViewer);
        instance.tipoViaSpinner =
                newViewerTipoViaSpinner((Spinner) instance.getViewInViewer().findViewById(R.id.tipo_via_spinner), activity, instance);
        instance.comuAutonomaSpinner =
                newViewerComuAutonomaSpinner((Spinner) instance.getViewInViewer().findViewById(R.id.autonoma_comunidad_spinner), activity, instance);
        instance.provinciaSpinner =
                newViewerProvinciaSpinner((Spinner) instance.getViewInViewer().findViewById(R.id.provincia_spinner), activity, instance);
        return instance;
    }

    // ==================================== ViewerIf ====================================

    @Override
    public void doViewInViewer(Bundle savedState, Serializable viewBean)
    {
        Timber.d("doViewInViewer()");
        tipoViaSpinner.doViewInViewer(savedState, viewBean);
        comuAutonomaSpinner.doViewInViewer(savedState, viewBean);
        provinciaSpinner.doViewInViewer(savedState, viewBean);

    }

    @Override
    public int clearSubscriptions()
    {
        Timber.d("clearSubscriptions()");
        return tipoViaSpinner.clearSubscriptions()
                + comuAutonomaSpinner.clearSubscriptions()
                + provinciaSpinner.clearSubscriptions();
    }

    @Override
    public void saveState(Bundle savedState)
    {
        Timber.d("saveState()");
        tipoViaSpinner.saveState(savedState);
        comuAutonomaSpinner.saveState(savedState);
        provinciaSpinner.saveState(savedState);
    }

    // ===================================  SpinnerClickHandler  =================================

    @SuppressWarnings("ConstantConditions")
    @Override
    public long doOnClickItemId(long itemId, @NonNull Class<? extends ViewerSelectionList> viewerSourceEvent)
    {
        if (ViewerComuAutonomaSpinner.class.isInstance(viewerSourceEvent)) {
            provinciaSpinner.getController().loadItemsByEntitiyId(itemId);
        }
        if (ViewerProvinciaSpinner.class.isInstance(viewerSourceEvent)){
            municipioSpinner.getController().loadItemsByEntitiyId(itemId);
        }
        return itemId;
    }
}
