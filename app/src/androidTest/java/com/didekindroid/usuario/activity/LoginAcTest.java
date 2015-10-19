package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.didekin.retrofitcl.OauthToken;
import com.didekindroid.R;
import com.didekindroid.usuario.activity.utils.CleanEnum;
import org.junit.*;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.*;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.*;
import static com.didekin.retrofitcl.OauthTokenHelper.HELPER;
import static com.didekindroid.security.TokenHandler.TKhandler;
import static com.didekindroid.uiutils.UIutils.isRegisteredUser;
import static com.didekindroid.usuario.activity.utils.CleanEnum.CLEAN_NOTHING;
import static com.didekindroid.usuario.activity.utils.CleanEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.*;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuario.dominio.DomainDataUtils.USER_PEPE;
import static com.didekindroid.usuario.webservices.Oauth2Service.Oauth2;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 09/10/15
 * Time: 12:24
 */
@RunWith(AndroidJUnit4.class)
public class LoginAcTest {

    LoginAc mActivity;
    CleanEnum whatToClean = CLEAN_NOTHING;

    @Rule
    public ActivityTestRule<LoginAc> mActivityRule = new ActivityTestRule<LoginAc>(LoginAc.class, true, false);

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(5000);
    }

    @Before
    public void setUp() throws Exception
    {
        Thread.sleep(2000);
    }

    @After
    public void tearDown() throws Exception
    {
        cleanOptions(whatToClean);
    }

//    ==========================================================================================================

    @Test
    public void testOnCreate() throws Exception
    {
        mActivity = mActivityRule.launchActivity(new Intent());
        assertThat(mActivity, notNullValue());
        onView(withId(R.id.reg_usuario_email_editT)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usuario_password_ediT)).check(matches(isDisplayed()));
        onView(withId(R.id.login_ac_button)).check(matches(isDisplayed()));
    }

    @Test
    public void testMakeBean_1()
    {
        mActivity = mActivityRule.launchActivity(new Intent());
        onView(withId(R.id.reg_usuario_email_editT)).perform(typeText("user_wrong"));
        onView(withId(R.id.reg_usuario_password_ediT)).perform(typeText("psw"));

        onView(withId(R.id.login_ac_button)).check(matches(isDisplayed())).perform(click());
        checkToastInTest(R.string.error_validation_msg, mActivity,
                R.string.email_hint, R.string.password);
    }

    @Test
    public void testValidate_1()
    {
        // User not in DB.
        mActivity = mActivityRule.launchActivity(new Intent());
        assertThat(isRegisteredUser(mActivity), is(false));

        onView(withId(R.id.reg_usuario_email_editT)).perform(typeText("user@notfound.com"));
        onView(withId(R.id.reg_usuario_password_ediT)).perform(typeText("password_ok"));

        onView(withId(R.id.login_ac_button)).check(matches(isDisplayed())).perform(click());
        //Invitación a buscar su comunidad y registrarse.
        onView(withId(R.id.comu_search_ac_layout)).check(matches(isDisplayed()));
        checkToastInTest(R.string.user_without_signedUp, mActivity);
    }

    @Test
    public void testValidate_2()
    {
        // User in DB.
        assertThat(ServOne.regComuAndUserAndUserComu(COMU_TRAV_PLAZUELA_PEPE), is(true));

        mActivity = mActivityRule.launchActivity(new Intent());
        // Previous state.
        assertThat(isRegisteredUser(mActivity), is(false));
        assertThat(TKhandler.getAccessTokenInCache(), nullValue());

        onView(withId(R.id.reg_usuario_email_editT)).perform(typeText(USER_PEPE.getUserName()));
        onView(withId(R.id.reg_usuario_password_ediT)).perform(typeText(USER_PEPE.getPassword()));
        onView(withId(R.id.login_ac_button)).check(matches(isDisplayed())).perform(click());

        checkNoToastInTest(R.string.user_without_signedUp, mActivity);
        onView(withId(R.id.comu_search_ac_layout)).check(matches(isDisplayed()));
        assertThat(isRegisteredUser(mActivity), is(true));
        assertThat(TKhandler.getAccessTokenInCache(), notNullValue());

        whatToClean = CLEAN_PEPE;
    }

    @Test
    public void testValidate_3()
    {
        whatToClean = CLEAN_PEPE;

        // User in DB: wrong password.
        assertThat(ServOne.regComuAndUserAndUserComu(COMU_TRAV_PLAZUELA_PEPE), is(true));
        mActivity = mActivityRule.launchActivity(new Intent());

        typeCheckClickPswdWrong();
    }

    @Test
    public void testValidate_4()
    {
        whatToClean = CLEAN_PEPE;

        // User in DB: wrong password three consecutive times.
        assertThat(ServOne.regComuAndUserAndUserComu(COMU_TRAV_PLAZUELA_PEPE), is(true));
        mActivity = mActivityRule.launchActivity(new Intent());

        getDialogFragment();
        onView(withText(R.string.send_password_by_mail_dialog)).inRoot(isDialog()).check(matches(isDisplayed()));
    }

    @Test
    public void testValidate_5() throws InterruptedException
    {
        whatToClean = CLEAN_PEPE;

        // User in DB: wrong password three consecutive times. Choice "not mail" in dialog.
        assertThat(ServOne.regComuAndUserAndUserComu(COMU_TRAV_PLAZUELA_PEPE), is(true));
        mActivity = mActivityRule.launchActivity(new Intent());

        getDialogFragment();
        onView(withText(R.string.send_password_by_mail_dialog)).inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withText(R.string.send_password_by_mail_NO)).inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());

        //Invitación a buscar su comunidad y registrarse.
        onView(withId(R.id.comu_search_ac_layout)).check(matches(isDisplayed()));
        Thread.sleep(16000);
        checkToastInTest(R.string.login_wrong_no_mail, mActivity);
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
        Thread.sleep(10000);

        onView(withId(R.id.reg_usuario_email_editT)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usuario_password_ediT)).check(matches(isDisplayed()));
        onView(withId(R.id.login_ac_button)).check(matches(isDisplayed()));
        checkToastInTest(R.string.password_new_in_login, mActivity);

        token = Oauth2.getRefreshUserToken(token.getRefreshToken().getValue());
        ServOne.deleteUser(HELPER.doBearerAccessTkHeader(token));
        cleanWithTkhandler();
    }

