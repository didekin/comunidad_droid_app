package com.didekindroid.api;

import android.view.View;

import com.didekindroid.exception.UiException;
import com.didekinlib.http.ErrorBean;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Single;

import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_WITH_EXCEPTION_EXEC;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekinlib.http.GenericExceptionMsg.BAD_REQUEST;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 22/03/17
 * Time: 18:21
 */
public class ObserverSingleListSelectedTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    ObserverSingleList<Integer> observerSingleList;

    @Before
    public void setUp()
    {
        observerSingleList = new ObserverSingleList<>(new CtrlerListForTest(new ViewerMock<View, CtrlerListIf>(null, null, null)));
    }

    @Test
    public void testOnSuccess() throws Exception
    {
        List<Integer> integerList = Arrays.asList(3, 5, 7, 9);
        Single.just(integerList).subscribeWith(observerSingleList);
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }

    @Test
    public void testOnError() throws Exception
    {
        Single.<List<Integer>>error(new UiException(new ErrorBean(BAD_REQUEST))).subscribeWith(observerSingleList);
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_WITH_EXCEPTION_EXEC));
    }

    static class CtrlerListForTest extends CtrlerIdentity<View> implements CtrlerListIf<Integer> {

        protected CtrlerListForTest(ViewerIf<View, CtrlerListIf> viewer)
        {
            super(viewer);
        }

        @Override
        public boolean loadItemsByEntitiyId(long entityId)
        {
            return false; // NOT tested.
        }

        @Override
        public void onSuccessLoadItemsById(List<Integer> itemList)
        {
            assertThat(itemList.size(), is(4));
            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
        }

        @Override
        public void onErrorCtrl(Throwable e)
        {
            assertThat(e instanceof UiException, is(true));
            UiException ui = (UiException) e;
            assertThat(ui.getErrorBean().getMessage(), is(BAD_REQUEST.getHttpMessage()));
            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_WITH_EXCEPTION_EXEC), is(BEFORE_METHOD_EXEC));
        }
    }
}