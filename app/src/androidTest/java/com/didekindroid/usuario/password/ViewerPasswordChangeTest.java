package com.didekindroid.usuario.password;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekinlib.http.ErrorBean;
import com.didekinlib.model.usuario.Usuario;

import org.junit.After;
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
import static com.didekindroid.testutil.ActivityTestUtils.isToastInView;
import static com.didekindroid.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.usuario.password.ViewerPasswordChange.newViewerPswdChange;
import static com.didekindroid.usuario.testutil.UserEspressoTestUtil.typePswdData;
import static com.didekindroid.usuario.testutil.UserNavigationTestConstant.nextPswdChangeAcRsId;
import static com.didekindroid.usuario.testutil.UserNavigationTestConstant.pswdChangeAcRsId;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOneUser;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekinlib.http.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;
import static com.didekinlib.model.usuario.UsuarioExceptionMsg.USER_NAME_NOT_FOUND;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 25/03/17
 * Time: 13:09
 */
public class ViewerPasswordChangeTest {

    @Rule
    public ActivityTestRule<? extends Activity> mActivityRule = new ActivityTestRule<PasswordChangeAc>(PasswordChangeAc.class) {

        @Override
        protected Intent getActivityIntent()
        {
            Usuario usuario = null;
            try {
                usuario = signUpAndUpdateTk(COMU_TRAV_PLAZUELA_PEPE);
            } catch (Exception e) {
                fail();
            }
            return new Intent().putExtra(user_name.key, usuario.getUserName());
        }
    };

    PasswordChangeAc activity;
    ViewerPasswordChange viewer;

    @Before
    public void setUp()
    {
        activity = (PasswordChangeAc) mActivityRule.getActivity();
        viewer = (ViewerPasswordChange) newViewerPswdChange(activity);
    }

    @After
    public void clearUp() throws UiException
    {
        cleanOneUser(USER_PEPE);
    }

    //    ============================  TESTS  ===================================

    @Test
    public void testNewViewerPswdChange() throws Exception
    {
        assertThat(viewer.getController(), instanceOf(CtrlerPasswordChange.class));
        assertThat(viewer.userName, is(activity.getIntent().getStringExtra(user_name.key)));
        assertThat(viewer.usuarioBean, notNullValue());
    }

    @Test
    public void testCheckLoginData_1() throws Exception
    {
        // Caso WRONG: We test the change to false.
        typePswdData("password1", "password2");

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer.checkLoginData();
            }
        });
        waitAtMost(3L, SECONDS).until(isToastInView(R.string.error_validation_msg, activity, R.string.password_different));
    }

    @Test
    public void testCheckLoginData_2() throws UiException
    {
        // Caso OK: We test the change to true.
        final AtomicBoolean isPswdDataOk = new AtomicBoolean(false);
        typePswdData("password1", "password1");

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                assertThat(isPswdDataOk.getAndSet(viewer.checkLoginData()), is(false));
            }
        });
        await().atMost(1, SECONDS).untilTrue(isPswdDataOk);
    }

    @Test
    public void testGetPswdDataFromView() throws Exception
    {
        typePswdData("new_password", "confirmation");
        assertThat(viewer.getPswdDataFromView()[0], is("new_password"));
        assertThat(viewer.getPswdDataFromView()[1], is("confirmation"));
    }

    @Test
    public void testProcessControllerError_1() throws Exception
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer.onErrorInObserver(new UiException(new ErrorBean(USER_NAME_NOT_FOUND)));
            }
        });
        waitAtMost(3, SECONDS).until(isToastInView(R.string.username_wrong_in_login, activity));
        onView(withId(pswdChangeAcRsId)).check(matches(isDisplayed()));
    }

    @Test
    public void testProcessControllerError_2()
    {
        assertThat(checkProcessCtrlError(viewer, GENERIC_INTERNAL_ERROR, GENERIC_APP_ACC), is(true));
    }

    @Test
    public void testReplaceRootView() throws Exception
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer.replaceComponent(new Bundle(0));
            }
        });
        waitAtMost(3, SECONDS).until(isToastInView(R.string.password_remote_change, activity));
        onView(withId(nextPswdChangeAcRsId)).check(matches(isDisplayed()));
    }
}