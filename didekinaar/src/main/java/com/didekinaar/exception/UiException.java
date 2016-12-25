package com.didekinaar.exception;

import android.app.Activity;
import android.content.Intent;

import com.didekin.common.exception.ErrorBean;
import com.didekinaar.R;

import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.didekinaar.exception.UiActionExceptionUtil.finishActivity;
import static com.didekinaar.utils.UIutils.makeToast;

/**
 * User: pedro@didekin
 * Date: 17/11/16
 * Time: 17:41
 */
public class UiException extends Exception implements UiExceptionIf {

    private final ErrorBean errorBean;

    public UiException(ErrorBean errorBean)
    {
        this.errorBean = errorBean;
    }

    @Override
    public void processMe(Activity activity, Intent intent)
    {
        Timber.d("processMe(): %s %s%n", activity.getComponentName().getClassName(), errorBean.getMessage());

        // Default case GENERIC_INTERNAL_ERROR: back to the beginning of the app.
        if (getUiActionException() == null){
            makeToast(activity, R.string.exception_generic_message);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setAction(Intent.ACTION_MAIN);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TOP);
            activity.startActivity(intent);
            finishActivity(activity, intent);
        }

        if (getUiActionException().getToastResourceId() > 0) {
            makeToast(activity, getUiActionException().getToastResourceId());
        }

        if (getUiActionException().getActivityToGoClass() != null) {
            intent.setClass(activity, getUiActionException().getActivityToGoClass());
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            finishActivity(activity, intent);
        }
    }

    @Override
    public ErrorBean getErrorBean()
    {
        return errorBean;
    }

    /**
     *  Method to be overwritten by subclasses in the final apps.
     *  Each app defines its own exception dealing.
     * */
    protected ActionForUiExceptionIf getUiActionException()
    {
        throw new UnsupportedOperationException("This method should be overwritten by final apps.");
    }

    public static class ActionForUiException implements ActionForUiExceptionIf {

        private final Class<? extends Activity> activityToGoClass;
        private final int toastResourceId;

        public ActionForUiException(Class<? extends Activity> activityToGoClass, int toastResourceId)
        {
            this.activityToGoClass = activityToGoClass;
            this.toastResourceId = toastResourceId;
        }

        public Class<? extends Activity> getActivityToGoClass()
        {
            return activityToGoClass;
        }

        @Override
        public int getToastResourceId()
        {
            return toastResourceId;
        }
    }
}
