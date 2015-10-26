package com.didekindroid.usuario.activity;

import android.content.Intent;
import android.support.test.runner.AndroidJUnit4;

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
import static com.didekindroid.usuario.activity.utils.CleanEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.checkToastInTest;
import static com.didekindroid.usuario.dominio.DomainDataUtils.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuario.webservices.ServiceOne.ServOne;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 26/10/15
 * Time: 13:48
 */
@RunWith(AndroidJUnit4.class)
public class LoginAcTest_2 extends LoginAcTest {

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(5000);
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
        Thread.sleep(5000);
        checkToastInTest(R.string.login_wrong_no_mail, mActivity);
    }
}
