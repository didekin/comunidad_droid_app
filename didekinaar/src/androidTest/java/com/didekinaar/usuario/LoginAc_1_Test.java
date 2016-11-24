package com.didekinaar.usuario;

import android.content.Intent;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

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
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static com.didekinaar.security.TokenHandler.TKhandler;
import static com.didekinaar.testutil.AarActivityTestUtils.checkNoToastInTest;
import static com.didekinaar.testutil.AarActivityTestUtils.checkToastInTest;
import static com.didekinaar.testutil.CleanUserEnum.CLEAN_PEPE;
import static com.didekinaar.usuariocomunidad.AarUserComuService.AarUserComuServ;
import static com.didekinaar.utils.UIutils.isRegisteredUser;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 09/10/15
 * Time: 12:24
 */
@RunWith(AndroidJUnit4.class)
public class LoginAc_1_Test extends LoginAcTest{

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(5000);
    }

//    ==========================================================================================================

    @Test
    public void testOnCreate() throws Exception
    {
        mActivity = mActivityRule.launchActivity(new Intent());
        assertThat(mActivity, notNullValue());
        onView(ViewMatchers.withId(R.id.reg_usuario_email_editT)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.reg_usuario_password_ediT)).check(matches(isDisplayed()));
        onView(ViewMatchers.withId(R.id.login_ac_button)).check(matches(isDisplayed()));

        onView(ViewMatchers.withId(R.id.appbar)).check(matches(isDisplayed()));
        onView(ViewMatchers.withContentDescription(R.string.navigate_up_txt)).check(matches(isDisplayed()));
    }

    @Test
    public void testMakeBean_1()
    {
        mActivity = mActivityRule.launchActivity(new Intent());

        onView(ViewMatchers.withId(R.id.reg_usuario_email_editT)).perform(typeText("user_wrong"));
        onView(ViewMatchers.withId(R.id.reg_usuario_password_ediT)).perform(typeText("psw"));

        onView(ViewMatchers.withId(R.id.login_ac_button)).check(matches(isDisplayed())).perform(click());
        checkToastInTest(R.string.error_validation_msg, mActivity,
                R.string.email_hint, R.string.password);
    }

    @Test
    public void testValidate_1() throws InterruptedException
    {
        // Caso NO OK: user not in DB.
        mActivity = mActivityRule.launchActivity(new Intent());
        assertThat(isRegisteredUser(mActivity), is(false));

        onView(ViewMatchers.withId(R.id.reg_usuario_email_editT)).perform(typeText("user@notfound.com"));
        onView(ViewMatchers.withId(R.id.reg_usuario_password_ediT)).perform(typeText("password_ok"));

        onView(ViewMatchers.withId(R.id.login_ac_button)).check(matches(isDisplayed())).perform(click());
        //Invitación a hacer login.
        onView(ViewMatchers.withId(R.id.login_ac_layout)).check(matches(isDisplayed()));
        checkToastInTest(R.string.user_without_signedUp, mActivity);
        Thread.sleep(2000);
    }

    @Test
    public void testValidate_2() throws UiAarException, IOException
    {
        // Caso OK: user in DB, but without token in cache.
        assertThat(AarUserComuServ.regComuAndUserAndUserComu(UsuarioTestUtils.COMU_TRAV_PLAZUELA_PEPE).execute().body(), is(true));

        mActivity = mActivityRule.launchActivity(new Intent());
        // Previous state.
        assertThat(isRegisteredUser(mActivity), is(false));
        assertThat(TKhandler.getAccessTokenInCache(), nullValue());

        onView(ViewMatchers.withId(R.id.reg_usuario_email_editT)).perform(typeText(UsuarioTestUtils.USER_PEPE.getUserName()));
        onView(ViewMatchers.withId(R.id.reg_usuario_password_ediT)).perform(typeText(UsuarioTestUtils.USER_PEPE.getPassword()));
        onView(ViewMatchers.withId(R.id.login_ac_button)).check(matches(isDisplayed())).perform(click());

        checkNoToastInTest(R.string.user_without_signedUp, mActivity);
        onView(ViewMatchers.withId(R.id.comu_search_ac_linearlayout)).check(matches(isDisplayed()));
        assertThat(isRegisteredUser(mActivity), is(true));
        assertThat(TKhandler.getAccessTokenInCache(), notNullValue());
        // NO navigate-up option in comunidad search.

        whatToClean = CLEAN_PEPE;
    }

    @Test
    public void testValidate_3() throws UiAarException, IOException
    {
        // Caso OK: user in DB, with token in cache.
        AarActivityTestUtils.signUpAndUpdateTk(UsuarioTestUtils.COMU_TRAV_PLAZUELA_PEPE);
        mActivity = mActivityRule.launchActivity(new Intent());
        // Previous state.
        assertThat(isRegisteredUser(mActivity), is(true));
        assertThat(TKhandler.getAccessTokenInCache(), notNullValue());

        onView(ViewMatchers.withId(R.id.reg_usuario_email_editT)).perform(typeText(UsuarioTestUtils.USER_PEPE.getUserName()));
        onView(ViewMatchers.withId(R.id.reg_usuario_password_ediT)).perform(typeText(UsuarioTestUtils.USER_PEPE.getPassword()));
        onView(ViewMatchers.withId(R.id.login_ac_button)).check(matches(isDisplayed())).perform(click());

        checkNoToastInTest(R.string.user_without_signedUp, mActivity);
        onView(ViewMatchers.withId(R.id.comu_search_ac_linearlayout)).check(matches(isDisplayed()));
        // NO navigate-up option in comunidad search.

        whatToClean = CLEAN_PEPE;
    }

    @Test
    public void testValidate_4() throws IOException, InterruptedException
    {
        whatToClean = CLEAN_PEPE;

        // Caso NO OK: user in DB, wrong password.
        assertThat(AarUserComuServ.regComuAndUserAndUserComu(UsuarioTestUtils.COMU_TRAV_PLAZUELA_PEPE).execute().body(), is(true));
        mActivity = mActivityRule.launchActivity(new Intent());

        typeCheckClickPswdWrong(UsuarioTestUtils.USER_PEPE.getUserName());
        Thread.sleep(2000);
    }

    @Test
    public void testValidate_5() throws IOException, InterruptedException
    {
        whatToClean = CLEAN_PEPE;

        // Caso NO OK: user in DB, wrong password three consecutive times.
        assertThat(AarUserComuServ.regComuAndUserAndUserComu(UsuarioTestUtils.COMU_TRAV_PLAZUELA_PEPE).execute().body(), is(true));
        mActivity = mActivityRule.launchActivity(new Intent());

        getDialogFragment(UsuarioTestUtils.USER_PEPE.getUserName());
        Thread.sleep(2000);
        onView(ViewMatchers.withText(R.string.send_password_by_mail_dialog)).inRoot(isDialog()).check(matches(isDisplayed()));
    }
}