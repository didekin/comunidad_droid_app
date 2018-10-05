package com.didekindroid.usuario;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.lib_one.usuario.PasswordChangeAc;
import com.didekinlib.model.usuario.Usuario;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.app.TaskStackBuilder.create;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchAcLayout;
import static com.didekindroid.lib_one.testutil.UiTestUtil.cleanTasks;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_DROID;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_DROID;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.usuario.UserTestData.regComuUserUserComuGetUser;
import static com.didekindroid.lib_one.usuario.UserTestNavigation.pswdChangeAcRsId;
import static com.didekindroid.lib_one.usuario.UsuarioBundleKey.user_name;
import static com.didekindroid.testutil.ActivityTestUtil.checkBack;
import static com.didekindroid.testutil.ActivityTestUtil.checkIsRegistered;
import static com.didekindroid.testutil.ActivityTestUtil.checkUp;
import static com.didekindroid.testutil.ActivityTestUtil.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtil.isToastInView;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_REAL_DROID;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;

/**
 * User: pedro@didekin
 * Date: 25/09/15
 * Time: 17:45
 */
@RunWith(AndroidJUnit4.class)
public class PasswordChange_App_Test {

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
            return new Intent().putExtra(user_name.key, usuario.getUserName());
        }
    };

    private PasswordChangeAc activity;
    private static Usuario usuario;

    @BeforeClass
    public static void setStatic() throws Exception
    {
        usuario = regComuUserUserComuGetUser(COMU_REAL_DROID);
    }

    @Before
    public void setUp() throws Exception
    {
        activity = (PasswordChangeAc) mActivityRule.getActivity();
        checkIsRegistered(activity.getViewer());
    }

    @After
    public void clean()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cleanTasks(activity);
        }
    }

    @AfterClass
    public static void cleanStatic()
    {
        cleanOptions(CLEAN_DROID);
    }

    //    ============================  TESTS  ===================================

    @Test
    public void testPasswordChange_Up()
    {
        doPswdChange();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            checkUp(comuSearchAcLayout);
        }
    }

    @Test
    public void testPasswordChange_Back()
    {
        doPswdChange();
        checkBack(onView(withId(seeUserComuByUserFrRsId)).check(matches(isDisplayed())), pswdChangeAcRsId);
    }

    //    ============================  HELPERS  ===================================

    private void doPswdChange()
    {
        onView(withId(R.id.reg_usuario_password_ediT)).perform(replaceText("new_pepe_password"));
        onView(withId(R.id.reg_usuario_password_confirm_ediT)).perform(replaceText("new_pepe_password"));
        onView(withId(R.id.password_validation_ediT)).perform(replaceText(USER_DROID.getPassword()), closeSoftKeyboard());

        onView(withId(R.id.password_change_ac_button)).check(matches(isDisplayed())).perform(click());
        waitAtMost(8, SECONDS).until(isToastInView(R.string.password_remote_change, activity));
        waitAtMost(8, SECONDS).until(isResourceIdDisplayed(seeUserComuByUserFrRsId));

    }
}