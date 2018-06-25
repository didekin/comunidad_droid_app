package com.didekindroid.usuariocomunidad.listbyuser;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.AppCompatActivity;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.ActivityNextMock;
import com.didekindroid.lib_one.api.router.FragmentInitiator;
import com.didekinlib.model.comunidad.Comunidad;

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
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.usuariocomunidad.repository.UserComuDao.userComuDao;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.userComuDataLayout;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_REAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.signUpGetComu;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

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
    }

    @Test
    public void test_GetBeanIdFunction()
    {
    }


}