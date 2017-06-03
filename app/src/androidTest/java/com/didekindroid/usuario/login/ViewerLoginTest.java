package com.didekindroid.usuario.login;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.usuario.UsuarioBean;
import com.didekinlib.http.ErrorBean;
import com.didekinlib.model.usuario.Usuario;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableSingleObserver;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.exception.UiExceptionRouter.GENERIC_APP_ACC;
import static com.didekindroid.testutil.ActivityTestUtils.checkProcessCtrlError;
import static com.didekindroid.testutil.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.testutil.ActivityTestUtils.isActivityDying;
import static com.didekindroid.testutil.ActivityTestUtils.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtils.isToastInView;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.usuario.UsuarioBundleKey.login_counter_atomic_int;
import static com.didekindroid.usuario.login.ViewerLogin.newViewerLogin;
import static com.didekindroid.usuario.testutil.UserEspressoTestUtil.checkPswdSendByMailDialog;
import static com.didekindroid.usuario.testutil.UserEspressoTestUtil.typeLoginData;
import static com.didekindroid.usuario.testutil.UserNavigationTestConstant.loginAcResourceId;
import static com.didekindroid.usuario.testutil.UserNavigationTestConstant.nextLoginAcRsId;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_DROID;
import static com.didekinlib.http.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;
import static com.didekinlib.model.usuario.UsuarioExceptionMsg.USER_NAME_NOT_FOUND;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 24/03/17
 * Time: 19:15
 */

public class ViewerLoginTest {

    static final AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public ActivityTestRule<? extends Activity> activityRule = new ActivityTestRule<>(LoginAc.class, true, true);

    LoginAc activity;
    ViewerLogin viewerLogin;

    @Before
    public void setUp()
    {
        activity = (LoginAc) activityRule.getActivity();
        viewerLogin = (ViewerLogin) newViewerLogin(activity);
    }

    @Test
    public void testNewViewerLogin() throws Exception
    {
        assertThat(viewerLogin, notNullValue());
        assertThat(viewerLogin.getController(), instanceOf(CtrlerLogin.class));
    }

    @Test
    public void testDoViewInViewer() throws Exception
    {
        Bundle bundle = new Bundle(1);
        bundle.putInt(login_counter_atomic_int.key, 2);
        // Precondition.
        assertThat(viewerLogin.getCounterWrong().get(), is(0));
        // Execute.
        viewerLogin.doViewInViewer(bundle, null);
        // Check.
        assertThat(viewerLogin.getCounterWrong().get(), is(2));
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
        waitAtMost(4, SECONDS).until(isToastInView(R.string.error_validation_msg, activity,
                R.string.email_hint, R.string.password));
    }

    @Test   // Validation OK
    public void testCheckLoginData_2() throws InterruptedException
    {
        typeLoginData(USER_DROID.getUserName(), USER_DROID.getPassword());
        assertThat(viewerLogin.checkLoginData(), is(true));
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
        viewerLogin.usuarioBean.compareAndSet(null, new UsuarioBean("mail_wrong", null, "password_wrong", null));

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewerLogin.processLoginBackInView(false);
            }
        });

        await().atMost(3, SECONDS).untilAtomic(viewerLogin.getCounterWrong(), equalTo(4));
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
        onView(withId(loginAcResourceId)).check(matches(isDisplayed()));
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

        await().atMost(3, SECONDS).until(isActivityDying(activity), is(true));
        onView(withId(nextLoginAcRsId)).check(matches(isDisplayed()));
    }

    @Test
    public void testDoDialogPositiveClick() throws Exception
    {
        viewerLogin.setController(new CtrlerLogin() {
            @Override
            public boolean doDialogPositiveClick(DisposableSingleObserver<Boolean> observer, @NonNull Usuario usuario)
            {
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
                return false;
            }
        });
        viewerLogin.doDialogPositiveClick(USER_DROID);
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
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
        await().atMost(3, SECONDS).until(isToastInView(R.string.password_new_in_login, activity));
    }

    @Test
    public void testProcessControllerError_1() throws Exception
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewerLogin.onErrorInObserver(new UiException(new ErrorBean(USER_NAME_NOT_FOUND)));
            }
        });
        waitAtMost(3, SECONDS).until(isToastInView(R.string.username_wrong_in_login, activity));
        onView(withId(loginAcResourceId)).check(matches(isDisplayed()));
    }

    @Test
    public void testProcessControllerError_2()
    {
        assertThat(checkProcessCtrlError(viewerLogin, GENERIC_INTERNAL_ERROR, GENERIC_APP_ACC), is(true));
    }

    @Test
    public void testSaveState() throws Exception
    {
        // Previous state.
        Bundle bundle = new Bundle(1);
        bundle.putInt(login_counter_atomic_int.key, 2);
        viewerLogin.doViewInViewer(bundle, null);
        assertThat(viewerLogin.getCounterWrong().get(), is(2));

        // Execute and check.
        viewerLogin.saveState(bundle);
        assertThat(bundle.getInt(login_counter_atomic_int.key), is(2));
    }

    @Test
    public void testReplaceRootView() throws Exception
    {
        viewerLogin.replaceComponent(new Bundle(0));
        waitAtMost(3, SECONDS).until(isResourceIdDisplayed(nextLoginAcRsId));
        assertThat(activity.isFinishing() || activity.isDestroyed(), is(true));
    }
}
