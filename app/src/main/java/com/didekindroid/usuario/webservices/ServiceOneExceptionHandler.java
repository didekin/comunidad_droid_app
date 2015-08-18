package com.didekindroid.usuario.webservices;

import android.util.Log;
import com.didekindroid.common.error.ErrorBean;
import retrofit.ErrorHandler;
import retrofit.RetrofitError;

/**
 * User: pedro@didekin
 * Date: 01/08/15
 * Time: 15:35
 */
public class ServiceOneExceptionHandler implements ErrorHandler {

    private static final String TAG = ServiceOneExceptionHandler.class.getCanonicalName();

    @Override
    public Throwable handleError(RetrofitError retrofitError)
    {
        Log.d(TAG, "handleError()");

        ErrorBean errorBean = null;
        try {
            errorBean = (ErrorBean) retrofitError.getBodyAs(ErrorBean.class);
        } catch (RuntimeException e) { /* To catch conversion exception.*/
        } finally {
            if (errorBean == null || errorBean.getMessage() == null) {
                errorBean = new ErrorBean(
                        retrofitError.getCause() != null ?
                                retrofitError.getCause().getMessage() :
                                retrofitError.getResponse().getReason(),
                        retrofitError.getResponse().getStatus());
            }
        }
        return new ServiceOneException(errorBean, retrofitError);
    }
}