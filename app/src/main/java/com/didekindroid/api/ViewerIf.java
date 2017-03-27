package com.didekindroid.api;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import java.util.List;

/**
 * User: pedro@didekin
 * Date: 16/03/17
 * Time: 13:29
 */
public interface ViewerIf<T extends View, C extends ControllerIf> {

    Activity getActivity();

    UiExceptionIf.ActionForUiExceptionIf processControllerError(UiException ui);

    int clearSubscriptions();

    T getViewInViewer();

    void doViewInViewer(Bundle savedState);

    C getController();

    void setController(C controller);

    void replaceRootView(@NonNull Bundle bundle);

    void saveState(Bundle savedState);

    @Nullable
    ViewerIf getParentViewer();
}
