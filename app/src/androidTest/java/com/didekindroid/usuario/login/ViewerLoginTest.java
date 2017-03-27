package com.didekindroid.usuario.login;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;

import com.didekindroid.R;
import com.didekindroid.usuario.UsuarioBean;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.exception.UiExceptionRouter.GENERIC_APP_ACC;
import static com.didekindroid.testutil.ActivityTestUtils.checkProcessCtrlError;
import static com.didekindroid.testutil.ActivityTestUtils.checkProcessCtrlErrorOnlyToast;
import static com.didekindroid.testutil.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.testutil.ActivityTestUtils.isActivityDying;
import static com.didekindroid.testutil.ActivityTestUtils.isToastInView;
import static com.didekindroid.usuario.login.ViewerLogin.newViewerLogin;
import static com.didekindroid.usuario.testutil.UserEspressoTestUtil.checkPswdSendByMailDialog;
import static com.didekindroid.usuario.testutil.UserEspressoTestUtil.typeLoginData;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_DROID;
import static com.didekinlib.http.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;
import static com.didekinlib.model.usuario.UsuarioExceptionMsg.USER_NAME_NOT_FOUND;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 24/03/17
 * Time: 19:15
 */

public class ViewerLoginTest {

    @Rule
    public ActivityTestRule<? extends Activity> activityRule = new ActivityTestRule<>(LoginAc.class, true, true);

    LoginAc activity;
    ViewerLogin viewerLogin;
    int activityLayoutId = R.id.login_ac_layout;
    int nextActivityLayoutId = R.id.comu_search_ac_linearlayout;

    @Before
    public void setUp(){
        activity = (LoginAc) activityRule.getActivity();
        viewerLogin = (ViewerLogin) newViewerLogin(activity);
    }

    @Test
    public void testNewViewerLogin() throws Exception
    {

    }

    @Test
    public void testDoViewInViewer() throws Exception
    {

    }

    @Test  // Validation: error message.
    public void testCheckLoginData_1() throws Exception
    {
        typeLoginData("user_wrong", "psw");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewerLogin.checkLoginData();
            }
        });
        waitAtMost(1, SECONDS).until(isToastInView(R.string.error_validation_msg, activity,
                R.string.email_hint, R.string.password));
    }

    @Test   // Validation OK
    public void testCheckLoginData_2() throws InterruptedException
    {
        // We test the change to true.
        final AtomicBoolean isLoginDataOk = new AtomicBoolean(false);
        typeLoginData(USER_DROID.getUserName(), USER_DROID.getPassword());
        assertThat(viewerLogin.checkLoginData(), is(true));

        /*activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                assertThat(isLoginDataOk.getAndSet(viewerLogin.checkLoginData()), is(false));
            }
        });
        await().atMost(1, SECONDS).untilTrue(isLoginDataOk);*/
    }

    @Test
    public void testGetLoginDataFromView()
    {
        typeLoginData(USER_DROID.getUserName(), USER_DROID.getPassword());
        assertThat(viewerLogin.getLoginDataFromView()[0], is(USER_DROID.getUserName()));
        assertThat(viewerLogin.getLoginDataFromView()[1], is(USER_DROID.getPassword()));
    }

    @Test   // Login NO ok, counterWrong > 3.
    public void testProcessLoginBackInView_1()
    {
        viewerLogin.getCounterWrong().set(3);
        viewerLogin.usuarioBean.compareAndSet(null,new UsuarioBean("mail_wrong", null, "password_wrong", null));

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewerLogin.processLoginBackInView(false);
            }
        });

        await().atMost(1, SECONDS).untilAtomic(viewerLogin.getCounterWrong(), equalTo(4));
        checkPswdSendByMailDialog();
    }

    @Test   // Login NO ok, counterWrong <= 3.
    public void testProcessLoginBackInView_2() throws InterruptedException
    {
        viewerLogin.getCounterWrong().set(2);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewerLogin.processLoginBackInView(false);
            }
        });

        await().atMost(3, SECONDS).untilAtomic(viewerLogin.getCounterWrong(), equalTo(3));
        checkToastInTest(R.string.password_wrong, activity);
        onView(withId(activityLayoutId)).check(matches(isDisplayed()));
    }

    @Test
    public void testDoDialogInViewer() throws Exception
    {

    }

    @Test
    public void testDoDialogNegativeClick() throws Exception
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewerLogin.doDialogNegativeClick();
            }
        });

        await().atMost(1, SECONDS).until(isActivityDying(activity), is(true));
        onView(withId(nextActivityLayoutId)).check(matches(isDisplayed()));
    }

    @Test
    public void testDoDialogPositiveClick() throws Exception
    {

    }

    @Test
    public void testProcessBackSendPswdInView() throws Exception
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewerLogin.processBackSendPswdInView(true);
            }
        });
        await().atMost(2, SECONDS).until(isToastInView(R.string.password_new_in_login, activity));
    }

    @Test
    public void testProcessControllerError() throws Exception
    {
        assertThat(checkProcessCtrlError(viewerLogin, GENERIC_INTERNAL_ERROR, GENERIC_APP_ACC), is(true));
        checkProcessCtrlErrorOnlyToast(viewerLogin, USER_NAME_NOT_FOUND, R.string.username_wrong_in_login, activityLayoutId);
    }

    @Test
    public void testSaveState() throws Exception
    {

    }
}
