package com.didekindroid.usuario.login;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.ExtendableTestAc;
import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.usuario.UsuarioBean;
import com.didekinlib.http.ErrorBean;
import com.didekinlib.model.usuario.Usuario;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import timber.log.Timber;

import static android.support.test.espresso.Espresso.onView;
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
import static com.didekindroid.R.string.username_wrong_in_login;
import static com.didekindroid.security.Oauth2DaoRemote.Oauth2;
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ActivityTestUtils.checkNoToastInTest;
import static com.didekindroid.testutil.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.usuario.login.LoginReactorTest.doLoginMockReactor;
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

    protected LoginAc activity;
    protected int activityLayoutId = R.id.login_ac_layout;
    protected Usuario registeredUser;
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

    @Before
    public void setUp() throws Exception
    {
        MILLISECONDS.sleep(1000);
        isToCleanNormal = new AtomicBoolean(true);
        activity = (LoginAc) mActivityRule.getActivity();
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
        throw new UnsupportedOperationException("NO NAVIGATE-UP in LoginAc activity");
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

    // ============================================================
    //    ..... VIEW IMPLEMENTATION ....
    // ============================================================

    @Test
    public void testShowDialog()
    {
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

    // ============================================================
    //    ..... CONTROLLER IMPLEMENTATION ....
    /* ============================================================*/

    @Test   // Validation: error message.
    public void testCheckLoginData_1() throws InterruptedException
    {
        // We test the change to false.
        isLoginDataOk = new AtomicBoolean(true);

        typeLoginData("user_wrong", "psw");

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                assertThat(isLoginDataOk.getAndSet(activity.checkLoginData()), is(true));
            }
        });

        await().atMost(1, SECONDS).untilFalse(isLoginDataOk);
        checkToastInTest(R.string.error_validation_msg, activity,
                R.string.email_hint, R.string.password);
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

    @Test   // Login OK.
    public void testValidateLoginRemote_1()
    {
        processInUiValidateLogin(USER_DROID.getPassword());
        checkLoginValidateBackOk();
    }

    @Test   // Login NOT OK, counterWrong > 3.
    public void testValidateLoginRemote_2()
    {
        activity.counterWrong.set(3);
        processInUiValidateLogin("password_wrong");
        await().atMost(1, SECONDS).untilAtomic(activity.counterWrong, equalTo(4));
        checkShowDialog();
    }

    @Test   // Login NOT OK, counterWrong <= 3.
    public void testValidateLoginRemote_3()
    {
        activity.counterWrong.set(2);
        processInUiValidateLogin("password_wrong");
        checkLoginValidateBackWrong();
    }

    @Test   // Login OK.
    public void testProcessBackLoginRemote_1()
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                activity.processBackLoginRemote(true);
            }
        });
        checkLoginValidateBackOk();
    }

    @Test   // Login NO ok, counterWrong > 3.
    public void testProcessBackLoginRemote_2()
    {
        activity.counterWrong.set(3);
        activity.usuarioBean = new UsuarioBean("mail_wrong", null, "password_wrong", null);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                activity.processBackLoginRemote(false);
            }
        });

        await().atMost(1, SECONDS).untilAtomic(activity.counterWrong, equalTo(4));
        checkShowDialog();
    }

    @Test   // Login NO ok, counterWrong <= 3.
    public void testProcessBackLoginRemote_3()
    {
        activity.counterWrong = new AtomicInteger(0);
        activity.counterWrong.set(2);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                activity.processBackLoginRemote(false);
            }
        });
        checkLoginValidateBackWrong();
    }

    @Test
    public void testDoDialogPositiveClick() throws UiException
    {
        isToCleanNormal.set(false);
        activity.usuarioBean = new UsuarioBean(USER_DROID.getUserName(), "userdroid", USER_DROID.getPassword(), USER_DROID.getPassword());
        // Necesitamos el usuarion dentro de usuarioBean inicializado.
        activity.usuarioBean.validate(activity.getResources(), null);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                activity.doDialogPositiveClick(doLoginMockReactor(true));
            }
        });

        await().atMost(5, SECONDS).until(isActivityDying(), is(true));
        checkToastInTest(R.string.password_new_in_login, activity);
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

        await().atMost(1, SECONDS).until(isActivityDying(), is(true));
        onView(withId(getNextViewResourceId())).check(matches(isDisplayed()));
    }

    @Test
    public void testProcessBackSendPassword()
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                activity.processBackSendPassword(true);
            }
        });
        await().atMost(1, SECONDS).until(isActivityDying(), is(true));
        checkToastInTest(R.string.password_new_in_login, activity);
    }

    @Test
    public void testProcessBackErrorInReactor_1()
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                activity.processBackErrorInReactor(new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR)));
            }
        });
        await().atMost(1, SECONDS).until(isActivityDying(), is(true));
        onView(withId(getNextViewResourceId())).check(matches(isDisplayed()));
    }

    @Test
    public void testProcessBackErrorInReactor_2()
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                activity.processBackErrorInReactor(new UiException(new ErrorBean(USER_NAME_NOT_FOUND)));
            }
        });
        checkToastInTest(username_wrong_in_login, activity);
    }

    //    ========================== Utility methods ============================

    private void typeLoginData(String userName, String password)
    {
        onView(withId(reg_usuario_email_editT)).perform(typeText(userName));
        onView(withId(reg_usuario_password_ediT)).perform(typeText(password));
    }

    private Callable<Boolean> isActivityDying()
    {
        return new Callable<Boolean>() {
            public Boolean call() throws Exception
            {
                return activity.isFinishing() || activity.isDestroyed();
            }
        };
    }

    private void checkShowDialog()
    {
        Timber.d("checkShowDialogThread: %s", Thread.currentThread().getName());

        onView(withText(send_password_by_mail_dialog)).inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withText(send_password_by_mail_YES)).inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withText(send_password_by_mail_NO)).inRoot(isDialog())
                .check(matches(isDisplayed()));
    }

    private void checkLoginValidateBackOk()
    {
        Timber.d("checkLoginValidateBackOkThread: %s", Thread.currentThread().getName());

        await().atMost(3, SECONDS).until(isActivityDying(), is(true));
        checkNoToastInTest(R.string.user_without_signedUp, activity);
        onView(withId(getNextViewResourceId())).check(matches(isDisplayed()));
    }

    private void checkLoginValidateBackWrong()
    {
        await().atMost(3, SECONDS).untilAtomic(activity.counterWrong, equalTo(3));
        checkToastInTest(R.string.password_wrong, activity);
        onView(withId(activityLayoutId)).check(matches(isDisplayed()));
    }

    private void processInUiValidateLogin(final String password)
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                Timber.d("runOnUiThread: %s", Thread.currentThread().getName());
                activity.usuarioBean = new UsuarioBean(USER_DROID.getUserName(), "userdroid", password, password);
                // Necesitamos el usuarion dentro de usuarioBean inicializado.
                activity.usuarioBean.validate(activity.getResources(), null);
                activity.validateLoginRemote();
            }
        });
    }
}
