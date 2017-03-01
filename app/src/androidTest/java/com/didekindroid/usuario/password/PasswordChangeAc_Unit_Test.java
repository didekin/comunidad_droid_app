package com.didekindroid.usuario.password;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.ExtendableTestAc;
import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.usuario.testutil.UserEspressoTestUtil;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.didekindroid.R.id.user_data_ac_layout;
import static com.didekindroid.exception.UiExceptionRouter.GENERIC_APP_ACC;
import static com.didekindroid.testutil.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.testutil.ActivityTestUtils.isToastInView;
import static com.didekindroid.testutil.ActivityTestUtils.testClearCtrlSubscriptions;
import static com.didekindroid.testutil.ActivityTestUtils.testProcessCtrlError;
import static com.didekindroid.testutil.ActivityTestUtils.testProcessCtrlErrorOnlyToast;
import static com.didekindroid.testutil.ActivityTestUtils.testReplaceViewStd;
import static com.didekindroid.usuario.UsuarioBundleKey.user_name;
import static com.didekinlib.http.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;
import static com.didekinlib.model.usuario.UsuarioExceptionMsg.USER_NAME_NOT_FOUND;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Awaitility.fieldIn;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 25/09/15
 * Time: 17:45
 */
@RunWith(AndroidJUnit4.class)
public class PasswordChangeAc_Unit_Test implements ExtendableTestAc {

    String[] textFromView;
    PasswordChangeAc activity;
    ControllerPasswordChangeIf controller;

    @Rule
    public ActivityTestRule<? extends Activity> mActivityRule = new ActivityTestRule<>(PasswordChangeAc.class,true);

    @BeforeClass
    public static void relax() throws InterruptedException
    {
        TimeUnit.MILLISECONDS.sleep(2500);
    }

    @Before
    public void setUp() throws Exception
    {
        activity = (PasswordChangeAc) mActivityRule.getActivity();
        controller = new ControllerPasswordChange(activity);
    }

    @Override
    public void checkNavigateUp()
    {
        fail("NO NAVIGATE-UP in PasswordChange activity");
    }

    @Override
    public int getNextViewResourceId()
    {
        return user_data_ac_layout;
    }

    //  ====================================== VIEWER TESTS  =======================================

    @Test
    public void testProcessControllerError_1()
    {
        testProcessCtrlError(activity, GENERIC_INTERNAL_ERROR, GENERIC_APP_ACC);
    }

    @Test
    public void testProcessControllerError_2() throws Exception
    {
        int activityLayoutId = R.id.password_change_ac_layout;
        testProcessCtrlErrorOnlyToast(activity,USER_NAME_NOT_FOUND, R.string.username_wrong_in_login, activityLayoutId);
    }

    @Test
    public void testClearControllerSubscriptions()
    {
        testClearCtrlSubscriptions(controller, activity);
    }

    @Test
    public void testReplaceView()
    {
        testReplaceViewStd(activity, getNextViewResourceId());
        checkToastInTest(R.string.password_remote_change, activity);
    }

    //  ================================== VIEWER PASSWORD TESTS  ==================================

    @Test
    public void testGetPswdDataFromView() throws Exception
    {
        UserEspressoTestUtil.typePswdData("new_password", "confirmation");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                textFromView = activity.getPswdDataFromView();
            }
        });
        await().atMost(1, SECONDS).until(fieldIn(this).ofType(String[].class).andWithName("textFromView"),
                equalTo(new String[]{"new_password", "confirmation"}));
    }

    @Test
    public void testCheckLoginData_1() throws Exception
    {
        // Caso WRONG: We test the change to false.
        UserEspressoTestUtil.typePswdData("password1", "password2");

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                activity.checkLoginData();
            }
        });
        await().atMost(1L, SECONDS).until(isToastInView(R.string.error_validation_msg, activity, R.string.password_different));
        assertThat(activity.usuarioBean.getUserName(), allOf(
                is(activity.userName),
                is(activity.getIntent().getStringExtra(user_name.key))
        ));
    }

    @Test
    public void testCheckLoginData_2() throws UiException
    {
        // Caso OK: We test the change to true.
        final AtomicBoolean isPswdDataOk = new AtomicBoolean(false);
        UserEspressoTestUtil.typePswdData("password1", "password1");

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                assertThat(isPswdDataOk.getAndSet(activity.checkLoginData()), is(false));
            }
        });
        await().atMost(1, SECONDS).untilTrue(isPswdDataOk);
        assertThat(activity.usuarioBean.getUsuario(), notNullValue());
    }
}