package com.didekindroid.api;

import android.app.Activity;
import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Spinner;

import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf;
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

/**
 * User: pedro@didekin
 * Date: 11/05/17
 * Time: 12:39
 */
@RunWith(AndroidJUnit4.class)
public class ObserverSingleSelectListTest {

    final AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    ObserverSingleSelectList<ViewerSelectList<Spinner, CtrlerSelectList<String>, String>, String> observerSingleSelectList;

    @Before
    public void setUp()
    {
        final Activity activity = activityRule.getActivity();
        final AtomicReference<ObserverSingleSelectList<ViewerSelectList<Spinner, CtrlerSelectList<String>, String>, String>> atomicObserver = new AtomicReference<>(null);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                atomicObserver.compareAndSet(null,
                        new ObserverSingleSelectList<ViewerSelectList<Spinner, CtrlerSelectList<String>, String>, String>(
                                new ViewerSelectList<Spinner, CtrlerSelectList<String>, String>(new Spinner(activity), activity, null) {
                                    @Override
                                    public void initSelectedItemId(Bundle savedState)
                                    {
                                    }

                                    @Override
                                    public void onSuccessLoadItemList(List<String> itemsList)
                                    {
                                        assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
                                    }

                                    @Override
                                    public UiExceptionIf.ActionForUiExceptionIf onErrorInObserver(Throwable error)
                                    {
                                        assertThat(flagMethodExec.getAndSet(AFTER_METHOD_WITH_EXCEPTION_EXEC), is(BEFORE_METHOD_EXEC));
                                        return null;
                                    }
                                }
                        ));
            }
        });
        waitAtMost(2, SECONDS).untilAtomic(atomicObserver, notNullValue());
        observerSingleSelectList = atomicObserver.get();
    }

    @Test
    public void test_OnSuccess() throws Exception
    {
        List<String> stringList = Arrays.asList("uno", "dos", "tres");
        Single.just(stringList).subscribeWith(observerSingleSelectList);
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }

    @Test
    public void test_OnError() throws Exception
    {
        Single.<List<String>>error(new UiException(new ErrorBean(BAD_REQUEST))).subscribeWith(observerSingleSelectList);
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_WITH_EXCEPTION_EXEC));
    }
}