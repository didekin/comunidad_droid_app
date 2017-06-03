package com.didekindroid.api;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.exception.UiException;
import com.didekindroid.security.IdentityCacherMock;
import com.didekinlib.http.ErrorBean;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_WITH_EXCEPTION_EXEC;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekinlib.http.GenericExceptionMsg.BAD_REQUEST;
import static io.reactivex.Completable.error;
import static io.reactivex.Completable.fromSingle;
import static io.reactivex.Single.just;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 30/05/17
 * Time: 12:42
 */
@RunWith(AndroidJUnit4.class)
public class ObserverCacheCleanerTest {

    final AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);
    ControllerIf controller;

    @Test
    public void test_OnComplete() throws Exception
    {
        controller = new Controller();
        assertThat(fromSingle(just("hola")).subscribeWith(new ObserverCacheCleaner(controller)).isDisposed(), is(true));
    }

    @Test
    public void test_OnError() throws Exception
    {
        controller = new Controller(new IdentityCacherMock() {
            @Override
            public void cleanIdentityCache()
            {
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_WITH_EXCEPTION_EXEC), is(BEFORE_METHOD_EXEC));
            }
        });
        assertThat(error(new UiException(new ErrorBean(BAD_REQUEST))).subscribeWith(new ObserverCacheCleaner(controller)).isDisposed(), is(true));
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_WITH_EXCEPTION_EXEC));
    }
}