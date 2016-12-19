package com.didekinaar.usuario;

import android.app.Activity;
import android.content.Intent;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;

import com.didekin.oauth2.SpringOauthToken;
import com.didekinaar.R;
import com.didekinaar.testutil.AarActivityTestUtils;
import com.didekinaar.testutil.AarActivityTestUtils.CleanUserEnum;
import com.didekinaar.testutil.ExtendableTestAc;
import com.didekinaar.usuario.login.LoginAc;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekin.oauth2.OauthTokenHelper.HELPER;
import static com.didekinaar.security.Oauth2Service.Oauth2;
import static com.didekinaar.testutil.AarActivityTestUtils.CleanUserEnum.CLEAN_NOTHING;
import static com.didekinaar.testutil.AarActivityTestUtils.checkToastInTest;
import static com.didekinaar.testutil.AarActivityTestUtils.cleanOptions;
import static com.didekinaar.usuario.UsuarioService.AarUserServ;
import static com.didekinaar.usuario.testutil.UsuarioTestUtils.USER_DROID;
import static com.didekinaar.utils.UIutils.isRegisteredUser;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 26/10/15
 * Time: 13:55
 */
public abstract class LoginAcTest implements ExtendableTestAc {

    protected LoginAc mActivity;
    protected CleanUserEnum whatToClean = CLEAN_NOTHING;
    protected int activityLayoutId = R.id.login_ac_layout;

    @Rule
    public ActivityTestRule<? extends Activity> mActivityRule = getActivityRule();

    @Before
    public void setUp() throws Exception
    {
        Thread.sleep(3000);
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(whatToClean);
    }

    //    =====================================  TESTS  ==========================================

    @Test
    public final void testOnCreate() throws Exception
    {
        mActivity = (LoginAc) mActivityRule.launchActivity(new Intent());
        assertThat(mActivity, Matchers.notNullValue());
        onView(ViewMatchers.withId(R.id.reg_usuario_email_editT)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.reg_usuario_password_ediT)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.login_ac_button)).check(matches(isDisplayed()));

        onView(ViewMatchers.withId(R.id.appbar)).check(matches(isDisplayed()));
        onView(ViewMatchers.withContentDescription(R.string.navigate_up_txt)).check(matches(isDisplayed()));
    }

    @Test
    public final void testMakeBean_1()
    {
        mActivity = (LoginAc) mActivityRule.launchActivity(new Intent());

        onView(ViewMatchers.withId(R.id.reg_usuario_email_editT)).perform(typeText("user_wrong"));
        onView(ViewMatchers.withId(R.id.reg_usuario_password_ediT)).perform(typeText("psw"));

        onView(ViewMatchers.withId(R.id.login_ac_button)).check(matches(isDisplayed())).perform(click());
        checkToastInTest(R.string.error_validation_msg, mActivity,
                R.string.email_hint, R.string.password);
    }

    @Test
    public final void testValidate_0() throws InterruptedException
    {
        // Caso NO OK: user not in DB.
        mActivity = (LoginAc) mActivityRule.launchActivity(new Intent());
        assertThat(isRegisteredUser(mActivity), is(false));

        onView(ViewMatchers.withId(R.id.reg_usuario_email_editT)).perform(typeText("user@notfound.com"));
        onView(ViewMatchers.withId(R.id.reg_usuario_password_ediT)).perform(typeText("password_ok"));

        onView(ViewMatchers.withId(R.id.login_ac_button)).check(matches(isDisplayed())).perform(click());
        //Invitación a hacer login.
        onView(ViewMatchers.withId(R.id.login_ac_layout)).check(matches(isDisplayed()));
        checkToastInTest(R.string.user_without_signedUp, mActivity);
        Thread.sleep(2000);
    }

    //    ========================== Utility methods ============================

    public final void typeCheckClickPswdWrong(String userName)
    {
        if (userName != null) {
            onView(ViewMatchers.withId(R.id.reg_usuario_email_editT)).perform(typeText(userName));
        }
        onView(ViewMatchers.withId(R.id.reg_usuario_password_ediT)).perform(typeText("pasword_wrong"));
        onView(ViewMatchers.withId(R.id.login_ac_button)).check(matches(isDisplayed())).perform(click());

        checkToastInTest(R.string.password_wrong_in_login, mActivity);
        onView(withId(activityLayoutId)).check(matches(isDisplayed()));
    }

    public final void reTypeCheckClickPswdWrong(String userName)
    {
        onView(ViewMatchers.withId(R.id.reg_usuario_email_editT)).perform(replaceText(userName));
        onView(ViewMatchers.withId(R.id.reg_usuario_password_ediT)).perform(replaceText("pasword_wrong"));
        onView(ViewMatchers.withId(R.id.login_ac_button)).check(matches(isDisplayed())).perform(click());

        checkToastInTest(R.string.password_wrong_in_login, mActivity);
        onView(withId(activityLayoutId)).check(matches(isDisplayed()));
    }

    public final void getDialogFragment(String userName) throws InterruptedException
    {
        typeCheckClickPswdWrong(userName);
        Thread.sleep(1000);
        reTypeCheckClickPswdWrong(userName);
        Thread.sleep(1000);
        reTypeCheckClickPswdWrong(userName);

        onView(ViewMatchers.withId(R.id.reg_usuario_email_editT)).perform(replaceText(userName));
        onView(ViewMatchers.withId(R.id.reg_usuario_password_ediT)).perform(replaceText("pasword_wrong"));
        onView(ViewMatchers.withId(R.id.login_ac_button)).check(matches(isDisplayed())).perform(click());
    }


    public final void checkLoginWithThreeErrors() throws Exception
    {
        // User in DB: wrong password three consecutive times. Choice "yes mail" in dialog.
        assertThat(registerUser(), is(true));
        SpringOauthToken token = Oauth2.getPasswordUserToken(USER_DROID.getUserName(), USER_DROID.getPassword());

        getDialogFragment(USER_DROID.getUserName());
        onView(ViewMatchers.withText(R.string.send_password_by_mail_dialog)).inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(ViewMatchers.withText(R.string.send_password_by_mail_YES)).inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withId(R.id.reg_usuario_email_editT)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usuario_password_ediT)).check(matches(isDisplayed()));
        onView(withId(R.id.login_ac_button)).check(matches(isDisplayed()));
        checkToastInTest(R.string.password_new_in_login, mActivity);
        Thread.sleep(2000);

        token = Oauth2.getRefreshUserToken(token.getRefreshToken().getValue());
        // Verificamos cambio de password.
        String newPassword = AarUserServ.getUserData(HELPER.doBearerAccessTkHeader(token)).execute().body().getPassword();
        AarUserServ.deleteUser(HELPER.doBearerAccessTkHeader(token)).execute();

        assertThat(newPassword, notNullValue());
        assertThat(newPassword.length() > 12, is(true));

        AarActivityTestUtils.cleanWithTkhandler();
    }
}
