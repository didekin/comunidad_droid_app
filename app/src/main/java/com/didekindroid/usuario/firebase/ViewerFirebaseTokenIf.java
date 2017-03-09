package com.didekindroid.usuario.firebase;

import android.view.View;

import com.didekindroid.ManagerIf;
import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf;
import com.didekindroid.incidencia.core.ControllerFirebaseTokenIf;

import java.util.concurrent.atomic.AtomicReference;

/**
 * User: pedro@didekin
 * Date: 03/03/17
 * Time: 15:06
 */

public interface ViewerFirebaseTokenIf<B> extends ManagerIf.ViewerIf {

    void setController(ControllerFirebaseTokenIf controllerIn);

    void checkGcmTokenAsync();

    class ViewerFirebaseToken implements ViewerFirebaseTokenIf {

        final AtomicReference<ControllerFirebaseTokenIf> controller = new AtomicReference<>();
        final ManagerIf manager;

        ViewerFirebaseToken(ManagerIf manager)
        {
            this.manager = manager;
        }

        public static ViewerFirebaseTokenIf newViewerFirebaseToken(ManagerIf manager)
        {
            ViewerFirebaseTokenIf viewer = new ViewerFirebaseToken(manager);
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
}
