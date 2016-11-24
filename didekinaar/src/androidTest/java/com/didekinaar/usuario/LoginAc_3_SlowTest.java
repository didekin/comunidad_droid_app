package com.didekinaar.usuario;

import android.content.Intent;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.oauth2.SpringOauthToken;
import com.didekinaar.R;
import com.didekinaar.exception.UiAarException;
import com.didekinaar.testutil.AarActivityTestUtils;
import com.didekinaar.testutil.UsuarioTestUtils;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static com.didekin.oauth2.OauthTokenHelper.HELPER;
import static com.didekinaar.security.Oauth2Service.Oauth2;
import static com.didekinaar.testutil.AarActivityTestUtils.checkToastInTest;
import static com.didekinaar.usuariocomunidad.AarUserComuService.AarUserComuServ;
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
    public void testValidate_6() throws InterruptedException, UiAarException, IOException
    {
        // User in DB: wrong password three consecutive times. Choice "yes mail" in dialog.
        boolean isRegistered = AarUserComuServ.regComuAndUserAndUserComu(UsuarioTestUtils.COMU_REAL_DROID).execute().body();
        assertThat(isRegistered, is(true));
        SpringOauthToken token = Oauth2.getPasswordUserToken(UsuarioTestUtils.USER_DROID.getUserName(), UsuarioTestUtils.USER_DROID.getPassword());
        mActivity = mActivityRule.launchActivity(new Intent());

        getDialogFragment(UsuarioTestUtils.USER_DROID.getUserName());
        onView(ViewMatchers.withText(R.string.send_password_by_mail_dialog)).inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withText(R.string.send_password_by_mail_YES)).inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());

        onView(ViewMatchers.withId(R.id.reg_usuario_email_editT)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.reg_usuario_password_ediT)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.login_ac_button)).check(matches(isDisplayed()));
        checkToastInTest(R.string.password_new_in_login, mActivity);
        Thread.sleep(2000);

        token = Oauth2.getRefreshUserToken(token.getRefreshToken().getValue());
        // Verificamos cambio de password.
        String newPassword = AarUsuarioService.AarUserServ.getUserData(HELPER.doBearerAccessTkHeader(token)).execute().body().getPassword();
        AarUsuarioService.AarUserServ.deleteUser(HELPER.doBearerAccessTkHeader(token)).execute();

        assertThat(newPassword, notNullValue());
        assertThat(newPassword.length() > 12, is(true));

        AarActivityTestUtils.cleanWithTkhandler();
    }
}
