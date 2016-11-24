package com.didekinaar.exception;

import android.app.Activity;
import android.content.Intent;

import com.didekin.common.exception.ErrorBean;

/**
 * User: pedro@didekin
 * Date: 16/11/16
 * Time: 14:07
 */

interface UiCmmExceptionIf {
    void processMe(Activity activity, Intent intent);
    ErrorBean getErrorBean();
}
