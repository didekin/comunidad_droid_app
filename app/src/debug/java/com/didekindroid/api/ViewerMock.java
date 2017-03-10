package com.didekindroid.api;

import android.view.View;

import com.didekindroid.api.ManagerIf.ViewerIf;
import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf.ActionForUiExceptionIf;

import java.util.concurrent.atomic.AtomicReference;

import static com.didekindroid.testutil.ConstantExecution.VIEWER_AFTER_ERROR_CONTROL;
import static com.didekindroid.testutil.ConstantExecution.VIEWER_FLAG_INITIAL;
import static com.didekindroid.testutil.ConstantExecution.WRONG_FLAG_VALUE;
import static com.didekindroid.util.UIutils.assertTrue;

/**
 * User: pedro@didekin
 * Date: 24/02/17
 * Time: 17:23
 */
public class ViewerMock<T extends View, B> implements ViewerIf<T, B> {

    public static final AtomicReference<String> flagViewerMockMethodExec = new AtomicReference<>(VIEWER_FLAG_INITIAL);

    protected final ManagerIf<B> manager;
    protected T viewInViewer;

    public ViewerMock(ManagerIf<B> manager)
    {
        this.manager = manager;
    }

    @Override
    public ManagerIf<B> getManager()
    {
        return manager;
    }

    @Override
    public ActionForUiExceptionIf processControllerError(UiException e)
    {
        assertTrue(flagViewerMockMethodExec.getAndSet(VIEWER_AFTER_ERROR_CONTROL).equals(VIEWER_FLAG_INITIAL), WRONG_FLAG_VALUE);
        return null;
    }

    @Override
    public int clearControllerSubscriptions()
    {
        return 99;
    }

    @Override
    public T getViewInViewer()
    {
        return viewInViewer;
    }
}
