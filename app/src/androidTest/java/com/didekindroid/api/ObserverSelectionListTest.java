package com.didekindroid.api;

import android.app.Activity;
import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Spinner;

import com.didekindroid.api.testutil.CtrlerSpinnerForTest;
import com.didekindroid.exception.UiException;
import com.didekinlib.http.ErrorBean;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Observable;

import static com.didekindroid.api.testutil.CtrlerSpinnerForTest.flagMethodExec;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_WITH_EXCEPTION_EXEC;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekinlib.http.GenericExceptionMsg.BAD_REQUEST;
import static io.reactivex.Observable.*;
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
public class ObserverSelectionListTest {

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
                                        new ViewerSelectionList<Spinner, CtrlerSelectionList<String>, String>(new Spinner(activity), activity, null) {
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
    public void test_OnNext() throws Exception
    {
        List<String> stringList = Arrays.asList("uno", "dos", "tres");
        just(stringList).subscribeWith(observerSelectionList);
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }

    @Test
    public void test_OnError() throws Exception
    {
        Observable.<List<String>>error(new UiException(new ErrorBean(BAD_REQUEST))).subscribeWith(observerSelectionList);
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_WITH_EXCEPTION_EXEC));
    }
}