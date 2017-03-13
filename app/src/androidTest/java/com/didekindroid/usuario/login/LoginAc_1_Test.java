package com.didekindroid.usuario.login;

import android.app.Activity;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.ExtendableTestAc;
import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.security.IdentityCacher;
import com.didekindroid.usuario.UsuarioBean;
import com.didekinlib.model.usuario.Usuario;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicBoolean;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.isDialog;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.R.id.comu_search_ac_linearlayout;
import static com.didekindroid.R.id.login_ac_button;
import static com.didekindroid.R.id.reg_usuario_email_editT;
import static com.didekindroid.R.id.reg_usuario_password_ediT;
import static com.didekindroid.R.string.send_password_by_mail_NO;
import static com.didekindroid.R.string.send_password_by_mail_YES;
import static com.didekindroid.R.string.send_password_by_mail_dialog;
import static com.didekindroid.exception.UiExceptionRouter.GENERIC_APP_ACC;
import static com.didekindroid.security.Oauth2DaoRemote.Oauth2;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ActivityTestUtils.checkProcessCtrlError;
import static com.didekindroid.testutil.ActivityTestUtils.checkProcessCtrlErrorOnlyToast;
import static com.didekindroid.testutil.ActivityTestUtils.checkViewerReplaceView;
import static com.didekindroid.testutil.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.testutil.ActivityTestUtils.isActivityDying;
import static com.didekindroid.testutil.ActivityTestUtils.isToastInView;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_DROID;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_DROID;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_DROID;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekinlib.http.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;
import static com.didekinlib.model.usuario.UsuarioExceptionMsg.USER_NAME_NOT_FOUND;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Awaitility.fieldIn;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 26/10/15
 * Time: 13:55
 */
@RunWith(AndroidJUnit4.class)
public class LoginAc_1_Test implements ExtendableTestAc {

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


    String[] textFromView;
    AtomicBoolean isLoginDataOk;
    AtomicBoolean isToCleanNormal;
    IdentityCacher identityCacher;
    ControllerLoginIf controller;

    @BeforeClass
    public static void relax() throws InterruptedException
    {
        MILLISECONDS.sleep(2000);
    }

    @Before
    public void setUp() throws Exception
    {
        identityCacher = TKhandler;
        isToCleanNormal = new AtomicBoolean(true);
        activity = (LoginAc) mActivityRule.getActivity();
        // Default initialization.
        controller = new ControllerLogin(activity);
    }

    @After
    public void cleanUp() throws UiException
    {
        if (!isToCleanNormal.get()) {
            // Es necesario conseguir un nuevo token: la cache está en blanco.
            TKhandler.initIdentityCache(Oauth2.getPasswordUserToken(USER_DROID.getUserName(), USER_DROID.getPassword()));
            usuarioDao.deleteUser();
            return;
        }
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

    //    =====================================  TESTS  ==========================================

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
        assertThat(controller.getSubscriptions().size(), is(0));
    }

    // ============================================================
    //    ..... VIEWER IMPLEMENTATION ....
    // ============================================================

    @Test
    public void testProcessControllerError_1()
    {
        assertThat(checkProcessCtrlError(activity, GENERIC_INTERNAL_ERROR, GENERIC_APP_ACC), is(true));
    }

    @Test
    public void testProcessControllerError_2() throws InterruptedException
    {
        checkProcessCtrlErrorOnlyToast(activity, USER_NAME_NOT_FOUND, R.string.username_wrong_in_login, activityLayoutId);
    }

    @Test
    public void testReplaceView()
    {
        checkViewerReplaceView(activity, getNextViewResourceId());
    }

    // ============================================================
    //    .............. CONTROLLER (Integration)  ...............
    /* ============================================================*/

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
        activity.counterWrong.set(3);
        typeLoginData(USER_DROID.getUserName(), "password_wrong");
        onView(withId(login_ac_button)).check(matches(isDisplayed())).perform(click());

