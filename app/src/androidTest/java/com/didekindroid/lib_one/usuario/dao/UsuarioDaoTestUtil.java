package com.didekindroid.lib_one.usuario.dao;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.http.exception.ErrorBean;

import java.util.concurrent.Callable;

import static com.didekinlib.http.exception.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;

/**
 * User: pedro@didekin
 * Date: 31/10/2017
 * Time: 12:28
 */

public class UsuarioDaoTestUtil {

    public static class SendPswdCallable implements Callable<Boolean> {
        @Override
        public Boolean call() throws Exception
        {
            return true;
        }
    }

    public static class SendPswdCallableError implements Callable<Boolean> {
        @Override
        public Boolean call() throws Exception
        {
            throw new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR));
        }
    }
}
