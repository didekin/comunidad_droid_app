package com.didekindroid.router;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.accesorio.ConfidencialidadAc;
import com.didekindroid.comunidad.ComuSearchAc;
import com.didekindroid.incidencia.list.IncidSeeByComuAc;
import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.lib_one.api.ActivityNextMock;
import com.didekindroid.usuario.DeleteMeAc;
import com.didekindroid.usuario.LoginAc;
import com.didekindroid.usuario.PasswordChangeAc;
import com.didekindroid.usuario.UserDataAc;
import com.didekindroid.usuariocomunidad.listbyuser.SeeUserComuByUserAc;
import com.didekindroid.usuariocomunidad.register.RegComuAndUserAndUserComuAc;
import com.didekindroid.usuariocomunidad.register.RegComuAndUserComuAc;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.runner.lifecycle.Stage.RESUMED;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_CLOSED_LIST_FLAG;
import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.lib_one.testutil.MockTestNavigation.nextMockAcLayout;
import static com.didekindroid.router.MnRouterAction.comu_search_mn;
import static com.didekindroid.router.MnRouterAction.confidencialidad_mn;
import static com.didekindroid.router.MnRouterAction.delete_me_mn;
import static com.didekindroid.router.MnRouterAction.incid_see_closed_by_comu_mn;
import static com.didekindroid.router.MnRouterAction.incid_see_open_by_comu_mn;
import static com.didekindroid.router.MnRouterAction.login_mn;
import static com.didekindroid.router.MnRouterAction.navigateUp;
import static com.didekindroid.router.MnRouterAction.password_change_mn;
import static com.didekindroid.router.MnRouterAction.reg_nueva_comunidad_mn;
import static com.didekindroid.router.MnRouterAction.see_usercomu_by_user_mn;
import static com.didekindroid.router.MnRouterAction.user_data_mn;
import static com.didekindroid.testutil.ActivityTestUtil.checkUp;
import static com.didekindroid.testutil.ActivityTestUtil.getActivitesInTaskByStage;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 06/02/2018
 * Time: 14:20
 * <p>
 * Tests for menu items which require intent initialization are omitted.
 */
@RunWith(AndroidJUnit4.class)
public class MnRouterActionTest {

    @Rule
    public IntentsTestRule<ActivityMock> activityRule = new IntentsTestRule<ActivityMock>(ActivityMock.class) {
        @Override
        protected Intent getActivityIntent()
        {
            Intent intent = new Intent();
            intent.putExtra("keyTest_2", "Value_keyTest_2");
            return intent;
        }
    };

    ActivityMock activity;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
    }

    @After
    public void cleanUp()
    {
        secInitializer.get().getTkCacher().updateIsRegistered(false);
    }

    @Test
    public void test_navigateUp_1() throws Exception
    {
        ActivityManager manager = (ActivityManager) activity.getSystemService(ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            manager.getAppTasks().get(0).startActivity(activity, new Intent(activity, ActivityNextMock.class), new Bundle(0));
            // Calling indirectly the method to test and check new activity layout.
            checkUp(R.id.mock_ac_layout);
            // Check that the up activity is resumed and has the original intent.
            Collection<Activity> activities = getActivitesInTaskByStage(RESUMED);
            assertThat(activities.size(), is(1));
            for (Activity next : activities) {
                assertThat(next.getComponentName().getClassName(), is(ActivityMock.class.getCanonicalName()));
                assertThat(next.getIntent().getStringExtra("keyTest_2"), is("Value_keyTest_2"));
            }
        }
    }

    @Test
    public void test_navigateUp_2() throws Exception
    {
        // From ActivityMock we initiate ActivityNextMock.
        Intent intent = new Intent(getTargetContext(), ActivityNextMock.class).setFlags(FLAG_ACTIVITY_NEW_TASK);
        ActivityNextMock nextAc = (ActivityNextMock) getInstrumentation().startActivitySync(intent);
        onView(withId(nextMockAcLayout)).check(matches(isDisplayed()));
        // Navigate up to ActivityMock from ActivityNextMock.
        navigateUp.initActivity(nextAc);
        onView(withId(R.id.mock_ac_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void test_confidencialidad_mn()
    {
        confidencialidad_mn.initActivity(activity);
        intended(hasComponent(ConfidencialidadAc.class.getName()));
    }

    @Test
    public void test_incid_see_closed_by_comu_mn()
    {
        incid_see_closed_by_comu_mn.initActivity(activity);
        intended(hasComponent(IncidSeeByComuAc.class.getName()));
        intended(hasExtras(hasEntry(INCID_CLOSED_LIST_FLAG.key, is(false))));
    }

    @Test
    public void test_incid_see_open_by_comu_mn()
    {
        incid_see_open_by_comu_mn.initActivity(activity);
        intended(hasComponent(IncidSeeByComuAc.class.getName()));
        intended(hasExtras(hasEntry(INCID_CLOSED_LIST_FLAG.key, is(true))));
    }

    @Test
    public void test_comu_search_mn()
    {
        comu_search_mn.initActivity(activity);
        intended(hasComponent(ComuSearchAc.class.getName()));
    }

    @Test
    public void test_delete_me_mn()
    {
        secInitializer.get().getTkCacher().updateIsRegistered(true);
        waitAtMost(4, SECONDS).until(secInitializer.get().getTkCacher()::isRegisteredUser);
        delete_me_mn.initActivity(activity);
        intended(hasComponent(DeleteMeAc.class.getName()));
    }

    @Test
    public void test_login_mn()
    {
        login_mn.initActivity(activity);
        intended(hasComponent(LoginAc.class.getName()));
    }

    @Test
    public void test_password_change_mn()
    {
        secInitializer.get().getTkCacher().updateIsRegistered(true);
        waitAtMost(4, SECONDS).until(secInitializer.get().getTkCacher()::isRegisteredUser);
        password_change_mn.initActivity(activity);
        intended(hasComponent(PasswordChangeAc.class.getName()));
    }

    @Test
    public void test_user_data_mn()
    {
        secInitializer.get().getTkCacher().updateIsRegistered(true);
        waitAtMost(4, SECONDS).until(secInitializer.get().getTkCacher()::isRegisteredUser);
        user_data_mn.initActivity(activity);
        intended(hasComponent(UserDataAc.class.getName()));
    }

    @Test
    public void test_reg_nueva_comunidad_mn_1()
    {
        reg_nueva_comunidad_mn.initActivity(activity);
        intended(hasComponent(RegComuAndUserAndUserComuAc.class.getName()));
    }

    @Test
    public void test_reg_nueva_comunidad_mn_2()
    {
        secInitializer.get().getTkCacher().updateIsRegistered(true);
        waitAtMost(4, SECONDS).until(secInitializer.get().getTkCacher()::isRegisteredUser);
        reg_nueva_comunidad_mn.initActivity(activity);
        intended(hasComponent(RegComuAndUserComuAc.class.getName()));
    }

    @Test
    public void test_see_usercomu_by_user_mn()
    {
        secInitializer.get().getTkCacher().updateIsRegistered(true);
        waitAtMost(4, SECONDS).until(secInitializer.get().getTkCacher()::isRegisteredUser);
        see_usercomu_by_user_mn.initActivity(activity);
        intended(hasComponent(SeeUserComuByUserAc.class.getName()));
    }
}