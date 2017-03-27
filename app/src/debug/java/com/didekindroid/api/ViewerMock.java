package com.didekindroid.api;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

/**
 * User: pedro@didekin
 * Date: 24/02/17
 * Time: 17:23
 */
public class ViewerMock<T extends View, C extends ControllerIf> extends Viewer<T,C> {

    public ViewerMock(T view, Activity activity, ViewerIf<T, C> parentViewer)
    {
        super(view, activity, null);
    }

    @Override
    public void doViewInViewer(Bundle savedState)
    {
    }
}
