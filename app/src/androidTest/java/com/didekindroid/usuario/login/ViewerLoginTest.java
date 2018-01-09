package com.didekindroid.usuario.login;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.usuario.UsuarioBean;
import com.didekindroid.usuario.dao.CtrlerUsuario;
import com.didekinlib.http.ErrorBean;
import com.didekinlib.model.usuario.Usuario;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableSingleObserver;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.R.string.send_password_by_mail_YES;
import static com.didekindroid.R.string.send_password_by_mail_dialog;
import static com.didekindroid.testutil.ActivityTestUtils.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtils.isToastInView;
import static com.didekindroid.testutil.ActivityTestUtils.isViewDisplayedAndPerform;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.usuario.UsuarioBundleKey.login_counter_atomic_int;
import static com.didekindroid.usuario.testutil.UserEspressoTestUtil.checkTextsInDialog;
import static com.didekindroid.usuario.testutil.UserEspressoTestUtil.typeLoginData;
import static com.didekindroid.usuario.testutil.UserNavigationTestConstant.loginAcResourceId;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_DROID;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_DROID;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_REAL_DROID;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static com.didekinlib.model.usuario.UsuarioExceptionMsg.USER_NAME_NOT_FOUND;
import static java.util.concurrent.TimeUnit.SECONDS;
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
@RunWith(AndroidJUnit4.class)
public class ViewerLoginTest {

    static final AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public IntentsTestRule<? extends Activity> activityRule = new IntentsTestRule<>(LoginAc.class, true, true);

    LoginAc activity;

    @Before
    public void setUp()
    {
        activity = (LoginAc) activityRule.getActivity();
    }

    @Test
    public void testNewViewerLogin() throws Exception
    {
        assertThat(activity.viewerLogin, notNullValue());
        assertThat(activity.viewerLogin.getController(), instanceOf(CtrlerUsuario.class));
    }

    @Test
    public void testDoViewInViewer_1() throws Exception
    {
        Bundle bundle = new Bundle(1);
        bundle.putInt(login_counter_atomic_int.key, 2);
        // Precondition.
        assertThat(activity.viewerLogin.getCounterWrong().get(), is(0));
        // Execute.
        activity.viewerLogin.doViewInViewer(bundle, null);
        // Check.
        assertThat(activity.viewerLogin.getCounterWrong().get(), is(2));
    }

    @Test
    public void testDoViewInViewer_2() throws Exception
    {
        // Execute.
        activity.runOnUiThread(() -> {
            activity.viewerLogin.doViewInViewer(new Bundle(0), USER_DROID.getUserName());
            // Check.
            assertThat(((EditText) activity.viewerLogin.getViewInViewer().findViewById(R.id.reg_usuario_email_editT))
                    .getText().toString(), is(USER_DROID.getUserName()));
        });
    }

    @Test  // Validation: error message.
    public void testCheckLoginData_1() throws Exception
    {
        typeLoginData("user_wrong", "psw");
        activity.runOnUiThread(() -> activity.viewerLogin.checkLoginData());
        waitAtMost(4, SECONDS).until(isToastInView(R.string.error_validation_msg, activity,
                R.string.email_hint, R.string.password));
    }

    @Test   // Validation OK
    public void testCheckLoginData_2() throws InterruptedException
    {
        typeLoginData(USER_DROID.getUserName(), USER_DROID.getPassword());
        assertThat(activity.viewerLogin.checkLoginData(), is(true));
    }

    @Test
    public void testGetLoginDataFromView()
    {
        typeLoginData(USER_DROID.getUserName(), USER_DROID.getPassword());
        assertThat(activity.viewerLogin.getLoginDataFromView()[0], is(USER_DROID.getUserName()));
        assertThat(activity.viewerLogin.getLoginDataFromView()[1], is(USER_DROID.getPassword()));
    }

    @Test
    public void test_ShowDialogAfterErrors() throws Exception
    {
        // Precondition.
        activity.viewerLogin.usuarioBean.compareAndSet(null, new UsuarioBean("userName", null, "password_ok", null));
        // Exec.
        activity.viewerLogin.showDialogAfterErrors();
        // Check.
        checkTextsInDialog(send_password_by_mail_dialog, send_password_by_mail_YES);
    }

