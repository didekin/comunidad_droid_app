package com.didekindroid.lib_one.api;

import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.AppCompatActivity;
import android.widget.Spinner;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.api.router.UiExceptionRouterIf;
import com.didekindroid.lib_one.api.router.RouterActionIf;
import com.didekinlib.http.exception.ErrorBean;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Single;

import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.AFTER_METHOD_EXEC_A;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.AFTER_METHOD_WITH_EXCEPTION_EXEC;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.BEFORE_METHOD_EXEC;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.BAD_REQUEST;
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
        final AppCompatActivity activity = activityRule.getActivity();
        final AtomicReference<ObserverSingleSelectList<ViewerSelectList<Spinner, CtrlerSelectList<String>, String>, String>> atomicObserver = new AtomicReference<>(null);
        activity.runOnUiThread(
                () -> atomicObserver.compareAndSet(
                        null,
                        new ObserverSingleSelectList<>(
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
                                    public UiExceptionRouterIf getExceptionRouter()
                                    {
                                        return httpMsg -> (RouterActionIf) () -> ActivityNextMock.class;
                                    }

                                    @Override
                                    public void onErrorInObserver(Throwable error)
                                    {
                                        assertThat(flagMethodExec.getAndSet(AFTER_METHOD_WITH_EXCEPTION_EXEC), is(BEFORE_METHOD_EXEC));
                                    }
                                }
                        )
                )
        );
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