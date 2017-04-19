package com.didekindroid.api;

import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.didekindroid.exception.UiException;
import com.didekinlib.http.ErrorBean;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Maybe;

import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_WITH_EXCEPTION_EXEC;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekinlib.http.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 08/03/17
 * Time: 12:18
 */
@RunWith(AndroidJUnit4.class)
public class ObserverMaybeListTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Test
    public void testOnSuccess() throws Exception
    {
        List<Integer> list = new ArrayList<>(1);
        list.add(11);
        list.add(12);
        Maybe.just(list).subscribeWith(
                new ObserverMaybeList<>(
                        new CtrlerIncidSeeForTest(
                                new ViewerMock<View, CtrlerSelectionListIf>(null, null, null)
                        )
                )
        );
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }

    @Test
    public void testOnError() throws Exception
    {
        Maybe.<List<Integer>>error(new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR)))
                .subscribeWith(new ObserverMaybeList<>(
                        new CtrlerIncidSeeForTest(
                                new ViewerMock<View, CtrlerSelectionListIf>(null, null, null)
                        )
                ));
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_WITH_EXCEPTION_EXEC));
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    class CtrlerIncidSeeForTest extends Controller implements CtrlerSelectionListIf<Integer> {

        public CtrlerIncidSeeForTest(ViewerIf<View, CtrlerSelectionListIf> viewer)
        {
            super(viewer);
        }

        @Override
        public boolean loadItemsByEntitiyId(Long... entityId)
        {
            return false; // NOT necessary for test.
        }

        @Override
        public void onSuccessLoadItemsInList(List<Integer> itemList)
        {
            assertThat(itemList.size(), is(2));
            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
        }

        @Override
        public void onErrorCtrl(Throwable e)
        {
            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_WITH_EXCEPTION_EXEC), is(BEFORE_METHOD_EXEC));
        }
    }
}