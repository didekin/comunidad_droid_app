package com.didekindroid.usuario.firebase;

import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.exception.UiException;
import com.didekindroid.usuario.firebase.FirebaseTokenReactor.RegGcmTokenObserver;
import com.didekinlib.http.ErrorBean;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;

import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.firebase.FirebaseTokenReactor.tokenReactor;
import static com.didekindroid.usuario.firebase.FirebaseTokenReactor.updatedGcmTkSingle;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekinlib.http.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;
import static io.reactivex.plugins.RxJavaPlugins.reset;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 26/01/17
 * Time: 09:48
 */
@RunWith(AndroidJUnit4.class)
public class FirebaseTokenReactorTest {

    @Before
    public void getFixture() throws IOException, UiException
    {
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
    }

    @After
    public void cleanFileToken() throws UiException
    {
        cleanOptions(CLEAN_JUAN);
    }

    //  ====================================================================================
    //    .................................... OBSERVABLES .................................
    //  ====================================================================================

    /**
     * Synchronous execution: no scheduler specified, everything runs in the test runner thread.
     */
    @Test
    public void testUpdatedGcmTkSingle() throws Exception
    {
        updatedGcmTkSingle().test().assertResult(1).assertComplete().assertNoErrors();
    }

    //  =======================================================================================
    // ............................ SUBSCRIBERS ..................................
    //  =======================================================================================

    /**
     * Synchronous execution: no scheduler specified, everything runs in the test runner thread.
     * onError method: we test that the gcm token has not updated status.
     */
    @Test
    public void testOnError() throws Exception
    {
        Single.<Integer>error(new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR)))
                .subscribeWith(new RegGcmTokenObserver());
        assertThat(TKhandler.isGcmTokenSentServer(), is(false));
    }

    /**
     * Synchronous execution: no scheduler specified, everything runs in the test runner thread.
     * onSuccess method: we test that the gcm token has updated status.
     */
    @Test
    public void testOnSuccess() throws Exception
    {
        Single.just(1).subscribeWith(new RegGcmTokenObserver());
        assertThat(TKhandler.isGcmTokenSentServer(), is(true));
    }

    //  =======================================================================================
    // ............................ SUBSCRIPTIONS  ..................................
    //  =======================================================================================

    /**
     * Synchronous execution: we use RxJavaPlugins to replace io scheduler; everything runs in the test runner thread.
     * We call the subscription and check that the gcm token status has been updated.
     */
    @Test
    public void testCheckGcmToken_1()
    {
        // Preconditions: the user is registered and her gcmToken has not been sent to database.
        assertThat(TKhandler.isRegisteredUser() && !TKhandler.isGcmTokenSentServer(), is(true));

        CompositeDisposable subscriptions;
        try {
            trampolineReplaceIoScheduler();
            subscriptions = tokenReactor.checkGcmToken(null);
        } finally {
            reset();
        }
        assertThat(subscriptions.size(), is(1));
        assertThat(TKhandler.isGcmTokenSentServer(), is(true));
    }

    /**
     * Synchronous execution: we use RxJavaPlugins to replace io scheduler; everything runs in the test runner thread.
     * The gcmToken has been updated in database: we check that the subscriptions continue to be null.
     */
    @Test
    public void testCheckGcmToken_2()
    {
        // Preconditions: the user is registered and her gcmToken has been sent to database.
        TKhandler.updateIsGcmTokenSentServer(true);
        assertThat(TKhandler.isRegisteredUser(), is(true));

        CompositeDisposable subscriptions;
        try {
            trampolineReplaceIoScheduler();
            subscriptions = tokenReactor.checkGcmToken(null);
        } finally {
            reset();
        }
        assertThat(subscriptions, nullValue());
    }

    /**
     * Synchronous execution.
     * We call the subscription and check that the gcm token status has been updated.
     */
    @Test
    public void testCheckGcmTokenSync() throws Exception
    {
        tokenReactor.checkGcmTokenSync();
        assertThat(TKhandler.isGcmTokenSentServer(), is(true));
    }

}