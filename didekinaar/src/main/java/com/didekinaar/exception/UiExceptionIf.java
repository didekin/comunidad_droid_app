package com.didekinaar.exception;

import android.app.Activity;
import android.content.Intent;

import com.didekin.common.exception.ErrorBean;

/**
 * User: pedro@didekin
 * Date: 16/11/16
 * Time: 14:07
 */

interface UiExceptionIf {
    void processMe(Activity activity, Intent intent) throws UiException;
    ErrorBean getErrorBean();
}
