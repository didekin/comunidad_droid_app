package com.didekin.retrofitcl;

import com.didekin.exception.ErrorBean;
import retrofit.RetrofitError;

/**
 * User: pedro@didekin
 * Date: 01/08/15
 * Time: 15:37
 */
public class ServiceOneException extends RuntimeException {

    private final ErrorBean errorBean;
    private final RetrofitError retrofitError;

    public ServiceOneException(ErrorBean errorBean, RetrofitError retrofitError)
    {
        this.errorBean = errorBean;
        this.retrofitError = retrofitError;
    }

    public String getServiceMessage()
    {
        return errorBean.getMessage();
    }

    public int getHttpStatus()
    {
        return errorBean.getHttpStatus();
    }

    public RetrofitError getRetrofitError()
    {
        return retrofitError;
    }
}