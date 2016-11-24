package com.didekinaar.exception;

/**
 * User: pedro@didekin
 * Date: 08/10/15
 * Time: 11:08
 */

import android.app.Activity;
import android.content.Intent;

import com.didekin.common.exception.ErrorBean;

/**
 * Exceptions to be dealt with in the user interface
 */
@SuppressWarnings("AbstractClassExtendsConcreteClass")
public abstract class UiCmmExceptionAbs extends Exception implements UiCmmExceptionIf  {

    protected final ErrorBean errorBean;

    protected UiCmmExceptionAbs(ErrorBean errorBean)
    {
        this.errorBean = errorBean;
    }

    @Override
    public abstract void processMe(Activity activity, Intent intent);

    @Override
    public ErrorBean getErrorBean()
    {
        return errorBean;
    }
}
