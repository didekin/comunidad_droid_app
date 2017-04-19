package com.didekindroid.api;

import android.app.Activity;
import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.didekindroid.exception.UiException;
import com.didekinlib.http.ErrorBean;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Single;

import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_WITH_EXCEPTION_EXEC;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekinlib.http.GenericExceptionMsg.BAD_REQUEST;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 16/03/17
 * Time: 17:50
 */
@RunWith(AndroidJUnit4.class)
public class ObserverSelectionListTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    ObserverSelectionList<String> observerSelectionList;

    @Before
    public void setUp()
    {
        final Activity activity = activityRule.getActivity();
        final AtomicReference<ObserverSelectionList<String>> atomicObserver = new AtomicReference<>(null);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                atomicObserver.compareAndSet(null,
                        new ObserverSelectionList<>(
                                new CtrlerSpinnerForTest(
                                        new ViewerSelectionList<AdapterView, CtrlerSelectionList<String>, String>(new Spinner(activity), activity, null) {
                                            @Override
                                            public void onSuccessLoadItems(List<String> incidCloseList)
                                            {
                                            }

                                            @Override
                                            public void initSelectedItemId(Bundle savedState)
                                            {
                                            }
                                        }
                                )
                        )
                );
            }
        });
        waitAtMost(2, SECONDS).untilAtomic(atomicObserver, notNullValue());
        observerSelectionList = atomicObserver.get();
    }

    @Test
    public void testOnSuccess() throws Exception
    {
        List<String> stringList = Arrays.asList("uno", "dos", "tres");
        Single.just(stringList).subscribeWith(observerSelectionList);
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }

    @Test
    public void testOnError() throws Exception
    {
        Single.<List<String>>error(new UiException(new ErrorBean(BAD_REQUEST))).subscribeWith(observerSelectionList);
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_WITH_EXCEPTION_EXEC));
    }

    //    =========================== HELPERS =============================

    static class CtrlerSpinnerForTest extends CtrlerSelectionList<String> {


        protected CtrlerSpinnerForTest(ViewerSelectionList<AdapterView, CtrlerSelectionList<String>, String> viewer)
        {
            super(viewer);
        }

        @Override
        public void onSuccessLoadItemsInList(List<String> itemList)
        {
            assertThat(itemList.size(), is(3));
            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
        }

        @Override
        public void onErrorCtrl(Throwable e)
        {
            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_WITH_EXCEPTION_EXEC), is(BEFORE_METHOD_EXEC));
        }

        @Override
        public boolean loadItemsByEntitiyId(Long... entityId)
        {
            fail();
            return false;
        }
    }
}