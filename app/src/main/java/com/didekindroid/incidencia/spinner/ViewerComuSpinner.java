package com.didekindroid.incidencia.spinner;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Spinner;

import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf.ActionForUiExceptionIf;
import com.didekindroid.incidencia.spinner.ManagerComuSpinnerIf.ControllerComuSpinnerIf;
import com.didekindroid.incidencia.spinner.ManagerComuSpinnerIf.ViewerComuSpinnerIf;
import com.didekinlib.model.comunidad.Comunidad;

import timber.log.Timber;

import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_LIST_INDEX;
import static com.didekindroid.incidencia.spinner.ControllerComuSpinner.newControllerComuSpinner;

/**
 * User: pedro@didekin
 * Date: 16/02/17
 * Time: 10:49
 */
@SuppressWarnings("WeakerAccess")
public final class ViewerComuSpinner implements ViewerComuSpinnerIf {

    final ManagerComuSpinnerIf manager;
    final private Spinner spinnerView;
    int comunidadSelectedIndex;
    private ControllerComuSpinnerIf controller;

    private ViewerComuSpinner(ManagerComuSpinnerIf spinnerManager)
    {
        manager = spinnerManager;
        spinnerView = spinnerManager.initSpinnerView();
    }

    public static ViewerComuSpinner newComuSpinnerViewer(ManagerComuSpinnerIf comuSpinnerManager)
    {
        Timber.d("newComuSpinnerViewer(comuSpinnerManager)");
        ViewerComuSpinner instance = new ViewerComuSpinner(comuSpinnerManager);
        instance.setController(instance);
        return instance;
    }

    @Override
    public Spinner getViewInViewer()
    {
        Timber.d("getViewInViewer()");
        return spinnerView;
    }

    @Override
    public void replaceView(Object initParams)
    {
        Timber.d("replaceView()");
        throw new UnsupportedOperationException();
    }

    @Override
    public ViewerComuSpinnerIf setDataInView()
    {
        Timber.d("setDataInView()");
        controller.loadDataInSpinner();
        return this;
    }

    @Override
    public Activity getActivity()
    {
        Timber.d("getContext()");
        return manager.getActivity();
    }

    @Override
    public ViewerComuSpinnerIf initSelectedIndex(Bundle savedState)
    {
        Timber.d("initSelectedIndex()");

        // From savedInstanceState.
        if (savedState != null) {
            comunidadSelectedIndex = savedState.getInt(COMUNIDAD_LIST_INDEX.key, 0);
            return this;
        }

        // From intent.
        long idToSearchFor = manager.getComunidadIdInIntent();

        if (idToSearchFor > 0L) {
            int position = 0;
            comunidadSelectedIndex = 0;
            do {
                if (((Comunidad) spinnerView.getItemAtPosition(position)).getC_Id() == idToSearchFor) {
                    comunidadSelectedIndex = position;
                    break;
                }
            } while (++position < spinnerView.getCount());
        }
        return this;
    }

    @Override
    public ActionForUiExceptionIf processControllerError(UiException ui)
    {
        Timber.d("processControllerError()");
        return manager.processViewerError(ui);
    }

    @Override
    public void saveSelectedIndex(Bundle savedState)
    {
        Timber.d("saveSelectedIndex()");
        savedState.putInt(COMUNIDAD_LIST_INDEX.key, comunidadSelectedIndex);
    }

    @Override
    public int getComunidadSelectedIndex()
    {
        Timber.d("getComunidadSelectedIndex()");
        return comunidadSelectedIndex;
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
        this.controller = newControllerComuSpinner(viewer);
    }

    // Mainly to allow injection of mock controllers.
    void injectController(ControllerComuSpinnerIf controller)
    {
        this.controller = controller;
    }
}
