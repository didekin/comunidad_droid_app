package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.oauth2.OauthToken.AccessToken;
import com.didekindroid.R;
import com.didekindroid.common.activity.UiException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekin.oauth2.OauthTokenHelper.HELPER;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.common.testutils.ActivityTestUtils.cleanWithTkhandler;
import static com.didekindroid.common.webservices.Oauth2Service.Oauth2;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.COMU_REAL_DROID;
import static com.didekindroid.usuario.testutils.UsuarioTestUtils.USER_DROID;
import static com.didekindroid.usuario.webservices.UsuarioService.ServOne;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 26/10/15
 * Time: 13:51
 */
@RunWith(AndroidJUnit4.class)
public class LoginAc_3_SlowTest extends LoginAcTest {

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(5000);
    }

    @Test
    public void testValidate_6() throws InterruptedException, UiException, IOException
    {
        // User in DB: wrong password three consecutive times. Choice "yes mail" in dialog.
        assertThat(ServOne.regComuAndUserAndUserComu(COMU_REAL_DROID).execute().body(), is(true));
        AccessToken token = Oauth2.getPasswordUserToken(USER_DROID.getUserName(), USER_DROID.getPassword());
        mActivity = mActivityRule.launchActivity(new Intent());

        getDialogFragment(USER_DROID.getUserName());
        onView(withText(R.string.send_password_by_mail_dialog)).inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withText(R.string.send_password_by_mail_YES)).inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withId(R.id.reg_usuario_email_editT)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usuario_password_ediT)).check(matches(isDisplayed()));
        onView(withId(R.id.login_ac_button)).check(matches(isDisplayed()));
        Thread.sleep(3000);
        checkToastInTest(R.string.password_new_in_login, mActivity);

        token = Oauth2.getRefreshUserToken(token.getRefreshToken().getValue());
        // Verificamos cambio de password.
        Thread.sleep(1500);
        String newPassword = ServOne.getUserData(HELPER.doBearerAccessTkHeader(token)).execute().body().getPassword();
        assertThat(newPassword, notNullValue());
        assertThat(newPassword.length() > 12, is(true));

        ServOne.deleteUser(HELPER.doBearerAccessTkHeader(token)).execute();
        cleanWithTkhandler();
    }
}
