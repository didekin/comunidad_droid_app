package com.didekindroid.usuario.password;

import android.app.Activity;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.ExtendableTestAc;
import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekinlib.http.ErrorBean;
import com.didekinlib.model.usuario.Usuario;

import org.awaitility.Duration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.R.id.user_data_ac_layout;
import static com.didekindroid.testutil.ActivityTestUtils.checkToastInTest;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.clickNavigateUp;
import static com.didekindroid.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.usuario.dao.UsuarioDaoRemote.usuarioDao;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOneUser;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_TRAV_PLAZUELA_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekinlib.model.usuario.UsuarioExceptionMsg.USER_NAME_NOT_FOUND;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Awaitility.fieldIn;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
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
public class PasswordChangeAcTest implements ExtendableTestAc {

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
    private int activityLayoutId = R.id.password_change_ac_layout;

    @Before
    public void setUp() throws Exception
    {
        activity = (PasswordChangeAc) mActivityRule.getActivity();
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
    public void testPasswordChange_NotOK() throws InterruptedException, UiException
    {
        typePswdData("new_pepe_password", "new_wrong_password");
        onView(withId(R.id.password_change_ac_button)).check(matches(isDisplayed())).perform(click());

        checkToastInTest(R.string.error_validation_msg, activity, R.string.password_different);
        // Se queda en la misma actividad.
        onView(withId(R.id.password_change_ac_layout)).check(matches(isDisplayed()));

        cleanOneUser(USER_PEPE);
    }

    @Test
    public void testPasswordChange_OK() throws UiException, InterruptedException
    {
        typePswdData("new_pepe_password", "new_pepe_password");
        onView(withId(R.id.password_change_ac_button)).check(matches(isDisplayed())).perform(click());

        TimeUnit.MILLISECONDS.sleep(2500);
        onView(withId(getNextViewResourceId())).check(matches(isDisplayed()));
        checkUp(activityLayoutId);

        usuarioDao.deleteUser();
    }

    //  ================================== CONTROLLER TESTS  =====================================

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

        cleanOneUser(USER_PEPE);
    }

    @Test
    public void testCheckLoginData_1() throws Exception
    {
        // Caso WRONG: We test the change to false.
        isPswdDataOk = new AtomicBoolean(true);
        typePswdData("password1", "password2");

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                assertThat(isPswdDataOk.getAndSet(activity.checkLoginData()), is(true));
            }
        });
        await().atMost(1, SECONDS).untilFalse(isPswdDataOk);
        assertThat(activity.usuarioBean.getUserName(), allOf(
                is(activity.userName),
                is(activity.getIntent().getStringExtra(user_name.key))
        ));
        checkToastInTest(R.string.error_validation_msg, activity, R.string.password_different);

        cleanOneUser(USER_PEPE);
    }

    @Test
    public void testCheckLoginData_2() throws UiException
    {
        // Caso OK: We test the change to true.
        isPswdDataOk = new AtomicBoolean(false);
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

        cleanOneUser(USER_PEPE);
    }

    @Test
    public void testChangePasswordInRemote() throws Exception
    {
        // Caso OK.
        typePswdData("password1", "password1");

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                assertThat(activity.checkLoginData(), is(true));
                activity.changePasswordInRemote();
            }
        });

        TimeUnit.MILLISECONDS.sleep(2500);
        checkToastInTest(R.string.password_remote_change, activity);
        onView(withId(getNextViewResourceId())).check(matches(isDisplayed()));

        usuarioDao.deleteUser();
    }

    @Test
    public void testProcessBackChangedPswdRemote() throws Exception
    {
        testChangePasswordInRemote();
    }

    @Test
    public void testProcessErrorInReactor_1() throws Exception
    {
        isExceptionThrown = new AtomicBoolean(false);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                activity.processErrorInReactor(new UiException(new ErrorBean(USER_NAME_NOT_FOUND)));
                assertThat(isExceptionThrown.getAndSet(true), is(false));
            }
        });
        waitAtMost(Duration.ONE_SECOND).untilTrue(isExceptionThrown);
        checkToastInTest(R.string.username_wrong_in_login, activity);
        onView(withId(activityLayoutId)).check(matches(isDisplayed()));
    }

    //  ================================== HELPERS  =====================================

    private void typePswdData(String password, String confirmation)
    {
        onView(withId(R.id.reg_usuario_password_ediT)).perform(replaceText(password));
        onView(withId(R.id.reg_usuario_password_confirm_ediT)).perform(replaceText(confirmation));
    }
}