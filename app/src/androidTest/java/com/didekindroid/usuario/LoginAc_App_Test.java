package com.didekindroid.usuario;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.usuario.LoginAc;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.support.v4.app.TaskStackBuilder.create;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchAcLayout;
import static com.didekindroid.lib_one.testutil.UiTestUtil.cleanTasks;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_DROID;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_DROID;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.testutil.ActivityTestUtil.checkUp;
import static com.didekindroid.testutil.ActivityTestUtil.isActivityDying;
import static com.didekindroid.testutil.ActivityTestUtil.isResourceIdDisplayed;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_REAL_DROID;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpAndUpdateTk;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 26/10/15
 * Time: 13:55
 */
@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public class LoginAc_App_Test {

    private LoginAc activity;

    @Rule
    public ActivityTestRule<? extends Activity> mActivityRule = new ActivityTestRule<LoginAc>(LoginAc.class) {
        @Override
        protected void beforeActivityLaunched()
        {
            // Precondition: the user is registered.
            try {
                signUpAndUpdateTk(COMU_REAL_DROID);
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
    public final void testOnCreate()
    {
        onView(allOf(
                withId(com.didekindroid.lib_one.R.id.reg_usuario_email_editT),
                withText(USER_DROID.getUserName())
        )).check(matches(isDisplayed()));
        onView(withId(com.didekindroid.lib_one.R.id.reg_usuario_password_ediT)).check(matches(isDisplayed()));

        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            checkUp(comuSearchAcLayout);
        }
    }

    @Test   // Login OK.
    public void testValidateLoginRemote_1()
    {
        onView(withId(com.didekindroid.lib_one.R.id.reg_usuario_email_editT)).perform(replaceText(USER_DROID.getUserName()));
        onView(withId(com.didekindroid.lib_one.R.id.reg_usuario_password_ediT)).perform(typeText(USER_DROID.getPassword()));
        // Exec.
        onView(withId(com.didekindroid.lib_one.R.id.login_ac_button)).check(matches(isDisplayed())).perform(click());
        // Check.
        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(seeUserComuByUserFrRsId));
        waitAtMost(2, SECONDS).until(isActivityDying(activity), is(true));
    }
}
