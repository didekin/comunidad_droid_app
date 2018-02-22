package com.didekindroid.usuario;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.model.usuario.Usuario;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.v4.app.TaskStackBuilder.create;
import static com.didekindroid.R.id.login_ac_button;
import static com.didekindroid.R.id.reg_usuario_email_editT;
import static com.didekindroid.R.id.reg_usuario_password_ediT;
import static com.didekindroid.R.string.send_password_by_mail_YES;
import static com.didekindroid.R.string.send_password_by_mail_dialog;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchAcLayout;
import static com.didekindroid.testutil.ActivityTestUtil.checkUp;
import static com.didekindroid.testutil.ActivityTestUtil.cleanTasks;
import static com.didekindroid.testutil.ActivityTestUtil.isActivityDying;
import static com.didekindroid.testutil.ActivityTestUtil.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtil.isToastInView;
import static com.didekindroid.lib_one.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.lib_one.usuario.testutil.UserEspressoTestUtil.checkTextsInDialog;
import static com.didekindroid.lib_one.usuario.testutil.UserEspressoTestUtil.typeLoginData;
import static com.didekindroid.usuario.UserTestNavigation.loginAcResourceId;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_DROID;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_DROID;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_REAL_DROID;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpAndUpdateTk;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 26/10/15
 * Time: 13:55
 */
@RunWith(AndroidJUnit4.class)
public class LoginAcTest {

    LoginAc activity;
    Usuario registeredUser;

    @Rule
    public ActivityTestRule<? extends Activity> mActivityRule = new ActivityTestRule<LoginAc>(LoginAc.class) {
        @Override
        protected void beforeActivityLaunched()
        {
            // Precondition: the user is registered.
            try {
                registeredUser = signUpAndUpdateTk(COMU_REAL_DROID);
            } catch (Exception e) {
                fail();
            }
            if (Build.VERSION.SDK_INT >= LOLLIPOP) {
                create(getTargetContext())
                        .addParentStack(LoginAc.class)
                        .startActivities();
            }
        }

        @Override
        protected Intent getActivityIntent()
        {
            Intent intent = new Intent();
            intent.putExtra(user_name.key, USER_DROID.getUserName());
            return intent;
        }
    };

    @Before
    public void setUp() throws Exception
    {
        activity = (LoginAc) mActivityRule.getActivity();
        assertThat(activity.getIntent().hasExtra(user_name.key), is(true));
    }

    @After
    public void cleanUp() throws UiException
    {
        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            cleanTasks(activity);
        }
        cleanOptions(CLEAN_DROID);
    }

    //    ==================================  TESTS INTEGRATIOIN  ==================================

    @Test
    public final void testOnCreate() throws Exception
    {
        onView(allOf(
                withId(reg_usuario_email_editT),
                withText(USER_DROID.getUserName())
        )).check(matches(isDisplayed()));
        onView(withId(reg_usuario_password_ediT)).check(matches(isDisplayed()));
        onView(withId(login_ac_button)).check(matches(isDisplayed()));

        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        onView(withContentDescription(R.string.navigate_up_txt)).check(matches(isDisplayed()));

        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            checkUp(comuSearchAcLayout);
        }
    }

    @Test
    public final void testOnStop() throws Exception
    {
        activity.runOnUiThread(() -> getInstrumentation().callActivityOnStop(activity));
        // Check.
        assertThat(activity.viewerLogin.getController().getSubscriptions().size(), is(0));
    }

    @Test   // Login OK.
    public void testValidateLoginRemote_1()
    {
        typeLoginData(USER_DROID.getUserName(), USER_DROID.getPassword());
        // Exec.
        onView(withId(login_ac_button)).check(matches(isDisplayed())).perform(click());
        // Check.
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(seeUserComuByUserFrRsId));
        waitAtMost(2, SECONDS).until(isActivityDying(activity), is(true));
    }

    @Test   // Login NOT OK, counterWrong > 3.
    public void testValidateLoginRemote_2()
    {
        activity.viewerLogin.getCounterWrong().set(3);
        typeLoginData(USER_DROID.getUserName(), "password_wrong");
        onView(withId(login_ac_button)).check(matches(isDisplayed())).perform(click());

        waitAtMost(2, SECONDS).untilAtomic(activity.viewerLogin.getCounterWrong(), equalTo(4));
        checkTextsInDialog(send_password_by_mail_dialog, send_password_by_mail_YES);
    }

    @Test   // Login NOT OK, counterWrong <= 3.
    public void testValidateLoginRemote_3() throws InterruptedException
    {
        SECONDS.sleep(2);

        activity.viewerLogin.getCounterWrong().set(2);
        typeLoginData(USER_DROID.getUserName(), "password_wrong");
        onView(withId(login_ac_button)).check(matches(isDisplayed())).perform(click(), closeSoftKeyboard());

        onView(withId(loginAcResourceId)).check(matches(isDisplayed()));
        waitAtMost(3, SECONDS).until(isToastInView(R.string.password_wrong, activity));
    }
}
