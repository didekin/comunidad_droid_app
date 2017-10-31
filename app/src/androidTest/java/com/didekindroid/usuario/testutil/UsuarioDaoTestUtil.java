package com.didekindroid.usuario.testutil;

import com.didekindroid.exception.UiException;
import com.didekinlib.http.ErrorBean;

import java.util.concurrent.Callable;

import static com.didekinlib.http.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;

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
