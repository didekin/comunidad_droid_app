package com.didekindroid.lib_one.api.exception;

import com.didekinlib.http.exception.ErrorBean;

/**
 * User: pedro@didekin
 * Date: 16/11/16
 * Time: 14:07
 */

public interface UiExceptionIf {

    ErrorBean getErrorBean();

    String getErrorHtppMsg();

}
