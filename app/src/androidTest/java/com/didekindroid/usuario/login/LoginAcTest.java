package com.didekindroid.usuario.login;

import android.app.Activity;
import android.content.Intent;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;

import com.didekindroid.ExtendableTestAc;
import com.didekindroid.R;
import com.didekinlib.http.oauth2.SpringOauthToken;
import com.didekinlib.model.usuario.Usuario;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.R.id.comu_search_ac_linearlayout;
import static com.didekindroid.R.id.login_ac_button;
import static com.didekindroid.R.id.reg_usuario_email_editT;
import static com.didekindroid.R.id.reg_usuario_password_ediT;
import static com.didekindroid.R.string.send_password_by_mail_dialog;
import static com.didekindroid.security.Oauth2DaoRemote.Oauth2;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ActivityTestUtils.checkNoToastInTest;
import static com.didekindroid.testutil.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOneUser;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanWithTkhandler;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_DROID;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekinlib.http.oauth2.OauthTokenHelper.HELPER;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 26/10/15
 * Time: 13:55
 */
public class LoginAcTest implements ExtendableTestAc {

    @Rule
    public ActivityTestRule<? extends Activity> mActivityRule = getActivityRule();
    protected LoginAc mActivity;
    protected int activityLayoutId = R.id.login_ac_layout;

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(4000);
    }

    @Before
    public void setUp() throws Exception
    {
        Thread.sleep(3000);
    }

    @Override
    public Usuario registerUser() throws Exception
    {
        return signUpAndUpdateTk(COMU_REAL_DROID);
    }

    @Override
    public ActivityTestRule<? extends Activity> getActivityRule()
    {
        return new ActivityTestRule<>(LoginAc.class, true, false);
    }

    @Override
    public void checkNavigateUp()
    {
        throw new UnsupportedOperationException("NO NAVIGATE-UP in LoginAc activity");
    }

    @Override
    public int getNextViewResourceId()
    {
        return comu_search_ac_linearlayout;
    }

    //    =====================================  TESTS  ==========================================

    @Test
    public final void testOnCreate() throws Exception
    {
        mActivity = (LoginAc) mActivityRule.launchActivity(new Intent());
        assertThat(mActivity, notNullValue());
        onView(withId(reg_usuario_email_editT)).check(matches(isDisplayed()));
        onView(withId(reg_usuario_password_ediT)).check(matches(isDisplayed()));
        onView(withId(login_ac_button)).check(matches(isDisplayed()));

        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        onView(withContentDescription(R.string.navigate_up_txt)).check(matches(isDisplayed()));
    }

    @Test
    public final void testMakeBean_1()
    {
        mActivity = (LoginAc) mActivityRule.launchActivity(new Intent());

        onView(withId(reg_usuario_email_editT)).perform(typeText("user_wrong"));
        onView(withId(reg_usuario_password_ediT)).perform(typeText("psw"));

        onView(withId(login_ac_button)).check(matches(isDisplayed())).perform(click());
        checkToastInTest(R.string.error_validation_msg, mActivity,
                R.string.email_hint, R.string.password);
    }

    @Test
    public final void testValidate_0() throws InterruptedException
    {
        // Caso NO OK: user not in DB.
        mActivity = (LoginAc) mActivityRule.launchActivity(new Intent());
        assertThat(TKhandler.isRegisteredUser(), is(false));

        onView(withId(reg_usuario_email_editT)).perform(typeText("user@notfound.com"));
        onView(withId(reg_usuario_password_ediT)).perform(typeText("password_ok"));

        onView(withId(login_ac_button)).check(matches(isDisplayed())).perform(click());
        //Invitación a hacer login.
        onView(withId(R.id.login_ac_layout)).check(matches(isDisplayed()));
        checkToastInTest(R.string.user_without_signedUp, mActivity);
        Thread.sleep(2000);
    }

    @Test
    public void testValidate_1() throws Exception
    {
        // Caso OK: user in DB, but without token in cache and isRegistered == false.
        Usuario registeredUser = registerUser();
        assertThat(registeredUser, notNullValue());
        mActivity = (LoginAc) mActivityRule.launchActivity(new Intent());
        // Cleaning identity cache.
        cleanWithTkhandler();
        assertThat(TKhandler.isRegisteredUser(), is(false));
        assertThat(TKhandler.getAccessTokenInCache(), nullValue());

        onView(withId(reg_usuario_email_editT)).perform(typeText(registeredUser.getUserName()));
        onView(withId(reg_usuario_password_ediT)).perform(typeText(registeredUser.getPassword()));
        onView(withId(login_ac_button)).check(matches(isDisplayed())).perform(click());

        checkNoToastInTest(R.string.user_without_signedUp, mActivity);
        onView(withId(getNextViewResourceId())).check(matches(isDisplayed()));
        assertThat(TKhandler.isRegisteredUser(), is(true));
        assertThat(TKhandler.getAccessTokenInCache(), Matchers.notNullValue());
        // NO navigate-up option in comunidad search.

        cleanOneUser(registeredUser);
    }

    @Test
    public void testValidate_2() throws Exception
    {
        // Caso OK: user in DB, with token in cache.
        Usuario registeredUser = registerUser();
        assertThat(registerUser(), notNullValue());
        mActivity = (LoginAc) mActivityRule.launchActivity(new Intent());
        // Identity cache.
        assertThat(TKhandler.isRegisteredUser(), is(true));
        assertThat(TKhandler.getAccessTokenInCache(), Matchers.notNullValue());

        onView(withId(reg_usuario_email_editT)).perform(typeText(registeredUser.getUserName()));
        onView(withId(reg_usuario_password_ediT)).perform(typeText(registeredUser.getPassword()));
        onView(withId(login_ac_button)).check(matches(isDisplayed())).perform(click());

        checkNoToastInTest(R.string.user_without_signedUp, mActivity);
        onView(withId(getNextViewResourceId())).check(matches(isDisplayed()));
        // NO navigate-up option in comunidad search.

        cleanOneUser(registeredUser);
    }

    @Test
    public void testValidate_3() throws Exception
    {
        // Caso NO OK: user in DB, token in cache, wrong password.
        Usuario registeredUser = registerUser();
        assertThat(registerUser(), notNullValue());
        mActivity = (LoginAc) mActivityRule.launchActivity(new Intent());

        typeCheckClickPswdWrong(registeredUser.getUserName());
        cleanOneUser(registeredUser);
    }

    @Test
    public void testValidate_4() throws Exception
    {
        // Caso NO OK: user in DB, wrong password three consecutive times.
        Usuario registeredUser = registerUser();
        assertThat(registerUser(), notNullValue());
        mActivity = (LoginAc) mActivityRule.launchActivity(new Intent());

        getDialogFragment(registeredUser.getUserName());
        Thread.sleep(2000);
        onView(withText(send_password_by_mail_dialog)).inRoot(isDialog()).check(matches(isDisplayed()));
        cleanOneUser(registeredUser);
    }

    @Test
    public void testValidate_5() throws Exception
    {
        // User in DB: wrong password three consecutive times. Choice "not mail" in dialog.
        Usuario registeredUser = registerUser();
        assertThat(registerUser(), notNullValue());
        mActivity = (LoginAc) mActivityRule.launchActivity(new Intent());

        getDialogFragment(registeredUser.getUserName());
        onView(withText(send_password_by_mail_dialog)).inRoot(isDialog())
                .check(matches(isDisplayed()));
        Thread.sleep(7000);
        onView(withText(R.string.send_password_by_mail_NO)).inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());

        //Invitación a buscar su comunidad y registrarse.
        onView(ViewMatchers.withId(getNextViewResourceId())).check(matches(isDisplayed()));
        checkToastInTest(R.string.login_wrong_no_mail, mActivity);

        cleanOneUser(registeredUser);
    }

    @Test
    public void testValidate_6() throws Exception
    {
        // User in DB: wrong password three consecutive times. Choice "yes mail" in dialog.
        Usuario registeredUser = registerUser();
        assertThat(registerUser(), notNullValue());
        mActivity = (LoginAc) mActivityRule.launchActivity(new Intent());

        SpringOauthToken token = Oauth2.getPasswordUserToken(registeredUser.getUserName(), registeredUser.getPassword());

        getDialogFragment(registeredUser.getUserName());
        onView(withText(send_password_by_mail_dialog)).inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withText(R.string.send_password_by_mail_YES)).inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());

        onView(withId(reg_usuario_email_editT)).check(matches(isDisplayed()));
        onView(withId(reg_usuario_password_ediT)).check(matches(isDisplayed()));
        onView(withId(login_ac_button)).check(matches(isDisplayed()));
        checkToastInTest(R.string.password_new_in_login, mActivity);
        Thread.sleep(2000);

        token = Oauth2.getRefreshUserToken(token.getRefreshToken().getValue());
        // Verificamos cambio de password.
        String newPassword = usuarioDao.getEndPoint().getUserData(HELPER.doBearerAccessTkHeader(token)).execute().body().getPassword();
        assertThat(newPassword, notNullValue());
        assertThat(newPassword.length() > 12, is(true));

        usuarioDao.getEndPoint().deleteUser(HELPER.doBearerAccessTkHeader(token)).execute();
        cleanWithTkhandler();
    }

    //    ========================== Utility methods ============================

    private void typeCheckClickPswdWrong(String userName)
    {
        if (userName != null) {
            onView(withId(reg_usuario_email_editT)).perform(typeText(userName));
        }
        onView(withId(reg_usuario_password_ediT)).perform(typeText("pasword_wrong"));
        onView(withId(login_ac_button)).check(matches(isDisplayed())).perform(click());

        checkToastInTest(R.string.password_wrong_in_login, mActivity);
        onView(withId(activityLayoutId)).check(matches(isDisplayed()));
    }

    private void reTypeCheckClickPswdWrong(String userName)
    {
        onView(withId(reg_usuario_email_editT)).perform(replaceText(userName));
        onView(withId(reg_usuario_password_ediT)).perform(replaceText("pasword_wrong"));
        onView(withId(login_ac_button)).check(matches(isDisplayed())).perform(click());

        checkToastInTest(R.string.password_wrong_in_login, mActivity);
        onView(withId(activityLayoutId)).check(matches(isDisplayed()));
    }

    private void getDialogFragment(String userName) throws InterruptedException
    {
        typeCheckClickPswdWrong(userName);
        Thread.sleep(1000);
        reTypeCheckClickPswdWrong(userName);
        Thread.sleep(1000);
        reTypeCheckClickPswdWrong(userName);

        onView(withId(reg_usuario_email_editT)).perform(replaceText(userName));
        onView(withId(reg_usuario_password_ediT)).perform(replaceText("pasword_wrong"));
        onView(withId(login_ac_button)).check(matches(isDisplayed())).perform(click());
    }
}
