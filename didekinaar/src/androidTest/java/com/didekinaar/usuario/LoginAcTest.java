package com.didekinaar.usuario;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;

import com.didekinaar.R;
import com.didekinaar.testutil.CleanUserEnum;

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
import static com.didekinaar.testutil.AarActivityTestUtils.checkToastInTest;
import static com.didekinaar.testutil.AarActivityTestUtils.cleanOptions;
import static com.didekinaar.testutil.CleanUserEnum.CLEAN_NOTHING;

/**
 * User: pedro@didekin
 * Date: 26/10/15
 * Time: 13:55
 */
public class LoginAcTest {

    LoginAc mActivity;
    CleanUserEnum whatToClean = CLEAN_NOTHING;
    int activityLayoutId = R.id.login_ac_layout;

    @Rule
    public ActivityTestRule<LoginAc> mActivityRule = new ActivityTestRule<>(LoginAc.class, true, false);

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

    //    ========================== Utility methods ============================

    void typeCheckClickPswdWrong(String userName)
    {
        if (userName != null) {
            onView(ViewMatchers.withId(R.id.reg_usuario_email_editT)).perform(typeText(userName));
        }
        onView(ViewMatchers.withId(R.id.reg_usuario_password_ediT)).perform(typeText("pasword_wrong"));
        onView(ViewMatchers.withId(R.id.login_ac_button)).check(matches(isDisplayed())).perform(click());

        checkToastInTest(R.string.password_wrong_in_login, mActivity);
        onView(withId(activityLayoutId)).check(matches(isDisplayed()));
    }

    void reTypeCheckClickPswdWrong(String userName)
    {
        onView(ViewMatchers.withId(R.id.reg_usuario_email_editT)).perform(replaceText(userName));
        onView(ViewMatchers.withId(R.id.reg_usuario_password_ediT)).perform(replaceText("pasword_wrong"));
        onView(ViewMatchers.withId(R.id.login_ac_button)).check(matches(isDisplayed())).perform(click());

        checkToastInTest(R.string.password_wrong_in_login, mActivity);
        onView(withId(activityLayoutId)).check(matches(isDisplayed()));
    }

    void getDialogFragment(String userName) throws InterruptedException
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
}
