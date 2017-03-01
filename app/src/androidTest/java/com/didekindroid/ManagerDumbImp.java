package com.didekindroid;

import android.app.Activity;
import android.content.Intent;

import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf;
import com.didekindroid.testutil.MockActivity;

/**
 * User: pedro@didekin
 * Date: 28/02/17
 * Time: 10:25
 */
public class ManagerDumbImp<B> implements ManagerIf<B> {

    Activity activity = new MockActivity();

    @Override
    public Activity getActivity()
    {
        return activity;
    }

    @Override
    public UiExceptionIf.ActionForUiExceptionIf processViewerError(UiException ui)
    {
        return ui.processMe(activity, new Intent());
    }

    @Override
    public void replaceRootView(B initParamsForView)
    {
    }
}
