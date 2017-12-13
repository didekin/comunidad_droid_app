package com.didekindroid.exception;

import android.content.Intent;

import com.didekindroid.exception.UiExceptionIf.IntentForUiExceptionIf;

/**
 * User: pedro@didekin
 * Date: 13/01/17
 * Time: 18:33
 */
public class IntentForUiException implements IntentForUiExceptionIf {

    private final Intent intentForException;
    private final int toastResourceId;

    IntentForUiException(int toastResourceId)
    {
        this(null, toastResourceId);
    }

    public IntentForUiException(Intent intentForException, int toastResourceId)
    {
        this.intentForException = intentForException;
        this.toastResourceId = toastResourceId;
    }

    @Override
    public Intent getIntentForException()
    {
        return intentForException;
    }

    @Override
    public int getToastResourceId()
    {
        return toastResourceId;
    }
}