//    ========================== Utility methods ============================

    private void typeCheckClickPswdWrong()
    {
        onView(withId(R.id.reg_usuario_email_editT)).perform(typeText(USER_PEPE.getUserName()));
        onView(withId(R.id.reg_usuario_password_ediT)).perform(typeText("pasword_wrong"));
        onView(withId(R.id.login_ac_button)).check(matches(isDisplayed())).perform(click());

        checkToastInTest(R.string.password_wrong_in_login, mActivity);
        onView(withId(R.id.login_ac_layout)).check(matches(isDisplayed()));
    }

    private void reTypeCheckClickPswdWrong()
    {
        onView(withId(R.id.reg_usuario_email_editT)).perform(replaceText(USER_PEPE.getUserName()));
        onView(withId(R.id.reg_usuario_password_ediT)).perform(replaceText("pasword_wrong"));
        onView(withId(R.id.login_ac_button)).check(matches(isDisplayed())).perform(click());

        checkToastInTest(R.string.password_wrong_in_login, mActivity);
        onView(withId(R.id.login_ac_layout)).check(matches(isDisplayed()));
    }

    private void getDialogFragment()
    {
        typeCheckClickPswdWrong();
        reTypeCheckClickPswdWrong();
        reTypeCheckClickPswdWrong();

        onView(withId(R.id.reg_usuario_email_editT)).perform(replaceText(USER_PEPE.getUserName()));
        onView(withId(R.id.reg_usuario_password_ediT)).perform(replaceText("pasword_wrong"));
        onView(withId(R.id.login_ac_button)).check(matches(isDisplayed())).perform(click());
    }
}