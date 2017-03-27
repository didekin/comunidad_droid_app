package com.didekindroid.usuario.login;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.ExtendableTestAc;
import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.usuario.testutil.UserEspressoTestUtil;
import com.didekinlib.model.usuario.Usuario;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.R.id.comu_search_ac_linearlayout;
import static com.didekindroid.R.id.login_ac_button;
import static com.didekindroid.R.id.reg_usuario_email_editT;
import static com.didekindroid.R.id.reg_usuario_password_ediT;
import static com.didekindroid.testutil.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.testutil.ActivityTestUtils.isActivityDying;
import static com.didekindroid.usuario.login.ViewerLogin.newViewerLogin;
import static com.didekindroid.usuario.testutil.UserEspressoTestUtil.typeLoginData;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_DROID;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_DROID;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_DROID;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 26/10/15
 * Time: 13:55
 */
@RunWith(AndroidJUnit4.class)
public class LoginAcTest implements ExtendableTestAc {

    LoginAc activity;
    int activityLayoutId = R.id.login_ac_layout;
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
                e.printStackTrace();
            }
        }
    };

    ViewerLoginIf viewerLogin;

    @BeforeClass
    public static void relax() throws InterruptedException
    {
        MILLISECONDS.sleep(2000);
    }

    @Before
    public void setUp() throws Exception
    {
        activity = (LoginAc) mActivityRule.getActivity();
        viewerLogin = newViewerLogin(activity);
    }

    @After
    public void cleanUp() throws UiException
    {
        cleanOptions(CLEAN_DROID);
    }

    @Override
    public void checkNavigateUp()
    {
        throw new UnsupportedOperationException("NO NAVIGATE-UP in LoginAc manager");
    }

    @Override
    public int getNextViewResourceId()
    {
        return comu_search_ac_linearlayout;
    }

    //    ==================================  TESTS INTEGRATIOIN  ==================================

    @Test
    public final void testOnCreate() throws Exception
    {
        onView(withId(reg_usuario_email_editT)).check(matches(isDisplayed()));
        onView(withId(reg_usuario_password_ediT)).check(matches(isDisplayed()));
        onView(withId(login_ac_button)).check(matches(isDisplayed()));

        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        onView(withContentDescription(R.string.navigate_up_txt)).check(matches(isDisplayed()));
    }

    @Test
    public final void testOnStop() throws Exception
    {
        InstrumentationRegistry.getInstrumentation().callActivityOnStop(activity);
        // Check.
        assertThat(viewerLogin.getController().getSubscriptions().size(), is(0));
    }

    @Test
    public void testReplaceView()
    {
//        checkViewerReplaceView(activity, getNextViewResourceId());   TODO: cambiar.
    }

    @Test   // Login OK.
    public void testValidateLoginRemote_1()
    {
        typeLoginData(USER_DROID.getUserName(), USER_DROID.getPassword());
        onView(withId(login_ac_button)).check(matches(isDisplayed())).perform(click());

        await().atMost(3, SECONDS).until(isActivityDying(activity), is(true));
        onView(withId(getNextViewResourceId())).check(matches(isDisplayed()));
    }

    @Test   // Login NOT OK, counterWrong > 3.
    public void testValidateLoginRemote_2()
    {
        viewerLogin.getCounterWrong().set(3);
        typeLoginData(USER_DROID.getUserName(), "password_wrong");
        onView(withId(login_ac_button)).check(matches(isDisplayed())).perform(click());

        await().atMost(2, SECONDS).untilAtomic(viewerLogin.getCounterWrong(), equalTo(4));
        UserEspressoTestUtil.checkPswdSendByMailDialog();
    }

    @Test   // Login NOT OK, counterWrong <= 3.
    public void testValidateLoginRemote_3() throws InterruptedException
    {
        viewerLogin.getCounterWrong().set(2);
        typeLoginData(USER_DROID.getUserName(), "password_wrong");
        onView(withId(login_ac_button)).check(matches(isDisplayed())).perform(click(), closeSoftKeyboard());

        onView(withId(activityLayoutId)).check(matches(isDisplayed()));
        checkToastInTest(R.string.password_wrong, activity);
    }
}
