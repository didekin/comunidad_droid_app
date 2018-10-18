package com.didekindroid.router;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.comunidad.ComuSearchAc;
import com.didekindroid.incidencia.list.IncidSeeByComuAc;
import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.usuariocomunidad.listbyuser.SeeUserComuByUserAc;
import com.didekindroid.usuariocomunidad.register.RegComuAndUserAndUserComuAc;
import com.didekindroid.usuariocomunidad.register.RegComuAndUserComuAc;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_CLOSED_LIST_FLAG;
import static com.didekindroid.lib_one.security.SecInitializer.secInitializer;
import static com.didekindroid.lib_one.usuario.UserTestData.authTokenExample;
import static com.didekindroid.router.DidekinMnAction.comu_search_mn;
import static com.didekindroid.router.DidekinMnAction.incid_see_closed_by_comu_mn;
import static com.didekindroid.router.DidekinMnAction.incid_see_open_by_comu_mn;
import static com.didekindroid.router.DidekinMnAction.reg_nueva_comunidad_mn;
import static com.didekindroid.router.DidekinMnAction.see_usercomu_by_user_mn;
import static com.didekindroid.router.testutil.UserRouterMapUtil.checkUserMnActionMap;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;

/**
 * User: pedro@didekin
 * Date: 06/02/2018
 * Time: 14:20
 * <p>
 * Tests for menu items which require intent initialization are omitted.
 */
@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public class DidekinMnActionTest {

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

    private ActivityMock activity;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
    }

    @After
    public void cleanUp() throws UiException
    {
        secInitializer.get().getTkCacher().updateAuthToken(null);
    }

    //  ===========================================================================

    @Test
    public void test_InitStaticMap()
    {
        checkUserMnActionMap();
    }

    @Test
    public void test_incid_see_closed_by_comu_mn()
    {
        incid_see_closed_by_comu_mn.initActivity(activity);
        intended(hasComponent(IncidSeeByComuAc.class.getName()));
        intended(hasExtras(hasEntry(INCID_CLOSED_LIST_FLAG.key, is(true))));
    }

    @Test
    public void test_incid_see_open_by_comu_mn()
    {
        incid_see_open_by_comu_mn.initActivity(activity);
        intended(hasComponent(IncidSeeByComuAc.class.getName()));
        intended(hasExtras(hasEntry(INCID_CLOSED_LIST_FLAG.key, is(false))));
    }

    @Test
    public void test_comu_search_mn()
    {
        comu_search_mn.initActivity(activity);
        intended(hasComponent(ComuSearchAc.class.getName()));
    }

    @Test
    public void test_reg_nueva_comunidad_mn_1()
    {
        reg_nueva_comunidad_mn.initActivity(activity);
        intended(hasComponent(RegComuAndUserAndUserComuAc.class.getName()));
    }

    @Test
    public void test_reg_nueva_comunidad_mn_2() throws UiException
    {
        secInitializer.get().getTkCacher().updateAuthToken(authTokenExample);
        waitAtMost(4, SECONDS).until(secInitializer.get().getTkCacher()::isUserRegistered);
        reg_nueva_comunidad_mn.initActivity(activity);
        intended(hasComponent(RegComuAndUserComuAc.class.getName()));
    }

    @Test
    public void test_see_usercomu_by_user_mn() throws UiException
    {
        secInitializer.get().getTkCacher().updateAuthToken(authTokenExample);
        waitAtMost(4, SECONDS).until(secInitializer.get().getTkCacher()::isUserRegistered);
        see_usercomu_by_user_mn.initActivity(activity);
        intended(hasComponent(SeeUserComuByUserAc.class.getName()));
    }
}