package com.didekindroid.api;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.AppCompatActivity;
import android.widget.Spinner;

import com.didekindroid.exception.UiException;
import com.didekinlib.http.ErrorBean;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Single;

import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_WITH_EXCEPTION_EXEC;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekinlib.http.GenericExceptionMsg.BAD_REQUEST;
import static io.reactivex.Single.just;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 30/05/17
 * Time: 12:55
 */
@RunWith(AndroidJUnit4.class)
public class ObserverSingleSelectItemTest {

    final AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);
    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);
    ObserverSingleSelectItem<ViewerSelectList<Spinner, CtrlerSelectList<String>, String>, String> observer;

    @Before
    public void setUp()
    {
        final AppCompatActivity activity = activityRule.getActivity();
        final AtomicReference<ObserverSingleSelectItem<ViewerSelectList<Spinner, CtrlerSelectList<String>, String>, String>> atomicObserver = new AtomicReference<>(null);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                atomicObserver.compareAndSet(null,
                        new ObserverSingleSelectItem<ViewerSelectList<Spinner, CtrlerSelectList<String>, String>, String>(
                                new ViewerSelectList<Spinner, CtrlerSelectList<String>, String>(new Spinner(activity), activity, null) {
                                    @Override
                                    public void initSelectedItemId(Bundle savedState)
                                    {
                                    }

                                    @Override
                                    public void onSuccessLoadSelectedItem(@NonNull Bundle bundle)
                                    {
                                        assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
                                    }

                                    @Override
                                    public void onErrorInObserver(Throwable error)
                                    {
                                        assertThat(flagMethodExec.getAndSet(AFTER_METHOD_WITH_EXCEPTION_EXEC), is(BEFORE_METHOD_EXEC));
                                    }
                                }
                        ));
            }
        });
        waitAtMost(2, SECONDS).untilAtomic(atomicObserver, notNullValue());
        observer = atomicObserver.get();
    }

    @Test
    public void test_OnSuccess() throws Exception
    {
        just(new Bundle(0)).subscribeWith(observer);
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }

    @Test
    public void test_OnError() throws Exception
    {
        Single.<Bundle>error(new UiException(new ErrorBean(BAD_REQUEST))).subscribeWith(observer);
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_WITH_EXCEPTION_EXEC));
    }
}