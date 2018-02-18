package com.didekindroid.usuario;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.usuario.PasswordChangeAc;
import com.didekinlib.model.usuario.Usuario;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.app.TaskStackBuilder.create;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withHint;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchAcLayout;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanWithTkhandler;
import static com.didekindroid.testutil.ActivityTestUtil.checkBack;
import static com.didekindroid.testutil.ActivityTestUtil.checkIsRegistered;
import static com.didekindroid.testutil.ActivityTestUtil.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtil.checkUp;
import static com.didekindroid.testutil.ActivityTestUtil.cleanTasks;
import static com.didekindroid.testutil.ActivityTestUtil.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtil.isToastInView;
import static com.didekindroid.lib_one.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.lib_one.usuario.dao.UsuarioDao.usuarioDaoRemote;
import static com.didekindroid.lib_one.usuario.testutil.UserEspressoTestUtil.typePswdWithPswdValidation;
import static com.didekindroid.lib_one.usuario.UserTestNavigation.pswdChangeAcRsId;
import static com.didekindroid.lib_one.usuario.UserTestNavigation.userDataAcRsId;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_DROID;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOneUser;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_REAL_DROID;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpAndUpdateTk;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 25/09/15
 * Time: 17:45
 */
@RunWith(AndroidJUnit4.class)
public class PasswordChangeAcTest {

    @Rule
    public ActivityTestRule<? extends Activity> mActivityRule = new ActivityTestRule<PasswordChangeAc>(PasswordChangeAc.class) {

        @Override
        protected void beforeActivityLaunched()
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                create(getTargetContext()).addParentStack(PasswordChangeAc.class).startActivities();
            }
        }

        @Override
        protected Intent getActivityIntent()
        {
            Usuario usuario = null;
            try {
                usuario = signUpAndUpdateTk(COMU_REAL_DROID);
            } catch (Exception e) {
                fail();
            }
            return new Intent().putExtra(user_name.key, usuario.getUserName());
        }
    };

    PasswordChangeAc activity;
    AtomicBoolean isClean = new AtomicBoolean(false);

    @Before
    public void setUp() throws Exception
    {
        activity = (PasswordChangeAc) mActivityRule.getActivity();
        checkIsRegistered(activity.viewer);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @After
    public void clean() throws UiException, ExecutionException, InterruptedException
    {
        if (!isClean.get()) {
            cleanOneUser(USER_DROID);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cleanTasks(activity);
        }
        SECONDS.sleep(2);
    }

    //    ============================  TESTS  ===================================

    @Test
    public void testOnCreate() throws Exception
    {
        onView(withId(pswdChangeAcRsId)).check(matches(isDisplayed()));

        onView(withId(R.id.reg_usuario_password_ediT)).check(matches(withText(containsString(""))))
                .check(matches(withHint(R.string.usuario_password_hint)))
                .check(matches(isDisplayed()));
        onView(withId(R.id.reg_usuario_password_confirm_ediT)).check(matches(withText(containsString(""))))
                .check(matches(withHint(R.string.usuario_password_confirm_hint)))
                .check(matches(isDisplayed()));
        onView(withId(R.id.password_validation_ediT)).check(matches(withText(containsString(""))))
                .check(matches(withHint(R.string.user_data_ac_password_hint)))
                .check(matches(isDisplayed()));
        onView(withId(R.id.password_change_ac_button)).check(matches(withText(R.string.modif_button_rot)))
                .check(matches(isDisplayed()));

        onView(withId(R.id.password_send_ac_button)).check(matches(withText(R.string.password_send_button_txt)))
                .check(matches(isDisplayed()));

        onView(withId(R.id.appbar)).check(matches(isDisplayed()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkUp(userDataAcRsId);
        }
    }

    @Test
    public void testPasswordChange_Up() throws UiException, InterruptedException
    {
        doPswdChange();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkUp(comuSearchAcLayout);
        }
        usuarioDaoRemote.deleteUser();
        cleanWithTkhandler();

        isClean.set(true);
    }

    @Test
    public void testPasswordChange_Back() throws UiException, InterruptedException
    {
        doPswdChange();

        checkBack(onView(withId(seeUserComuByUserFrRsId)).check(matches(isDisplayed())), pswdChangeAcRsId);

        usuarioDaoRemote.deleteUser();
        cleanWithTkhandler();

        isClean.set(true);
    }

    @Test
    public final void testOnStop() throws Exception
    {
        checkSubscriptionsOnStop(activity, activity.viewer.getController());
    }

    //    ============================  HELPERS  ===================================

    private void doPswdChange()
    {
        typePswdWithPswdValidation("new_pepe_password", "new_pepe_password", USER_DROID.getPassword());
        onView(withId(R.id.password_change_ac_button)).check(matches(isDisplayed())).perform(click());
        waitAtMost(6, SECONDS).until(isResourceIdDisplayed(seeUserComuByUserFrRsId));
        waitAtMost(4, SECONDS).until(isToastInView(R.string.password_remote_change, activity));
    }
}