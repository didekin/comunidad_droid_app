package com.didekindroid.usuario.activity;

import android.support.test.rule.ActivityTestRule;

import com.didekindroid.R;
import com.didekindroid.usuario.activity.utils.CleanEnum;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.usuario.activity.utils.CleanEnum.CLEAN_NOTHING;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.checkToastInTest;
import static com.didekindroid.usuario.activity.utils.UsuarioTestUtils.cleanOptions;
import static com.didekindroid.usuario.dominio.DomainDataUtils.USER_PEPE;

/**
 * User: pedro@didekin
 * Date: 26/10/15
 * Time: 13:55
 */
public class LoginAcTest {

    LoginAc mActivity;
    CleanEnum whatToClean = CLEAN_NOTHING;

    @Rule
    public ActivityTestRule<LoginAc> mActivityRule = new ActivityTestRule<>(LoginAc.class, true, false);

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

    //    ========================== Utility methods ============================

    void typeCheckClickPswdWrong()
    {
        onView(withId(R.id.reg_usuario_email_editT)).perform(typeText(USER_PEPE.getUserName()));
        onView(withId(R.id.reg_usuario_password_ediT)).perform(typeText("pasword_wrong"));
        onView(withId(R.id.login_ac_button)).check(matches(isDisplayed())).perform(click());

        checkToastInTest(R.string.password_wrong_in_login, mActivity);
        onView(withId(R.id.login_ac_layout)).check(matches(isDisplayed()));
    }

    void reTypeCheckClickPswdWrong()
    {
        onView(withId(R.id.reg_usuario_email_editT)).perform(replaceText(USER_PEPE.getUserName()));
        onView(withId(R.id.reg_usuario_password_ediT)).perform(replaceText("pasword_wrong"));
        onView(withId(R.id.login_ac_button)).check(matches(isDisplayed())).perform(click());

        checkToastInTest(R.string.password_wrong_in_login, mActivity);
        onView(withId(R.id.login_ac_layout)).check(matches(isDisplayed()));
    }

    void getDialogFragment()
    {
        typeCheckClickPswdWrong();
        reTypeCheckClickPswdWrong();
        reTypeCheckClickPswdWrong();

        onView(withId(R.id.reg_usuario_email_editT)).perform(replaceText(USER_PEPE.getUserName()));
        onView(withId(R.id.reg_usuario_password_ediT)).perform(replaceText("pasword_wrong"));
        onView(withId(R.id.login_ac_button)).check(matches(isDisplayed())).perform(click());
    }
}
