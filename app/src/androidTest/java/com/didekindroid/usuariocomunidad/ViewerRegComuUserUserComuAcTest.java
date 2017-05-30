package com.didekindroid.usuariocomunidad;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.api.ViewerParentInjectedIf;
import com.didekindroid.comunidad.ViewerRegComuFr;
import com.didekindroid.security.CtrlerAuthToken;
import com.didekindroid.usuario.ViewerRegUserFr;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.typeComunidadData;
import static com.didekindroid.testutil.ActivityTestUtils.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtils.isResourceIdDisplayed;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.usuario.testutil.UserEspressoTestUtil.typeUserDataFull;
import static com.didekindroid.usuariocomunidad.RolUi.INQ;
import static com.didekindroid.usuariocomunidad.RolUi.PRE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.typeUserComuData;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 24/05/17
 * Time: 11:26
 */
@RunWith(AndroidJUnit4.class)
public class ViewerRegComuUserUserComuAcTest {

    final AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public ActivityTestRule<RegComuAndUserAndUserComuAc> activityRule = new ActivityTestRule<>(RegComuAndUserAndUserComuAc.class, true, true);
    RegComuAndUserAndUserComuAc activity;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        AtomicReference<ViewerRegComuUserUserComuAc> viewerAtomic = new AtomicReference<>(null);
        viewerAtomic.compareAndSet(null, activity.viewer);
        waitAtMost(4, SECONDS).untilAtomic(viewerAtomic, notNullValue());
    }

    @Test
    public void test_NewViewerRegComuUserUserComuAc() throws Exception
    {
        assertThat(activity.viewer.getController(), isA(CtrlerAuthToken.class));
    }

    @Test
    public void test_DoViewInViewer() throws Exception
    {
        onView(withId(R.id.reg_com_usuario_usuariocomu_button)).check(matches(isDisplayed()));
    }

    @Test
    public void test_RegComuUserButtonListener() throws Exception
    {
        typeUserDataFull("yo@email.com", "alias1", "password1", "password1");
        typeUserComuData("port2", "escale_b", "planta-N", "puerta5", PRE, INQ);
        typeComunidadData();
        onView(withId(R.id.reg_com_usuario_usuariocomu_button)).perform(scrollTo(), click());

        waitAtMost(4, SECONDS).until(isResourceIdDisplayed(seeUserComuByUserFrRsId));
    }

    @Test
    public void test_ReplaceComponent() throws Exception
    {

    }

    @Test
    public void test_SetChildViewer() throws Exception
    {

    }

    @Test
    public void test_GetChildViewer() throws Exception
    {

    }

    //  =========================  TESTS FOR ACTIVITY/FRAGMENT LIFECYCLE  ===========================

    @Test
    public void test_OnCreate()
    {
        // Check for initialization of fragments viewers.
        ViewerParentInjectedIf viewerParent = activity.viewer;
        assertThat(viewerParent.getChildViewer(ViewerRegComuFr.class), notNullValue());
        assertThat(viewerParent.getChildViewer(ViewerRegUserFr.class), notNullValue());
        assertThat(viewerParent.getChildViewer(ViewerRegUserComuFr.class), notNullValue());
    }

    @Test
    public void test_OnStop()
    {
        checkSubscriptionsOnStop(activity.viewer.getController(), activity);
    }

}