package com.didekindroid.usuario.firebase;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.ManagerIf;
import com.didekindroid.ManagerMock;
import com.didekindroid.MockActivity;
import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf;
import com.didekindroid.incidencia.core.ControllerFirebaseTokenIf;
import com.didekindroid.usuario.firebase.ViewerFirebaseTokenIf.ViewerFirebaseToken;
import com.didekinlib.http.ErrorBean;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Single;

import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.firebase.FirebaseTokenReactor.tokenReactor;
import static com.didekindroid.usuario.firebase.FirebaseTokenReactor.updatedGcmTkSingle;
import static com.didekindroid.usuario.firebase.ViewerFirebaseTokenIf.ViewerFirebaseToken.newViewerFirebaseToken;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekinlib.http.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;
import static io.reactivex.plugins.RxJavaPlugins.reset;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 26/01/17
 * Time: 09:48
 */
@RunWith(AndroidJUnit4.class)
public class FirebaseTokenReactorTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    ControllerFirebaseTokenIf controller;
    ManagerIf manager;

    @Rule
    public ActivityTestRule<? extends Activity> activityRule = new ActivityTestRule<>(MockActivity.class, true, true);

    @Before
    public void getFixture() throws IOException, UiException
    {
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        manager = new ManagerMock(activityRule.getActivity());
    }

    @After
    public void cleanFileToken() throws UiException
    {
        cleanOptions(CLEAN_JUAN);
    }

    @AfterClass
    public static void resetScheduler()
    {
        reset();
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
        ViewerFirebaseTokenIf viewer = new ViewerFirebaseToken(manager){
            @Override
            public UiExceptionIf.ActionForUiExceptionIf processControllerError(UiException ui)
            {
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC), is(BEFORE_METHOD_EXEC));
                return null;
            }
        };
        controller = new ControllerFirebaseToken(viewer);
        viewer.setController(controller);

        // Preconditions.
        assertThat(controller.isGcmTokenSentServer(), is(false));
        // Call.
        Single.<Integer>error(new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR)))
                .subscribeWith(new FirebaseTokenReactor.RegGcmTokenObserver(controller));
        // Check.
        assertThat(controller.isGcmTokenSentServer(), is(false));
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC));
    }

    /**
     * Synchronous execution: no scheduler specified, everything runs in the test runner thread.
     * onSuccess method: we test that the gcm token has updated status.
     */
    @Test
    public void testOnSuccess() throws Exception
    {
        controller = new ControllerFirebaseToken(newViewerFirebaseToken(manager));
        assertThat(controller.isGcmTokenSentServer(), is(false));

        Single.just(1).subscribeWith(new FirebaseTokenReactor.RegGcmTokenObserver(controller));
        assertThat(controller.isGcmTokenSentServer(), is(true));
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
        controller = new ControllerFirebaseToken(newViewerFirebaseToken(manager));
        // Preconditions: the user is registered and her gcmToken has not been sent to database.
        assertThat(controller.isRegisteredUser() && controller.isGcmTokenSentServer(), is(false));

        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(tokenReactor.checkGcmToken(controller),is(true));
        } finally {
            reset();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
        assertThat(controller.isGcmTokenSentServer(), is(true));           // TODO
    }

    /**
     * Synchronous execution.
     * We call the subscription and check that the gcm token status has been updated.
     */
    @Test
    public void testCheckGcmTokenSync() throws Exception
    {
        controller = new ControllerFirebaseToken(newViewerFirebaseToken(manager));
        tokenReactor.checkGcmTokenSync(controller);
        assertThat(controller.getSubscriptions().size(), is(1));
    }
}