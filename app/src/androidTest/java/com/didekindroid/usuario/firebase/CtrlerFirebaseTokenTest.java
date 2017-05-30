package com.didekindroid.usuario.firebase;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.api.ActivityMock;
import com.didekindroid.exception.UiException;
import com.didekindroid.usuario.firebase.CtrlerFirebaseToken.RegGcmTokenObserver;
import com.didekinlib.http.ErrorBean;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Single;

import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.testutil.RxSchedulersUtils.resetAllSchedulers;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static com.didekindroid.usuario.firebase.CtrlerFirebaseToken.updatedGcmTkSingle;
import static com.didekindroid.usuario.firebase.ViewerFirebaseToken.newViewerFirebaseToken;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_JUAN;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekinlib.http.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 06/03/17
 * Time: 17:26
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerFirebaseTokenTest {

    static final AtomicReference<String> flagControl = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public ActivityTestRule<? extends Activity> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    CtrlerFirebaseToken controller;
    ActivityMock activity;

    @Before
    public void setUp() throws IOException, UiException
    {
        signUpAndUpdateTk(COMU_PLAZUELA5_JUAN);
        activity = (ActivityMock) activityRule.getActivity();
        controller = new CtrlerFirebaseToken(newViewerFirebaseToken(activity));
    }

    @After
    public void cleanUp() throws UiException
    {
        cleanOptions(CLEAN_JUAN);
    }

    //    ................................ OBSERVABLES/SUBSCRIBERS .................................

    /**
     * Synchronous execution: no scheduler specified, everything runs in the test runner thread.
     */
    @Test
    public void testUpdatedGcmTkSingle() throws Exception
    {
        updatedGcmTkSingle().test().assertResult(1);
    }

    @Test
    public void testObserverOnError()
    {
        // Preconditions.
        controller.updateIsGcmTokenSentServer(true);
        assertThat(controller.isGcmTokenSentServer(), is(true));
        // Call.
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                Single.<Integer>error(new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR)))
                        .subscribeWith(new RegGcmTokenObserver(controller));
                // Check call-back to onErrorObserver change the status to false.
                assertThat(controller.isGcmTokenSentServer(), is(false));
            }
        });
    }

    @Test
    public void testObserverOnSuccess()
    {
        // Preconditions.
        assertThat(controller.isGcmTokenSentServer(), is(false));

        Single.just(1).subscribeWith(new RegGcmTokenObserver(controller));
        // Check call-back to onSuccess change the status to true.
        assertThat(controller.isGcmTokenSentServer(), is(true));
    }

    //    ................................. INSTANCE METHODS ...............................

    @Test
    public void isGcmTokenSentServer() throws Exception
    {
        controller.getIdentityCacher().updateIsRegistered(true);

        controller.updateIsGcmTokenSentServer(true);
        assertThat(controller.isGcmTokenSentServer(), is(true));
        controller.updateIsGcmTokenSentServer(false);
        assertThat(controller.isGcmTokenSentServer(), is(false));
    }

    @Test
    public void updateIsGcmTokenSentServer() throws Exception
    {
        controller.getIdentityCacher().updateIsRegistered(true);
        assertThat(controller.isGcmTokenSentServer(), is(false));

        controller.updateIsGcmTokenSentServer(true);
        assertThat(controller.isGcmTokenSentServer(), is(true));
        controller.updateIsGcmTokenSentServer(false);
        assertThat(controller.isGcmTokenSentServer(), is(false));
    }

    @Test
    public void checkGcmToken() throws Exception
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();

            // Preconditions.
            controller.getIdentityCacher().updateIsRegistered(true);
            controller.updateIsGcmTokenSentServer(true);
            /* Execute. FALSE: no update because is already updated.*/
            assertThat(controller.checkGcmToken(), is(false));
            assertThat(controller.getSubscriptions().size(), is(0));
            // Mantains status.
            assertThat(controller.isGcmTokenSentServer(), is(true));

            // Preconditions.
            controller.getIdentityCacher().updateIsRegistered(true);
            controller.updateIsGcmTokenSentServer(false);
            /* Execute.*/
            assertThat(controller.checkGcmToken(), is(true));
            assertThat(controller.getSubscriptions().size(), is(1));
            // Change status.
            assertThat(controller.isGcmTokenSentServer(), is(true));

        } finally {
            resetAllSchedulers();
        }
    }

    @Test
    public void checkGcmTokenSync() throws Exception
    {
        // Preconditions.
        controller.getIdentityCacher().updateIsRegistered(true);
        controller.updateIsGcmTokenSentServer(true);
        /* Execute.*/
        assertThat(controller.checkGcmTokenSync(), is(true));
        // The token is updated: controller open subscription.
        assertThat(controller.getSubscriptions().size(), is(1));

        // Preconditions.
        controller.getIdentityCacher().updateIsRegistered(false);
        /* Execute.*/
        assertThat(controller.checkGcmTokenSync(), is(false));
        // NO increase in subscriptions.
        assertThat(controller.getSubscriptions().size(), is(1));
        assertThat(controller.isGcmTokenSentServer(), is(false));

        // Preconditions.
        controller.getIdentityCacher().updateIsRegistered(true);
        controller.updateIsGcmTokenSentServer(false);
        /* Execute.*/
        assertThat(controller.checkGcmTokenSync(), is(true));
        assertThat(controller.getSubscriptions().size(), is(2));
        assertThat(controller.isGcmTokenSentServer(), is(true));
    }
}