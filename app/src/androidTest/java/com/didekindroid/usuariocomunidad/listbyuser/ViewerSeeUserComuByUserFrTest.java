package com.didekindroid.usuariocomunidad.listbyuser;

import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.AppCompatActivity;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.ActivityNextMock;
import com.didekindroid.lib_one.api.router.FragmentInitiator;
import com.didekinlib.model.comunidad.Comunidad;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_LIST_ID;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.testutil.ActivityTestUtil.checkSubscriptionsOnStop;
import static com.didekindroid.usuariocomunidad.repository.UserComuDao.userComuDao;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.userComuDataLayout;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_REAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpGetComu;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public class ViewerSeeUserComuByUserFrTest {

    @Rule
    public ActivityTestRule<ActivityNextMock> activityRule = new ActivityTestRule<>(ActivityNextMock.class, false, true);

    private SeeUserComuByUserFr fr;
    private ViewerSeeUserComuByUserFr viewer;
    private Comunidad comuEscorial = null;

    @Before
    public void setUp()
    {
        comuEscorial = signUpGetComu(COMU_ESCORIAL_PEPE);  // Almería.
        userComuDao.regComuAndUserComu(COMU_REAL_PEPE).blockingAwait();  // Alicante.  Position 0.

        AppCompatActivity activity = activityRule.getActivity();
        fr = new SeeUserComuByUserFr();
        new FragmentInitiator<SeeUserComuByUserFr>(activity, R.id.next_mock_ac_layout).initFragmentTx(fr);
        waitAtMost(4, SECONDS).until(() -> activity.getSupportFragmentManager().findFragmentByTag(fr.getClass().getName()) != null);
        waitAtMost(4, SECONDS).until(() -> fr.viewer != null);
        viewer = fr.viewer;
    }

    @After
    public void cleanUp()
    {
        cleanOptions(CLEAN_PEPE);
    }

    @Test
    public void test_DoViewInViewer()
    {
        waitAtMost(4, SECONDS).until(() -> viewer.getViewInViewer().getCount() == 2);
        assertThat(viewer.getViewInViewer().getCheckedItemPosition(), is(0));

        viewer.setSelectedItemId(comuEscorial.getC_Id());
        // Exec.
        viewer.doViewInViewer(null, null);
        waitAtMost(4, SECONDS).until(() -> viewer.getViewInViewer().getCheckedItemPosition() == 1);
    }

    @Test
    public void test_setOnItemClickListener()
    {
        waitAtMost(4, SECONDS).until(() -> viewer.getViewInViewer().getCount() == 2);
        onData(is(COMU_ESCORIAL_PEPE)).check(matches(isDisplayed())).perform(click());
        onView(withId(userComuDataLayout)).check(matches(isDisplayed()));
    }

    @Test
    public void test_InitSelectedItemId()
    {
        Bundle savedState = new Bundle(1);
        savedState.putLong(COMUNIDAD_LIST_ID.key, 233L);
        // Exec
        viewer.initSelectedItemId(savedState);
        // Check.
        assertThat(viewer.getSelectedItemId(), is(233L));
    }

    @Test
    public void test_GetBeanIdFunction() throws Exception
    {
        assertThat(viewer.getBeanIdFunction().apply(
                new UsuarioComunidad.UserComuBuilder(comuEscorial, USER_JUAN).userComuRest(COMU_ESCORIAL_PEPE).build()),
                is(comuEscorial.getC_Id()));
    }

    @Test
    public void test_SaveState()
    {
        viewer.setSelectedItemId(322L);
        Bundle bundle = new Bundle(1);
        // Exec
        viewer.saveState(bundle);
        // Check.
        assertThat(bundle.getLong(COMUNIDAD_LIST_ID.key), is(322L));
    }

    @Test
    public void test_OnStopFragment()
    {
        checkSubscriptionsOnStop(viewer);
    }
}