package com.didekinaar.usuario;

import android.content.Intent;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import com.didekinaar.R;
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
import static com.didekinaar.testutil.AarActivityTestUtils.checkToastInTest;
import static com.didekinaar.testutil.CleanUserEnum.CLEAN_PEPE;
import static com.didekinaar.usuariocomunidad.AarUserComuService.AarUserComuServ;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 26/10/15
 * Time: 13:48
 */
@RunWith(AndroidJUnit4.class)
public class LoginAc_2_SlowTest extends LoginAcTest {

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(5000);
    }

    @Test
    public void testValidate_5() throws InterruptedException, IOException
    {
        whatToClean = CLEAN_PEPE;

        // User in DB: wrong password three consecutive times. Choice "not mail" in dialog.
        assertThat(AarUserComuServ.regComuAndUserAndUserComu(UsuarioTestUtils.COMU_TRAV_PLAZUELA_PEPE).execute().body(), is(true));
        mActivity = mActivityRule.launchActivity(new Intent());

        getDialogFragment(UsuarioTestUtils.USER_PEPE.getUserName());
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
