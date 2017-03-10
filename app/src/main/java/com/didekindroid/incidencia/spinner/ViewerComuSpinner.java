package com.didekindroid.incidencia.spinner;

import android.os.Bundle;
import android.widget.Spinner;

import com.didekindroid.api.ManagerIf;
import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf.ActionForUiExceptionIf;
import com.didekindroid.incidencia.spinner.ManagerComuSpinnerIf.ControllerComuSpinnerIf;
import com.didekindroid.incidencia.spinner.ManagerComuSpinnerIf.ViewerComuSpinnerIf;

import timber.log.Timber;

import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.spinner.ControllerComuSpinner.newControllerComuSpinner;

/**
 * User: pedro@didekin
 * Date: 16/02/17
 * Time: 10:49
 */
@SuppressWarnings("WeakerAccess")
public final class ViewerComuSpinner implements ViewerComuSpinnerIf {

    final ManagerComuSpinnerIf<?> manager;
    final private Spinner spinnerView;
    long comunidadSelectedId;
    private ControllerComuSpinnerIf controller;

    private ViewerComuSpinner(ManagerComuSpinnerIf<?> spinnerManager)
    {
        manager = spinnerManager;
        spinnerView = spinnerManager.initSpinnerView();
    }

    public static ViewerComuSpinner newComuSpinnerViewer(ManagerComuSpinnerIf<?> comuSpinnerManager)
    {
        Timber.d("newComuSpinnerViewer(comuSpinnerManager)");
        ViewerComuSpinner instance = new ViewerComuSpinner(comuSpinnerManager);
        instance.setController(instance);
        return instance;
    }

    // ==================================== ViewerComuSpinnerI ====================================

    @Override
    public ViewerComuSpinnerIf setDataInView(Bundle savedState)
    {
        Timber.d("setDataInView()");
        initSelectedIndex(savedState);
        controller.loadDataInSpinner();
        return this;
    }

    public long getComunidadSelectedId()
    {
        Timber.d("getComunidadSelectedId()");
        return comunidadSelectedId;
    }

    // ==================================== ViewerWithSelectIf ====================================

    @Override
    public ViewerComuSpinnerIf initSelectedIndex(Bundle savedState)
    {
        Timber.d("initSelectedIndex()");

        if (savedState != null) {
            // From savedInstanceState.
            comunidadSelectedId = savedState.getLong(COMUNIDAD_ID.key, 0);
        } else {
            // From intent.
            comunidadSelectedId = manager.getComunidadIdInIntent();
        }
        return this;
    }

    @Override
    public void saveSelectedIndex(Bundle savedState)
    {
        Timber.d("saveSelectedIndex()");
        savedState.putLong(COMUNIDAD_ID.key, comunidadSelectedId);
    }

    // ==================================== ViewerIf ====================================

    @Override
    public Spinner getViewInViewer()
    {
        Timber.d("getViewInViewer()");
        return spinnerView;
    }

    @Override
    public ManagerIf getManager()
    {
        Timber.d("getContext()");
        return manager;
    }

    @Override
    public ActionForUiExceptionIf processControllerError(UiException ui)
    {
        Timber.d("processControllerError()");
        return manager.processViewerError(ui);
    }

    @Override
    public int clearControllerSubscriptions()
    {
        Timber.d("clearControllerSubscriptions()");
        return controller.clearSubscriptions();
    }

    //  ===================================== HELPERS ============================================

    ControllerComuSpinnerIf getController()
    {
        return controller;
    }

    void setController(ViewerComuSpinnerIf viewer)
    {
        controller = newControllerComuSpinner(viewer);

    }

    // Mainly to allow injection of mock controllers.
    void injectController(ControllerComuSpinnerIf controller)
    {
        this.controller = controller;
    }
}
