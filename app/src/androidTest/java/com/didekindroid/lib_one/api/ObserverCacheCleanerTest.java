package com.didekindroid.lib_one.api;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.api.exception.UiExceptionRouterIf;
import com.didekindroid.lib_one.api.router.RouterActionIf;
import com.didekindroid.lib_one.security.SecurityTestUtils;
import com.didekinlib.http.exception.ErrorBean;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import static com.didekindroid.lib_one.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.lib_one.testutil.ConstantExecution.AFTER_METHOD_WITH_EXCEPTION_EXEC;
import static com.didekindroid.lib_one.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.BAD_REQUEST;
import static io.reactivex.Completable.error;
import static io.reactivex.Completable.fromSingle;
import static io.reactivex.Single.just;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
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
            public UiExceptionRouterIf getExceptionRouter()
            {
                return httpMsg -> (RouterActionIf) () -> ActivityNextMock.class;
            }

            @Override
            public void onErrorInObserver(Throwable error)
            {
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_WITH_EXCEPTION_EXEC), is(BEFORE_METHOD_EXEC));
                super.onErrorInObserver(error);
            }
        };
        viewer.setController(new Controller(TKhandler));
    }

    @Test
    public void test_OnComplete() throws Exception
    {
        assertThat(fromSingle(just("hola")).subscribeWith(new ObserverCacheCleaner(viewer)).isDisposed(), is(true));
    }

    @Test
    public void test_OnError() throws Exception
    {
        // Preconditions.
        TKhandler.initIdentityCache(SecurityTestUtils.doSpringOauthToken());
        assertThat(viewer.getController().getIdentityCacher().getTokenCache().get(), notNullValue());

        activity.runOnUiThread(() -> {
            assertThat(error(new UiException(new ErrorBean(BAD_REQUEST))).subscribeWith(new ObserverCacheCleaner(viewer)).isDisposed(), is(true));
            assertThat(viewer.getController().getIdentityCacher().getTokenCache().get(), nullValue());
            assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_WITH_EXCEPTION_EXEC));
        });
    }
}