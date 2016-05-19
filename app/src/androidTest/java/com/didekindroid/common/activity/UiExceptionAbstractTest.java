package com.didekindroid.common.activity;

import android.support.annotation.NonNull;

import com.didekin.common.exception.DidekinExceptionMsg;
import com.didekin.common.exception.ErrorBean;

/**
 * User: pedro@didekin
 * Date: 19/02/16
 * Time: 10:25
 */
public class UiExceptionAbstractTest {

    @NonNull
    UiException getUiException(DidekinExceptionMsg exceptionMsg)
    {
        ErrorBean errorBean = new ErrorBean(exceptionMsg.getHttpMessage(), exceptionMsg.getHttpStatus());
        return new UiException(errorBean);
    }
}
