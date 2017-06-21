package com.didekindroid.security;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.api.ActivityMock;
import com.didekindroid.api.ObserverCacheCleaner;
import com.didekindroid.exception.UiException;
import com.didekinlib.http.ErrorBean;
import com.didekinlib.http.oauth2.SpringOauthToken;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableCompletableObserver;

import static com.didekindroid.testutil.ActivityTestUtils.checkInitTokenCache;
import static com.didekindroid.testutil.ActivityTestUtils.checkNoInitCache;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.dao.UserComuDaoRemote.userComuDaoRemote;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekinlib.http.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;
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

    CtrlerAuthToken controller;
    Activity activity;

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        controller = new CtrlerAuthToken();
    }

    @After
    public void tearDown() throws Exception
    {
        controller.clearSubscriptions();
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
        DisposableCompletableObserver disposable = error(new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR)))
                .subscribeWith(new ObserverCacheCleaner(controller));
        checkNoInitCache();
        assertThat(disposable.isDisposed(), is(true));
    }

    /**
     * Synchronous execution: no scheduler specified, everything runs in the test runner thread.
     * onComplete method: we test that the token cache is NOT cleaned.
     */
    @Test
    public void testOauthUpdateTokenCacheObserver_2() throws UiException
    {
        checkInitTokenCache();

        DisposableCompletableObserver disposable = fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception
            {
                return null;
            }
        }).subscribeWith(new ObserverCacheCleaner(controller));

        checkInitTokenCache();
        assertThat(disposable.isDisposed(), is(true));
    }

    //  =======================================================================================
    // ............................ SUBSCRIPTIONS ..................................
    //  =======================================================================================

    /**
     * Synchronous execution: we use RxJavaPlugins to replace io scheduler; everything runs in the test runner thread.
     * We register a new user, checkMenu the cache is null, call the disposable and checkMenu cache is initialized.
     */
    @Test
    public void test_UpdateTkAndCacheFromUser() throws IOException, UiException
    {
        // Borramos el usario dado de alta por defecto.
        cleanOptions(CLEAN_PEPE);

        userComuDaoRemote.regComuAndUserAndUserComu(COMU_REAL_PEPE).execute().body();
        checkNoInitCache();
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            controller.updateTkAndCacheFromUser(USER_PEPE);
        } finally {
            resetAllSchedulers();
        }
        checkInitTokenCache();
    }

    @Test
    public void test_UpdateTkCacheFromRefreshTk() throws Exception
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.updateTkCacheFromRefreshTk(controller.getIdentityCacher().getRefreshTokenValue()), is(true));
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
            public boolean updateTkCacheFromRefreshTk(String refreshToken)
            {
                // No hay llamada al método del controller.
                fail();
                return false;
            }
        };
        controller.refreshAccessToken();
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
            public boolean updateTkCacheFromRefreshTk(String refreshToken)
            {
                // No hay llamada al método del controller.
                fail();
                return false;
            }
        };
        controller.refreshAccessToken();
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
            public boolean updateTkCacheFromRefreshTk(String refreshToken)
            {
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
                return false;
            }
        };

        controller.refreshAccessToken();
        // Hay llamada al método del controller.
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }
}