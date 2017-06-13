package com.didekindroid.api;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.didekindroid.exception.UiExceptionIf;

import java.io.Serializable;

/**
 * User: pedro@didekin
 * Date: 16/03/17
 * Time: 13:29
 */
public interface ViewerIf<T extends View, C extends ControllerIf> {

    AppCompatActivity getActivity();

    UiExceptionIf.ActionForUiExceptionIf onErrorInObserver(Throwable error);

    int clearSubscriptions();

    T getViewInViewer();

    void doViewInViewer(Bundle savedState, Serializable viewBean);

    @Nullable
    C getController();

    void setController(@NonNull C controller);

    void saveState(Bundle savedState);

    @Nullable
    ViewerIf getParentViewer();
}
