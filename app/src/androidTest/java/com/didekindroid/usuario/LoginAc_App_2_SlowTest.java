package com.didekindroid.usuario;

import android.app.Activity;
import android.content.Intent;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekinaar.R;
import com.didekinaar.usuario.login.LoginAcTest;
import com.didekinaar.usuario.login.LoginAc;
import com.didekinaar.usuario.testutil.UsuarioDataTestUtils;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static com.didekinaar.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekinaar.testutil.AarActivityTestUtils.checkToastInTest;
import static com.didekindroid.usuariocomunidad.UserComuService.AppUserComuServ;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestUtil.COMU_TRAV_PLAZUELA_PEPE;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 26/10/15
 * Time: 13:48
 */
@RunWith(AndroidJUnit4.class)
public class LoginAc_App_2_SlowTest extends LoginAcTest {

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(4000);
    }

    @Override
    public boolean registerUser() throws Exception
    {
        throw new UnsupportedOperationException("NO registerUser() in LoginAc_App_2_SlowTest");
    }

    @Override
    public ActivityTestRule<? extends Activity> getActivityRule()
    {
        return new ActivityTestRule<>(LoginAppAc.class, true, false);
    }

    @Override
    public void checkNavigateUp()
    {
        throw new UnsupportedOperationException("NO NAVIGATE-UP in LoginAppAc activity");
    }

    // ======================================  TESTS =====================================

    @Test
    public void testValidate_1() throws InterruptedException, IOException
    {
        whatToClean = CLEAN_PEPE;

        // User in DB: wrong password three consecutive times. Choice "not mail" in dialog.
        assertThat(AppUserComuServ.regComuAndUserAndUserComu(COMU_TRAV_PLAZUELA_PEPE).execute().body(), is(true));
        mActivity = (LoginAc) mActivityRule.launchActivity(new Intent());

        getDialogFragment(UsuarioDataTestUtils.USER_PEPE.getUserName());
        onView(ViewMatchers.withText(R.string.send_password_by_mail_dialog)).inRoot(isDialog())
                .check(matches(isDisplayed()));
        Thread.sleep(7000);
        onView(ViewMatchers.withText(R.string.send_password_by_mail_NO)).inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());

        //Invitación a buscar su comunidad y registrarse.
        onView(ViewMatchers.withId(R.id.comu_search_ac_linearlayout)).check(matches(isDisplayed()));
        checkToastInTest(R.string.login_wrong_no_mail, mActivity);
    }
}
