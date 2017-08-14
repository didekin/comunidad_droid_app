package com.didekindroid.usuario.password;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.usuario.login.CtrlerUsuario;
import com.didekinlib.http.ErrorBean;
import com.didekinlib.http.oauth2.SpringOauthToken;
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
import static com.didekindroid.security.TokenIdentityCacher.TKhandler;
import static com.didekindroid.testutil.ActivityTestUtils.checkOnErrorInObserver;
import static com.didekindroid.testutil.ActivityTestUtils.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtils.isToastInView;
import static com.didekindroid.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.usuario.password.ViewerPasswordChange.newViewerPswdChange;
import static com.didekindroid.usuario.testutil.UserEspressoTestUtil.typePswdData;
import static com.didekindroid.usuario.testutil.UserEspressoTestUtil.typePswdDataWithPswdValidation;
import static com.didekindroid.usuario.testutil.UserNavigationTestConstant.pswdChangeAcRsId;
import static com.didekindroid.usuario.testutil.UserNavigationTestConstant.userDataAcRsId;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOneUser;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekinlib.http.GenericExceptionMsg.BAD_REQUEST;
import static com.didekinlib.http.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;
import static com.didekinlib.model.usuario.UsuarioExceptionMsg.USER_NAME_NOT_FOUND;
import static io.reactivex.Single.just;
import static java.util.concurrent.TimeUnit.SECONDS;
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
    SpringOauthToken oldToken;

    @Before
    public void setUp()
    {
        activity = (PasswordChangeAc) mActivityRule.getActivity();
        viewer = newViewerPswdChange(activity);
        oldToken = TKhandler.getTokenCache().get();
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
        assertThat(viewer.getController(), instanceOf(CtrlerUsuario.class));
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
        waitAtMost(4L, SECONDS).until(isToastInView(R.string.error_validation_msg, activity, R.string.password_different));
    }

    @Test
    public void testCheckLoginData_2() throws Exception
    {
        // Caso WRONG: wrong format for current password.
        typePswdDataWithPswdValidation("password1", "password1", "wrong+password");

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer.checkLoginData();
            }
        });
        waitAtMost(4L, SECONDS).until(isToastInView(R.string.password_wrong, activity));
    }

    @Test
    public void testCheckLoginData_3() throws UiException
    {
        // Caso OK: We test the change to true.
        final AtomicBoolean isPswdDataOk = new AtomicBoolean(false);
        typePswdDataWithPswdValidation("password1", "password1", USER_PEPE.getPassword());

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                assertThat(isPswdDataOk.getAndSet(viewer.checkLoginData()), is(false));
            }
        });
        waitAtMost(2, SECONDS).untilTrue(isPswdDataOk);
    }

    @Test
    public void testGetPswdDataFromView() throws Exception
    {
        typePswdDataWithPswdValidation("new_password", "confirmation", "currentPassword");
        assertThat(viewer.getPswdDataFromView()[0], is("new_password"));
        assertThat(viewer.getPswdDataFromView()[1], is("confirmation"));
        assertThat(viewer.getPswdDataFromView()[2], is("currentPassword"));
    }

    @Test
    public void testOnErrorInObserver_1() throws Exception
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
    public void testOnErrorInObserver_2() throws Exception
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer.onErrorInObserver(new UiException(new ErrorBean(BAD_REQUEST)));
            }
        });
        waitAtMost(4, SECONDS).until(isToastInView(R.string.password_wrong, activity));
        onView(withId(pswdChangeAcRsId)).check(matches(isDisplayed()));
    }

    @Test
    public void testOnErrorInObserver_3()
    {
        assertThat(checkOnErrorInObserver(viewer, GENERIC_INTERNAL_ERROR, GENERIC_APP_ACC), is(true));
    }

    @Test
    public void testReplaceComponent() throws Exception
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer.replaceComponent(new Bundle(0));
            }
        });
        waitAtMost(3, SECONDS).until(isToastInView(R.string.password_remote_change, activity));
        onView(withId(userDataAcRsId)).check(matches(isDisplayed()));
    }

    //    ============================  TESTS OBSERVERS  ===================================

    @Test
    public void test_PswdChangeSingleObserver_Succcess()
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                just(true).subscribeWith(viewer.new PswdSendSingleObserver());
            }
        });
        waitAtMost(2, SECONDS).until(isToastInView(R.string.password_new_in_login, activity));
        waitAtMost(2, SECONDS).until(isResourceIdDisplayed(userDataAcRsId));
    }
}