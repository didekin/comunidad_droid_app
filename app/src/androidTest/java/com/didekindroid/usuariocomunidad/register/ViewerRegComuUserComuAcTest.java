package com.didekindroid.usuariocomunidad.register;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.api.ParentViewerInjectedIf;
import com.didekindroid.comunidad.ViewerRegComuFr;
import com.didekindroid.exception.UiException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.typeComunidadData;
import static com.didekindroid.testutil.ActivityTestUtils.checkSubscriptionsOnStop;
import static com.didekindroid.testutil.ActivityTestUtils.focusOnButton;
import static com.didekindroid.testutil.ActivityTestUtils.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtils.isToastInView;
import static com.didekindroid.testutil.ActivityTestUtils.isViewDisplayed;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.RolUi.INQ;
import static com.didekindroid.usuariocomunidad.RolUi.PRE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.typeUserComuData;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static io.reactivex.Single.just;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 01/06/17
 * Time: 20:11
 */
@RunWith(AndroidJUnit4.class)
public class ViewerRegComuUserComuAcTest {

    @Rule
    public ActivityTestRule<RegComuAndUserComuAc> acActivityTestRule = new ActivityTestRule<>(RegComuAndUserComuAc.class, true, true);
    RegComuAndUserComuAc activity;

    @Before
    public void setUp()
    {
        activity = acActivityTestRule.getActivity();
        AtomicReference<ViewerRegComuUserComuAc> viewerAtomic = new AtomicReference<>(null);
        viewerAtomic.compareAndSet(null, activity.viewer);
        waitAtMost(4, SECONDS).untilAtomic(viewerAtomic, notNullValue());
    }

    @Test
    public void test_NewViewerRegComuUserComuAc() throws Exception
    {
        assertThat(activity.viewer.getController(), isA(CtrlerUsuarioComunidad.class));
    }

    @Test
    public void test_DoViewInViewer() throws Exception
    {
        onView(withId(R.id.reg_comu_usuariocomunidad_button)).perform(scrollTo()).check(matches(isDisplayed()));
    }

    @Test
    public void test_RegComuUserComuButtonListener_1() throws Exception
    {
        // Precondition: user is registered.
        signUpAndUpdateTk(COMU_ESCORIAL_PEPE);

        typeUserComuData("port2", "escale_b", "planta-N", "puerta5", PRE, INQ);
        int buttonId = R.id.reg_comu_usuariocomunidad_button;
        focusOnButton(activity, buttonId);
        typeComunidadData();
        onView(withId(buttonId)).perform(scrollTo(), click());

        waitAtMost(5, SECONDS).until(isResourceIdDisplayed(seeUserComuByUserFrRsId));
        cleanOptions(CLEAN_PEPE);
    }

    @Test
    public void test_RegComuUserComuButtonListener_2() throws Exception
    {
        // Precondition: user is registered.
        signUpAndUpdateTk(COMU_ESCORIAL_PEPE);

        typeUserComuData("port2", "escale_b", "planta-N", "puerta5", PRE, INQ);
        int buttonId = R.id.reg_comu_usuariocomunidad_button;
        focusOnButton(activity, buttonId);
        onView(withId(buttonId)).perform(scrollTo(), click());

        // Error: no ha seleccionado municipio, ni tipo de vía, ni nombre de vía.
        waitAtMost(5, SECONDS).until(isToastInView(R.string.error_validation_msg, activity,
                R.string.municipio,
                R.string.nombre_via,
                R.string.tipo_via));
        cleanOptions(CLEAN_PEPE);
    }

    @Test
    public void test_RegComuAndUserComuObserver() throws IOException, UiException
    {
        // Precondition: user is registered.
        signUpAndUpdateTk(COMU_ESCORIAL_PEPE);

        ViewerRegComuUserComuAc.RegComuAndUserComuObserver observer = activity.viewer.new RegComuAndUserComuObserver();
        just(true).subscribeWith(observer);
        waitAtMost(4, SECONDS).until(isViewDisplayed(withId(seeUserComuByUserFrRsId)));
        cleanOptions(CLEAN_PEPE);
    }

    //  =========================  TESTS FOR ACTIVITY/FRAGMENT LIFECYCLE  ===========================

    @Test
    public void test_OnCreate() throws Exception
    {
        // Check for initialization of fragments viewers.
        ParentViewerInjectedIf viewerParent = activity.viewer;
        assertThat(viewerParent.getChildViewer(ViewerRegComuFr.class), notNullValue());
        assertThat(viewerParent.getChildViewer(ViewerRegUserComuFr.class), notNullValue());
        test_DoViewInViewer();
    }

    @Test
    public void test_OnStop()
    {
        checkSubscriptionsOnStop(activity, activity.viewer.getController());
    }
}