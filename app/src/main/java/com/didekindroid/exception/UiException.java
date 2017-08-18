package com.didekindroid.exception;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.didekindroid.R;
import com.didekinlib.http.ErrorBean;

import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.didekindroid.AppInitializer.creator;
import static com.didekindroid.util.UIutils.assertTrue;
import static com.didekindroid.util.UIutils.makeToast;

/**
 * User: pedro@didekin
 * Date: 17/11/16
 * Time: 17:41
 */
public class UiException extends Exception implements UiExceptionIf {

    private static final String actionForException_notNull = "ActionForException not null";
    private final ErrorBean errorBean;
    private final UiExceptionRouterIf exceptionRouter;

    public UiException(ErrorBean errorBean)
    {
        this(errorBean, creator.get().getUiExceptionRouter());
    }

    public UiException(ErrorBean errorBean, UiExceptionRouterIf exceptionRouter)
    {
        this.errorBean = errorBean;
        this.exceptionRouter = exceptionRouter;
    }

    @Override
    public void processMe(@NonNull Activity activity)
    {
        Timber.d("processMe(Activity activity): %s%n", errorBean.getMessage());

        ActionForUiExceptionIf action = exceptionRouter.getActionForException(this);
        if (action == null) { // NO entry in exceptions dealer's table for error bean message.
            makeToast(activity, R.string.exception_generic_message);
            Intent intent = new Intent();
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setAction(Intent.ACTION_MAIN);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TOP);
            activity.startActivity(intent);
        } else {
            processMe(activity, action);
        }
    }

    @Override
    public void processMe(@NonNull Activity activity, @NonNull Intent intent)
    {
        Timber.d("processMe(Activity activity, Intent intent): %s%n", errorBean.getMessage());

        ActionForUiExceptionIf actionForException = exceptionRouter.getActionForException(this);
        assertTrue(actionForException != null, actionForException_notNull);

        if (actionForException.getToastResourceId() > 0) {
            makeToast(activity, actionForException.getToastResourceId());
        }
        activity.startActivity(intent);
    }

    @Override
    public void processMe(@NonNull Activity activity, @NonNull ActionForUiExceptionIf actionForException)
    {
        Timber.d("processMe(Activity activity, ActionForUiExceptionIf actionForException): %s%n", errorBean.getMessage());

        if (actionForException.getToastResourceId() > 0) {
            makeToast(activity, actionForException.getToastResourceId());
        }
        if (actionForException.getActivityToGoClass() != null) {
            Intent intent = new Intent(activity, actionForException.getActivityToGoClass());
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        }
    }

    @Override
    public ErrorBean getErrorBean()
    {
        return errorBean;
    }
}
