package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.retrofitcl.OauthToken;
import com.didekindroid.R;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekin.retrofitcl.OauthTokenHelper.HELPER;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.checkToastInTest;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.cleanWithTkhandler;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuario.dominio.DomainDataUtils.USER_PEPE;
import static com.didekindroid.usuario.webservices.Oauth2Service.Oauth2;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 26/10/15
 * Time: 13:51
 */
@RunWith(AndroidJUnit4.class)
public class LoginAcTest_3 extends LoginAcTest {

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(5000);
    }

    @Test
    public void testValidate_6() throws InterruptedException
    {
        // User in DB: wrong password three consecutive times. Choice "yes mail" in dialog.
        assertThat(ServOne.regComuAndUserAndUserComu(COMU_TRAV_PLAZUELA_PEPE), is(true));
        OauthToken.AccessToken token = Oauth2.getPasswordUserToken(USER_PEPE.getUserName(), USER_PEPE.getPassword());
        mActivity = mActivityRule.launchActivity(new Intent());

        getDialogFragment();
        onView(withText(R.string.send_password_by_mail_dialog)).inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withText(R.string.send_password_by_mail_YES)).inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());
        Thread.sleep(5000);

        onView(withId(R.id.reg_usuario_email_editT)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usuario_password_ediT)).check(matches(isDisplayed()));
        onView(withId(R.id.login_ac_button)).check(matches(isDisplayed()));
        checkToastInTest(R.string.password_new_in_login, mActivity);

        token = Oauth2.getRefreshUserToken(token.getRefreshToken().getValue());
        ServOne.deleteUser(HELPER.doBearerAccessTkHeader(token));
        cleanWithTkhandler();
    }
}