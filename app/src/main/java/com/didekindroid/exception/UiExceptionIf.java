package com.didekindroid.exception;

import android.app.Activity;
import android.content.Intent;

import com.didekin.http.ErrorBean;

/**
 * User: pedro@didekin
 * Date: 16/11/16
 * Time: 14:07
 */

public interface UiExceptionIf {

    void processMe(Activity activity, Intent intent) throws UiException;
    ErrorBean getErrorBean();

    interface UiExceptionDealerIf {
        ActionForUiExceptionIf getActionForException(UiException uiException);
    }

    interface ActionForUiExceptionIf {
        Class<? extends Activity> getActivityToGoClass();
        int getToastResourceId();
    }
}
