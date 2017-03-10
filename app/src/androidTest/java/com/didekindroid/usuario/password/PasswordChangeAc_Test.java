package com.didekindroid.usuario.password;

import android.app.Activity;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.ExtendableTestAc;
import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.testutil.ActivityTestUtils;
import com.didekinlib.model.usuario.Usuario;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.R.id.user_data_ac_layout;
import static com.didekindroid.exception.UiExceptionRouter.GENERIC_APP_ACC;
import static com.didekindroid.testutil.ActivityTestUtils.checkProcessCtrlError;
import static com.didekindroid.testutil.ActivityTestUtils.checkProcessCtrlErrorOnlyToast;
import static com.didekindroid.testutil.ActivityTestUtils.checkViewerReplaceView;
import static com.didekindroid.testutil.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.clickNavigateUp;
import static com.didekindroid.testutil.ActivityTestUtils.isToastInView;
import static com.didekindroid.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.usuario.testutil.UserEspressoTestUtil.typePswdData;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOneUser;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekinlib.http.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;
import static com.didekinlib.model.usuario.UsuarioExceptionMsg.USER_NAME_NOT_FOUND;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Awaitility.fieldIn;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 25/09/15
 * Time: 17:45
 */
@RunWith(AndroidJUnit4.class)
public class PasswordChangeAc_Test implements ExtendableTestAc {

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

    String[] textFromView;
    AtomicBoolean isPswdDataOk;
    AtomicBoolean isExceptionThrown;
    PasswordChangeAc activity;
    ControllerPasswordChangeIf controller;
    private int activityLayoutId = R.id.password_change_ac_layout;

    @BeforeClass
    public static void relax() throws InterruptedException
    {
        TimeUnit.MILLISECONDS.sleep(2000);
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
        fail("NO NAVIGATE-UP in PasswordChange manager");
    }

    @Override
    public int getNextViewResourceId()
    {
        return user_data_ac_layout;
    }

    //    ================================= INTEGRATION TESTS  =====================================

    @Test
    public void testOnCreate() throws Exception
    {
        onView(withId(activityLayoutId)).check(matches(isDisplayed()));

        onView(withId(R.id.reg_usuario_password_ediT)).check(matches(withText(containsString(""))))
                .check(matches(withHint(R.string.usuario_password_hint)))
                .check(matches(isDisplayed()));
        onView(withId(R.id.reg_usuario_password_confirm_ediT)).check(matches(withText(containsString(""))))
                .check(matches(withHint(R.string.usuario_password_confirm_hint)))
                .check(matches(isDisplayed()));
        onView(withId(R.id.password_change_ac_button)).check(matches(withText(R.string.password_change_ac_button_txt)))
                .check(matches(isDisplayed()));

        onView(withId(R.id.appbar)).check(matches(isDisplayed()));
        clickNavigateUp();

        cleanOneUser(USER_PEPE);
    }

    @Test
    public void testPasswordChange_OK() throws UiException, InterruptedException
    {
        String refreshToken1 = controller.getIdentityCacher().getRefreshTokenValue();

        typePswdData("new_pepe_password", "new_pepe_password");
        onView(withId(R.id.password_change_ac_button)).check(matches(isDisplayed())).perform(click());

        waitAtMost(3L, SECONDS).until(ActivityTestUtils.getRefreshTokenValue(controller.getIdentityCacher()), not(equalTo(refreshToken1)));
        checkToastInTest(R.string.password_remote_change, activity);
        onView(withId(getNextViewResourceId())).check(matches(isDisplayed()));

        checkUp(activityLayoutId);

        usuarioDao.deleteUser();
    }

    @Test
    public final void testOnStop() throws Exception
    {
        InstrumentationRegistry.getInstrumentation().callActivityOnStop(activity);
        // Check.
        assertThat(controller.getSubscriptions().size(), is(0));
    }

    //  ====================================== VIEWER TESTS  =======================================

    @Test
    public void testProcessControllerError_1()
    {
        assertThat(checkProcessCtrlError(activity, GENERIC_INTERNAL_ERROR, GENERIC_APP_ACC), is(true));
    }

    @Test
    public void testProcessControllerError_2() throws Exception
    {
        int activityLayoutId = R.id.password_change_ac_layout;
        checkProcessCtrlErrorOnlyToast(activity, USER_NAME_NOT_FOUND, R.string.username_wrong_in_login, activityLayoutId);
    }

    @Test
    public void testReplaceView()
    {
        checkViewerReplaceView(activity, getNextViewResourceId());
        checkToastInTest(R.string.password_remote_change, activity);
    }

    //  ================================== VIEWER PASSWORD TESTS  ==================================

    @Test
    public void testGetPswdDataFromView() throws Exception
    {
        typePswdData("new_password", "confirmation");
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
        typePswdData("password1", "password2");

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                activity.checkLoginData();
            }
        });
        waitAtMost(3L, SECONDS).until(isToastInView(R.string.error_validation_msg, activity, R.string.password_different));
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
        typePswdData("password1", "password1");

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