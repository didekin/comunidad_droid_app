package com.didekindroid.api;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.didekindroid.exception.UiException;
import com.didekinlib.http.ErrorBean;

import org.junit.Before;
import org.junit.Rule;
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
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 30/05/17
 * Time: 12:42
 */
@RunWith(AndroidJUnit4.class)
public class ObserverCacheCleanerTest {

    final AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);
    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    ActivityMock activity;
    Viewer<?, Controller> viewer;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        viewer = new Viewer<View, Controller>(null, activity, null) {
            @Override
            public void onErrorInObserver(Throwable error)
            {
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_WITH_EXCEPTION_EXEC), is(BEFORE_METHOD_EXEC));
                super.onErrorInObserver(error);
            }
        };
        viewer.setController(new Controller());
    }

    @Test
    public void test_OnComplete() throws Exception
    {
        assertThat(fromSingle(just("hola")).subscribeWith(new ObserverCacheCleaner(viewer)).isDisposed(), is(true));
    }

    @Test
    public void test_OnError() throws Exception
    {
        assertThat(viewer.getController().getIdentityCacher().getTokenCache().get(), nullValue());
        assertThat(viewer.getController().getIdentityCacher().getRefreshTokenFile().exists(), is(false));

        activity.runOnUiThread(() -> {
            assertThat(error(new UiException(new ErrorBean(BAD_REQUEST))).subscribeWith(new ObserverCacheCleaner(viewer)).isDisposed(), is(true));
            assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_WITH_EXCEPTION_EXEC));
        });
    }
}