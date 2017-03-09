package com.didekindroid.security;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.ManagerMock;
import com.didekindroid.MockActivity;
import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.core.ControllerFirebaseTokenIf;
import com.didekindroid.usuario.firebase.ControllerFirebaseToken;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.usuario.firebase.ViewerFirebaseTokenIf.ViewerFirebaseToken.newViewerFirebaseToken;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_TK_HANDLER;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro
 * Date: 29/06/15
 * Time: 08:11
 */
@RunWith(AndroidJUnit4.class)
public class TokenIdentityCacherTest_3 {

    @Rule
    public ActivityTestRule<? extends Activity> activityRule = new ActivityTestRule<>(MockActivity.class, true, true);

    @After
    public void cleanFileToken() throws UiException
    {
        cleanOptions(CLEAN_TK_HANDLER);
    }

    @Test
    public void testUpdateIsRegistered() throws Exception
    {
        Activity activity = activityRule.getActivity();
        ControllerFirebaseTokenIf controller = new ControllerFirebaseToken(newViewerFirebaseToken(new ManagerMock(activity)));
        TKhandler.updateIsRegistered(true);
        controller.updateIsGcmTokenSentServer(true);

        TKhandler.updateIsRegistered(false);
        // Check the change in the flag.
        assertThat(controller.isGcmTokenSentServer(), is(false));

        TKhandler.updateIsRegistered(true);
        // No change in flag.
        assertThat(controller.isGcmTokenSentServer(), is(false));
    }
}