package com.didekindroid.exception;

import android.app.Activity;
import android.content.Intent;

import com.didekinlib.http.ErrorBean;

/**
 * User: pedro@didekin
 * Date: 16/11/16
 * Time: 14:07
 */

public interface UiExceptionIf {

    void processMe(Activity activity);

    void processMe(Activity activity, Intent intent);

    void processMe(Activity activity, IntentForUiExceptionIf actionForUiException);

    ErrorBean getErrorBean();

    interface UiExceptionRouterIf {
        IntentForUiExceptionIf getActionForException(UiException uiException);
    }

    interface IntentForUiExceptionIf {

        Intent getIntentForException();

        int getToastResourceId();
    }
}
