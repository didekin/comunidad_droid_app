package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.security.TokenHandler.TKhandler;
import static com.didekindroid.usuario.activity.utils.CleanEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.checkNoToastInTest;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.checkToastInTest;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuario.dominio.DomainDataUtils.USER_PEPE;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static com.didekindroid.utils.UIutils.isRegisteredUser;
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
public class LoginAcTest_1 extends LoginAcTest{

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
        onView(withId(R.id.reg_usuario_email_editT)).check(matches(isDisplayed()));
        onView(withId(R.id.reg_usuario_password_ediT)).check(matches(isDisplayed()));
        onView(withId(R.id.login_ac_button)).check(matches(isDisplayed()));

        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        onView(withContentDescription("Navigate up")).check(doesNotExist());
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
}