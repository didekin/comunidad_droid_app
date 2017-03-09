package com.didekindroid;

import android.app.Activity;

import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf;
import com.didekindroid.testutil.ConstantExecution;

import java.util.concurrent.atomic.AtomicReference;

import static com.didekindroid.testutil.ConstantExecution.MANAGER_AFTER_ERROR_CONTROL;
import static com.didekindroid.testutil.ConstantExecution.MANAGER_FLAG_INITIAL;
import static com.didekindroid.testutil.ConstantExecution.WRONG_FLAG_VALUE;
import static com.didekindroid.util.UIutils.assertTrue;

/**
 * User: pedro@didekin
 * Date: 28/02/17
 * Time: 10:25
 */
public class ManagerMock<B> implements ManagerIf<B> {

    private final Activity activity;
    public static final AtomicReference<String> flagManageMockExecMethod = new AtomicReference<>(MANAGER_FLAG_INITIAL);

    public ManagerMock(Activity activity)
    {
        this.activity = activity;
    }

    @Override
    public Activity getActivity()
    {
        return activity;
    }

    @Override
    public UiExceptionIf.ActionForUiExceptionIf processViewerError(UiException ui)
    {
        assertTrue(flagManageMockExecMethod.getAndSet(MANAGER_AFTER_ERROR_CONTROL).equals(MANAGER_FLAG_INITIAL), WRONG_FLAG_VALUE);
        return null;
    }

    @Override
    public void replaceRootView(B initParamsForView)
    {
        assertTrue(flagManageMockExecMethod.getAndSet(ConstantExecution.MANAGER_AFTER_REPLACED_VIEW).equals(MANAGER_FLAG_INITIAL), WRONG_FLAG_VALUE);
    }
}