        await().atMost(2, SECONDS).untilAtomic(activity.counterWrong, equalTo(4));
        checkShowDialog();
    }

    @Test   // Login NOT OK, counterWrong <= 3.
    public void testValidateLoginRemote_3() throws InterruptedException
    {
        activity.counterWrong.set(2);
        typeLoginData(USER_DROID.getUserName(), "password_wrong");
        onView(withId(login_ac_button)).check(matches(isDisplayed())).perform(click(), closeSoftKeyboard());

        onView(withId(activityLayoutId)).check(matches(isDisplayed()));
        checkToastInTest(R.string.password_wrong, activity);
//        waitAtMost(2, SECONDS).until(isToastInView(R.string.password_wrong, activity));
    }

    // ============================================================
    //    ........... VIEWER LOGIN IMPLEMENTATION .........
    // ============================================================

    @Test   // Validation: error message.
    public void testCheckLoginData_1() throws InterruptedException
    {
        typeLoginData("user_wrong", "psw");

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                activity.checkLoginData();
            }
        });
        waitAtMost(1, SECONDS).until(isToastInView(R.string.error_validation_msg, activity,
                R.string.email_hint, R.string.password));
    }

    @Test   // Validation OK
    public void testCheckLoginData_2() throws InterruptedException
    {
        // We test the change to true.
        isLoginDataOk = new AtomicBoolean(false);
        typeLoginData(USER_DROID.getUserName(), USER_DROID.getPassword());

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                assertThat(isLoginDataOk.getAndSet(activity.checkLoginData()), is(false));
            }
        });

        await().atMost(1, SECONDS).untilTrue(isLoginDataOk);
    }

    @Test
    public void testShowDialog()
    {
        // Preconditions.
        typeLoginData(USER_DROID.getUserName(), USER_DROID.getPassword());
        activity.checkLoginData();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                activity.showDialog(USER_DROID.getUserName());
            }
        });
        checkShowDialog();
    }

    @Test
    public void testGetLoginDataFromView()
    {
        typeLoginData(USER_DROID.getUserName(), USER_DROID.getPassword());

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                textFromView = activity.getLoginDataFromView();
            }
        });

        await().atMost(1, SECONDS).until(fieldIn(this).ofType(String[].class).andWithName("textFromView"),
                equalTo(new String[]{USER_DROID.getUserName(), USER_DROID.getPassword()}));
    }

    @Test   // Login NO ok, counterWrong > 3.
    public void testProcessLoginBackInView_1()
    {
        activity.counterWrong.set(3);
        activity.usuarioBean = new UsuarioBean("mail_wrong", null, "password_wrong", null);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                activity.processLoginBackInView(false);
            }
        });

        await().atMost(1, SECONDS).untilAtomic(activity.counterWrong, equalTo(4));
        checkShowDialog();
    }

    @Test   // Login NO ok, counterWrong <= 3.
    public void testProcessLoginBackInView_2() throws InterruptedException
    {
        activity.counterWrong.set(2);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                activity.processLoginBackInView(false);
            }
        });

        await().atMost(3, SECONDS).untilAtomic(activity.counterWrong, equalTo(3));
        checkToastInTest(R.string.password_wrong, activity);
        onView(withId(activityLayoutId)).check(matches(isDisplayed()));
    }

    @Test
    public void testProcessBackSendPswdInView() throws InterruptedException
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                activity.processBackSendPswdInView(true);
            }
        });
        await().atMost(2, SECONDS).until(isToastInView(R.string.password_new_in_login, activity));
    }

    @Test
    public void testDoDialogNegativeClick()
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                activity.doDialogNegativeClick();
            }
        });

        await().atMost(1, SECONDS).until(isActivityDying(activity), is(true));
        onView(withId(getNextViewResourceId())).check(matches(isDisplayed()));
    }

    // ========================== Utility methods ============================

    private void typeLoginData(String userName, String password)
    {
        onView(withId(reg_usuario_email_editT)).perform(typeText(userName));
        onView(withId(reg_usuario_password_ediT)).perform(typeText(password));
    }

    private void checkShowDialog()
    {
        onView(withText(send_password_by_mail_dialog)).inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withText(send_password_by_mail_YES)).inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withText(send_password_by_mail_NO)).inRoot(isDialog())
                .check(matches(isDisplayed()));
    }

    /* ========================== HELPERS ============================*/
}
