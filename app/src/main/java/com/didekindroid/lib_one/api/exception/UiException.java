package com.didekindroid.lib_one.api.exception;

import com.didekinlib.http.exception.ErrorBean;

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
    public ErrorBean getErrorBean()
    {
        return errorBean;
    }

    @Override
    public String getErrorHtppMsg()
    {
        return errorBean.getMessage();
    }

}
