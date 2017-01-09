package com.didekinaar.exception;

import android.app.Activity;
import android.content.Intent;

import com.didekin.common.exception.ErrorBean;
import com.didekinaar.R;

import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.didekinaar.AppInitializer.creator;
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
        Timber.d("processMe(): %s%n", errorBean.getMessage());

        ActionForUiExceptionIf action = creator.get().getExceptionDealer().getActionForException(this);

        if (action == null) { // NO entry in exceptions dealer's table for error bean message.
            makeToast(activity, R.string.exception_generic_message);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setAction(Intent.ACTION_MAIN);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TOP);
            activity.startActivity(intent);
            finishActivity(activity, intent);
        } else {
            if (action.getToastResourceId() > 0) {
                makeToast(activity, action.getToastResourceId());
            }
            if (action.getActivityToGoClass() != null) {
                intent.setClass(activity, action.getActivityToGoClass());
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
                finishActivity(activity, intent);
            }
        }
    }

    @Override
    public ErrorBean getErrorBean()
    {
        return errorBean;
    }

    public static class ActionForUiException implements ActionForUiExceptionIf {

        private final Class<? extends Activity> activityToGoClass;
        private final int toastResourceId;

        public ActionForUiException(Class<? extends Activity> activityToGoClass, int toastResourceId)
        {
            this.activityToGoClass = activityToGoClass;
            this.toastResourceId = toastResourceId;
        }

        @Override
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