    @Test   // Login NO ok, counterWrong > 3.
    public void testProcessLoginBackInView_1()
    {
        // Precondition.
        activity.viewerLogin.getCounterWrong().set(3);
        activity.viewerLogin.usuarioBean.compareAndSet(null, new UsuarioBean("mail_wrong", null, "password_wrong", null));
        // Exec.
        activity.runOnUiThread(() -> activity.viewerLogin.processLoginBackInView(false));
        // Check.
        waitAtMost(3, SECONDS).untilAtomic(activity.viewerLogin.getCounterWrong(), equalTo(4));
        checkTextsInDialog(send_password_by_mail_dialog, send_password_by_mail_YES);
    }

    @Test   // Login NO ok, counterWrong <= 3.
    public void testProcessLoginBackInView_2() throws InterruptedException
    {
        activity.viewerLogin.getCounterWrong().set(2);

        activity.runOnUiThread(() -> activity.viewerLogin.processLoginBackInView(false));

        waitAtMost(5, SECONDS).untilAtomic(activity.viewerLogin.getCounterWrong(), equalTo(3));
        waitAtMost(5, SECONDS).until(isToastInView(R.string.password_wrong, activity));
        onView(withId(loginAcResourceId)).check(matches(isDisplayed()));
    }

    @Test
    public void testDoDialogPositiveClick_1() throws Exception
    {
        activity.viewerLogin.setController(new CtrlerUsuario() {
            @Override
            public boolean sendNewPassword(DisposableSingleObserver<Boolean> observer, @NonNull Usuario usuario)
            {
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
                return false;
            }
        });
        activity.viewerLogin.doDialogPositiveClick(USER_DROID);
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }

    @Test
    public void testDoDialogPositiveClick_2() throws Exception
    {
        activity.runOnUiThread(() -> activity.viewerLogin.doDialogPositiveClick(null));
        waitAtMost(4, SECONDS).until(isToastInView(R.string.username_wrong_in_login, activity));
    }

    @Test
    public void testProcessBackSendPswdInView() throws Exception
    {
        activity.runOnUiThread(() -> activity.viewerLogin.processBackSendPswdInView(true));
        waitAtMost(4, SECONDS).until(isToastInView(R.string.password_new_in_login, activity));
    }

    @Test
    public void test_initAcWithBundle() throws Exception
    {
        // Preconditions: usuario registrado.
        signUpAndUpdateTk(COMU_REAL_DROID);
        // Exec.
        activity.viewerLogin.initAcFromActivity(null);
        // Check.
        waitAtMost(6, SECONDS).until(isResourceIdDisplayed(seeUserComuByUserFrRsId));
        assertThat(activity.isFinishing() || activity.isDestroyed(), is(true));
        // Clean.
        cleanOptions(CLEAN_DROID);
    }

    // =========================  LyfeCicle  =========================

    @Test
    public void testSaveState() throws Exception
    {
        // Previous state.
        Bundle bundle = new Bundle(1);
        bundle.putInt(login_counter_atomic_int.key, 2);
        activity.viewerLogin.doViewInViewer(bundle, null);
        assertThat(activity.viewerLogin.getCounterWrong().get(), is(2));
        // Execute and checkAppBarMenu.
        activity.viewerLogin.saveState(bundle);
        assertThat(bundle.getInt(login_counter_atomic_int.key), is(2));
    }

    // =========================  Help button  =========================

    @Test
    public void test_HelpButton_1() throws Exception
    {
        // Precondition.
        activity.viewerLogin.usuarioBean.compareAndSet(
                null,
                new UsuarioBean("didekindroid@didekin.es", null, null, null));
        waitAtMost(4, SECONDS).until(isViewDisplayedAndPerform(withId(R.id.login_help_fab), click()));
        // Check.
        checkTextsInDialog(R.string.send_password_by_mail_dialog, R.string.send_password_by_mail_YES);
    }

    @Test
    public void test_HelpButton_2() throws Exception
    {
        // Precondition.
        activity.viewerLogin.usuarioBean.set(null);
        waitAtMost(4, SECONDS).until(isViewDisplayedAndPerform(withId(R.id.login_help_fab), click()));
        // Check.
        checkTextsInDialog(R.string.send_password_by_mail_dialog, R.string.send_password_by_mail_YES);
    }

    // ============================================================
    // ....................... SUBSCRIBERS ...................
    // ============================================================

    @Test
    public void testOnErrorInObserver_1() throws Exception
    {
        activity.runOnUiThread(() -> activity.viewerLogin.onErrorInObserver(new UiException(new ErrorBean(USER_NAME_NOT_FOUND))));
        waitAtMost(3, SECONDS).until(isToastInView(R.string.username_wrong_in_login, activity));
        onView(withId(loginAcResourceId)).check(matches(isDisplayed()));
    }
}
