package com.didekindroid.lib_one.security;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.didekindroid.api.ActivityMock;
import com.didekindroid.lib_one.api.ObserverCacheCleaner;
import com.didekindroid.lib_one.api.Viewer;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.http.auth.SpringOauthToken;
import com.didekinlib.http.exception.ErrorBean;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableCompletableObserver;

import static com.didekindroid.testutil.ActivityTestUtils.checkInitTokenCache;
import static com.didekindroid.testutil.ActivityTestUtils.checkNoInitCache;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_WITH_EXCEPTION_EXEC;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekinlib.http.exception.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;
import static io.reactivex.Completable.error;
import static io.reactivex.Completable.fromCallable;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 15/05/17
 * Time: 16:37
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerAuthTokenTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<ActivityMock>(ActivityMock.class) {
        @Override
        protected void beforeActivityLaunched()
        {
            try {
                signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
            } catch (IOException | UiException e) {
                fail();
            }
        }
    };

    ActivityMock activity;
    Viewer<?, CtrlerAuthToken> viewer;
    CtrlerAuthToken controller;

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        viewer = new Viewer<View, CtrlerAuthToken>(null, activity, null) {
            @Override
            public void onErrorInObserver(Throwable error)
            {
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_WITH_EXCEPTION_EXEC), is(BEFORE_METHOD_EXEC));
                super.onErrorInObserver(error);
            }
        };
        viewer.setController(new CtrlerAuthToken());
        controller = viewer.getController();
    }

    @After
    public void tearDown() throws Exception
    {
        viewer.getController().clearSubscriptions();
        resetAllSchedulers();
        cleanOptions(CLEAN_PEPE);
    }

    //  =======================================================================================
    // ............................ SUBSCRIBERS ..................................
    //  =======================================================================================

    /**
     * Synchronous execution: no scheduler specified, everything runs in the test runner thread.
     * onErrorObserver method: we test that the token cache is cleaned.
     */
    @Test
    public void testOauthUpdateTokenCacheObserver_1() throws UiException
    {
        checkInitTokenCache();
        activity.runOnUiThread(() -> {
            DisposableCompletableObserver disposable = error(new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR)))
                    .subscribeWith(new ObserverCacheCleaner(viewer));
            checkNoInitCache();
            assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_WITH_EXCEPTION_EXEC));
            assertThat(disposable.isDisposed(), is(true));
        });
    }

    /**
     * Synchronous execution: no scheduler specified, everything runs in the test runner thread.
     * onComplete method: we test that the token cache is NOT cleaned.
     */
    @Test
    public void testOauthUpdateTokenCacheObserver_2() throws UiException
    {
        checkInitTokenCache();

        DisposableCompletableObserver disposable = fromCallable(() -> null).subscribeWith(new ObserverCacheCleaner(viewer));

        checkInitTokenCache();
        assertThat(disposable.isDisposed(), is(true));
    }

    //  =======================================================================================
    // ............................ SUBSCRIPTIONS ..................................
    //  =======================================================================================

    @Test
    public void test_UpdateTkCacheFromRefreshTk() throws Exception
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.updateTkCacheFromRefreshTk(controller.getIdentityCacher().getRefreshTokenValue(), viewer), is(true));
        } finally {
            resetAllSchedulers();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
    }

    @Test
    public void testRefreshAccessToken_1() throws IOException, UiException
    {
        // Precondition: a fully initialized cache.
        assertThat(controller.getIdentityCacher().getTokenCache().get().getValue().isEmpty(), is(false));
        // Initial state.
        assertThat(controller.getIdentityCacher().isRegisteredUser(), is(true));
        assertThat(controller.getIdentityCacher().getTokenCache().get(), notNullValue());
        assertThat(controller.getIdentityCacher().getTokenCache().get().getValue(), notNullValue());
        assertThat(controller.getIdentityCacher().getRefreshTokenFile().exists(), is(true));

        controller = new CtrlerAuthToken() {
            @Override
            public boolean updateTkCacheFromRefreshTk(String refreshToken, Viewer viewer)
            {
                // No hay llamada al método del controller.
                fail();
                return false;
            }
        };
        controller.refreshAccessToken(viewer);
    }

    @Test
    public void testRefreshAccessToken_2() throws IOException, UiException
    {
        // Precondition: a user in DB, cache is null.
        // Borramos cache.
        controller.getIdentityCacher().cleanIdentityCache();
        // Initial state.
        assertThat(controller.getIdentityCacher().isRegisteredUser(), is(true));
        assertThat(controller.getIdentityCacher().getTokenCache().get(), nullValue());

        controller = new CtrlerAuthToken() {
            @Override
            public boolean updateTkCacheFromRefreshTk(String refreshToken, Viewer viewer)
            {
                // No hay llamada al método del controller.
                fail();
                return false;
            }
        };
        controller.refreshAccessToken(viewer);
    }

    @Test
    public void testRefreshAccessToken_3() throws IOException, UiException
    {
        // Precondition: a user in DB, refreshToken in cache, accessToken is null.
        String refreshTkOriginal = controller.getIdentityCacher().getRefreshTokenValue();
        // Clean cache and initialize with a refreshToken.
        controller.getIdentityCacher().cleanIdentityCache();
        controller.getIdentityCacher().getTokenCache().compareAndSet(null, new SpringOauthToken(refreshTkOriginal));
        // Initial state.
        assertThat(controller.getIdentityCacher().isRegisteredUser(), is(true));
        assertThat(controller.getIdentityCacher().getTokenCache().get().getRefreshToken(), notNullValue());
        assertThat(controller.getIdentityCacher().getTokenCache().get().getValue(), nullValue());

        controller = new CtrlerAuthToken() {
            @Override
            public boolean updateTkCacheFromRefreshTk(String refreshToken, Viewer viewer)
            {
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
                return false;
            }
        };

        controller.refreshAccessToken(viewer);
        // Hay llamada al método del controller.
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }
}