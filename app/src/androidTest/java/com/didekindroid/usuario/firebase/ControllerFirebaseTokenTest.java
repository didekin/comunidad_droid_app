package com.didekindroid.usuario.firebase;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.ManagerIf;
import com.didekindroid.ManagerMock;
import com.didekindroid.MockActivity;
import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.core.ControllerFirebaseTokenIf;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.usuario.firebase.ViewerFirebaseTokenIf.ViewerFirebaseToken.newViewerFirebaseToken;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_TK_HANDLER;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 06/03/17
 * Time: 17:26
 */
@RunWith(AndroidJUnit4.class)
public class ControllerFirebaseTokenTest {

    static final AtomicReference<String> flagControl = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public ActivityTestRule<? extends Activity> activityRule = new ActivityTestRule<>(MockActivity.class, true, true);

    ManagerIf manager;
    ControllerFirebaseToken controller;

    @Before
    public void setUp()
    {
        manager = new ManagerMock(activityRule.getActivity());
    }

    @After
    public void cleanUp() throws UiException
    {
        cleanOptions(CLEAN_TK_HANDLER);
    }

    @Test
    public void checkGcmToken_1() throws Exception
    {
        ControllerFirebaseTokenIf.FirebaseTokenReactorIf reactor = new FirebaseTokenReactor() {
            @Override
            public boolean checkGcmToken(ControllerFirebaseTokenIf controller)
            {
                assertThat(flagControl.getAndSet(AFTER_METHOD_EXEC), is(BEFORE_METHOD_EXEC));
                return flagControl.get().equals(AFTER_METHOD_EXEC);
            }
        };
        controller = new ControllerFirebaseToken(newViewerFirebaseToken(manager), reactor, TKhandler);

        // Preconditions.
        controller.getIdentityCacher().updateIsRegistered(true);
        controller.updateIsGcmTokenSentServer(false);
        /* Execute.*/
        controller.checkGcmToken();
        // Check.
        assertThat(flagControl.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC));

        // Preconditions.
        controller.getIdentityCacher().updateIsRegistered(true);
        controller.updateIsGcmTokenSentServer(true);
        /* Execute.*/
        controller.checkGcmToken();
        // Check.
        assertThat(flagControl.getAndSet(BEFORE_METHOD_EXEC), is(BEFORE_METHOD_EXEC));
    }

    @Test
    public void checkGcmTokenSync() throws Exception
    {
        ControllerFirebaseTokenIf.FirebaseTokenReactorIf reactor = new FirebaseTokenReactor(){
            @Override
            public void checkGcmTokenSync(ControllerFirebaseTokenIf controller)
            {
                assertThat(flagControl.getAndSet(AFTER_METHOD_EXEC), is(BEFORE_METHOD_EXEC));
            }
        };
        controller = new ControllerFirebaseToken(newViewerFirebaseToken(manager), reactor, TKhandler);

        // Preconditions.
        controller.getIdentityCacher().updateIsRegistered(true);
        /* Execute.*/
        controller.checkGcmTokenSync();
        // Check.
        assertThat(flagControl.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC));

        // Preconditions.
        controller.getIdentityCacher().updateIsRegistered(false);
        /* Execute.*/
        controller.checkGcmTokenSync();
        // Check.
        assertThat(flagControl.getAndSet(BEFORE_METHOD_EXEC), is(BEFORE_METHOD_EXEC));
    }

    @Test
    public void isGcmTokenSentServer() throws Exception
    {
        controller = new ControllerFirebaseToken(newViewerFirebaseToken(manager));
        controller.getIdentityCacher().updateIsRegistered(true);

        controller.updateIsGcmTokenSentServer(true);
        assertThat(controller.isGcmTokenSentServer(), is(true));
        controller.updateIsGcmTokenSentServer(false);
        assertThat(controller.isGcmTokenSentServer(), is(false));
    }

    @Test
    public void updateIsGcmTokenSentServer() throws Exception
    {
        controller = new ControllerFirebaseToken(newViewerFirebaseToken(manager));
        controller.getIdentityCacher().updateIsRegistered(true);
        assertThat(controller.isGcmTokenSentServer(), is(false));

        controller.updateIsGcmTokenSentServer(true);
        assertThat(controller.isGcmTokenSentServer(), is(true));
        controller.updateIsGcmTokenSentServer(false);
        assertThat(controller.isGcmTokenSentServer(), is(false));
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================
}