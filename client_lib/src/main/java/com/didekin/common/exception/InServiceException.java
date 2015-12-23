package com.didekin.common.exception;

import retrofit.RetrofitError;

/**
 * User: pedro@didekin
 * Date: 01/08/15
 * Time: 15:37
 */
public class InServiceException extends RuntimeException {

    private final ErrorBean errorBean;
    private final RetrofitError retrofitError;

    public InServiceException(ErrorBean errorBean, RetrofitError retrofitError)
    {
        this.errorBean = errorBean;
        this.retrofitError = retrofitError;
    }

    public String getHttpMessage()
    {
        return errorBean.getMessage();
    }

    public int getHttpStatus()
    {
        return errorBean.getHttpStatus();
    }

    @SuppressWarnings("unused")
    public RetrofitError getRetrofitError()
    {
        return retrofitError;
    }
}