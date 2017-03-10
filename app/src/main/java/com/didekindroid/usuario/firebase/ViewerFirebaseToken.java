package com.didekindroid.usuario.firebase;

import android.view.View;

import com.didekindroid.api.ManagerIf;
import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf;
import com.didekindroid.incidencia.core.ControllerFirebaseTokenIf;

import java.util.concurrent.atomic.AtomicReference;

/**
 * User: pedro@didekin
 * Date: 09/03/17
 * Time: 21:11
 */
public class ViewerFirebaseToken implements ViewerFirebaseTokenIf {

    private final AtomicReference<ControllerFirebaseTokenIf> controller = new AtomicReference<>();
    private final ManagerIf manager;

    ViewerFirebaseToken(ManagerIf manager)
    {
        this.manager = manager;
    }

    public static ViewerFirebaseTokenIf newViewerFirebaseToken(ManagerIf manager)
    {
        ViewerFirebaseTokenIf viewer = new com.didekindroid.usuario.firebase.ViewerFirebaseToken(manager);
        viewer.setController(new ControllerFirebaseToken(viewer));
        return viewer;
    }

    @Override
    public ManagerIf getManager()
    {
        return manager;
    }

    @Override
    public UiExceptionIf.ActionForUiExceptionIf processControllerError(UiException ui)
    {
        return manager.processViewerError(ui);
    }

    @Override
    public int clearControllerSubscriptions()
    {
        return controller.get().clearSubscriptions();
    }

    @Override
    public View getViewInViewer()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setController(ControllerFirebaseTokenIf controllerIn)
    {
        controller.compareAndSet(null, controllerIn);
    }

    @Override
    public void checkGcmTokenAsync()
    {
        controller.get().checkGcmToken();
    }
}
