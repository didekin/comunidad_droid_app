package com.didekindroid;

import android.app.Activity;
import android.view.View;

import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf.ActionForUiExceptionIf;
import com.didekindroid.security.IdentityCacher;

import io.reactivex.disposables.CompositeDisposable;

/**
 * User: pedro@didekin
 * Date: 20/02/17
 * Time: 10:11
 */

public interface ManagerIf<B> {

    Activity getActivity();

    ActionForUiExceptionIf processViewerError(UiException ui);

    void replaceRootView(B initParamsForView);

    // .................... VIEWER .....................

    interface ViewerIf<T extends View, B> {

        ManagerIf<B> getManager();

        ActionForUiExceptionIf processControllerError(UiException ui);

        int clearControllerSubscriptions();

        T getViewInViewer();
    }

    // ................. CONTROLLER ....................

    interface ControllerIf {

        CompositeDisposable getSubscriptions();

        void processReactorError(Throwable e);

        int clearSubscriptions();

        /**
         * It allows to inject different implementations of viewer in ControllerIdentityAbs.processReactorError().
         */
        ViewerIf getViewer();
    }

    interface ControllerIdentityIf extends ControllerIf {

        boolean isRegisteredUser();

        void updateIsRegistered(boolean isRegisteredUser);

        IdentityCacher getIdentityCacher();
    }
}
