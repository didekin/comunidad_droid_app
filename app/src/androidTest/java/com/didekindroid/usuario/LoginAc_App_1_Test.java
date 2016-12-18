package com.didekindroid.usuario;

import android.content.Intent;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import com.didekinaar.exception.UiException;
import com.didekinaar.usuario.LoginAcTest;
import com.didekinaar.usuario.testutil.UsuarioTestUtils;

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
import static com.didekinaar.testutil.AarActivityTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekinaar.testutil.AarActivityTestUtils.checkNoToastInTest;
import static com.didekinaar.utils.UIutils.isRegisteredUser;
import static com.didekindroid.usuariocomunidad.UserComuService.AppUserComuServ;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.signUpAndUpdateTk;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 24/11/16
 * Time: 14:39
 */
@RunWith(AndroidJUnit4.class)
public class LoginAc_App_1_Test extends LoginAcTest {

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(4000);
    }

    @Test
    public void testValidate_1() throws UiException, IOException
    {
        // Caso OK: user in DB, but without token in cache.
        assertThat(AppUserComuServ.regComuAndUserAndUserComu(COMU_TRAV_PLAZUELA_PEPE).execute().body(), is(true));

        mActivity = mActivityRule.launchActivity(new Intent());
        // Previous state.
        assertThat(isRegisteredUser(mActivity), is(false));
        assertThat(TKhandler.getAccessTokenInCache(), nullValue());

        onView(ViewMatchers.withId(com.didekinaar.R.id.reg_usuario_email_editT)).perform(typeText(UsuarioTestUtils.USER_PEPE.getUserName()));
        onView(ViewMatchers.withId(com.didekinaar.R.id.reg_usuario_password_ediT)).perform(typeText(UsuarioTestUtils.USER_PEPE.getPassword()));
        onView(ViewMatchers.withId(com.didekinaar.R.id.login_ac_button)).check(matches(isDisplayed())).perform(click());

        checkNoToastInTest(com.didekinaar.R.string.user_without_signedUp, mActivity);
        onView(ViewMatchers.withId(com.didekinaar.R.id.comu_search_ac_linearlayout)).check(matches(isDisplayed()));
        assertThat(isRegisteredUser(mActivity), is(true));
        assertThat(TKhandler.getAccessTokenInCache(), notNullValue());
        // NO navigate-up option in comunidad search.

        whatToClean = CLEAN_PEPE;
    }

    @Test
    public void testValidate_2() throws UiException, IOException
    {
        // Caso OK: user in DB, with token in cache.
        signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
        mActivity = mActivityRule.launchActivity(new Intent());
        // Previous state.
        assertThat(isRegisteredUser(mActivity), is(true));
        assertThat(TKhandler.getAccessTokenInCache(), notNullValue());

        onView(ViewMatchers.withId(com.didekinaar.R.id.reg_usuario_email_editT)).perform(typeText(UsuarioTestUtils.USER_PEPE.getUserName()));
        onView(ViewMatchers.withId(com.didekinaar.R.id.reg_usuario_password_ediT)).perform(typeText(UsuarioTestUtils.USER_PEPE.getPassword()));
        onView(ViewMatchers.withId(com.didekinaar.R.id.login_ac_button)).check(matches(isDisplayed())).perform(click());

        checkNoToastInTest(com.didekinaar.R.string.user_without_signedUp, mActivity);
        onView(ViewMatchers.withId(com.didekinaar.R.id.comu_search_ac_linearlayout)).check(matches(isDisplayed()));
        // NO navigate-up option in comunidad search.

        whatToClean = CLEAN_PEPE;
    }

    @Test
    public void testValidate_3() throws IOException, InterruptedException
    {
        whatToClean = CLEAN_PEPE;

        // Caso NO OK: user in DB, wrong password.
        assertThat(AppUserComuServ.regComuAndUserAndUserComu(COMU_TRAV_PLAZUELA_PEPE).execute().body(), is(true));
        mActivity = mActivityRule.launchActivity(new Intent());

        typeCheckClickPswdWrong(UsuarioTestUtils.USER_PEPE.getUserName());
        Thread.sleep(2000);
    }

    @Test
    public void testValidate_4() throws IOException, InterruptedException
    {
        whatToClean = CLEAN_PEPE;

        // Caso NO OK: user in DB, wrong password three consecutive times.
        assertThat(AppUserComuServ.regComuAndUserAndUserComu(COMU_TRAV_PLAZUELA_PEPE).execute().body(), is(true));
        mActivity = mActivityRule.launchActivity(new Intent());

        getDialogFragment(UsuarioTestUtils.USER_PEPE.getUserName());
        Thread.sleep(2000);
        onView(ViewMatchers.withText(com.didekinaar.R.string.send_password_by_mail_dialog)).inRoot(isDialog()).check(matches(isDisplayed()));
    }
}
