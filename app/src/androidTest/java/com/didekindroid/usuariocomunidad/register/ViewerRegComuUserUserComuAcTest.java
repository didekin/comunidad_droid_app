package com.didekindroid.usuariocomunidad.register;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.api.ParentViewerInjectedIf;
import com.didekindroid.comunidad.ViewerRegComuFr;
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
import static com.didekindroid.testutil.ActivityTestUtils.focusOnButton;
import static com.didekindroid.testutil.ActivityTestUtils.isResourceIdDisplayed;
import static com.didekindroid.testutil.ActivityTestUtils.isToastInView;
import static com.didekindroid.testutil.ActivityTestUtils.isViewDisplayed;
import static com.didekindroid.usuario.testutil.UserEspressoTestUtil.typeUserDataFull;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.CleanUserEnum.CLEAN_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOptions;
import static com.didekindroid.usuariocomunidad.RolUi.INQ;
import static com.didekindroid.usuariocomunidad.RolUi.PRE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_ESCORIAL_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
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
        assertThat(activity.viewer.getController(), isA(CtrlerUsuarioComunidad.class));
    }

    @Test
    public void test_DoViewInViewer() throws Exception
    {
        onView(withId(R.id.reg_com_usuario_usuariocomu_button)).perform(scrollTo()).check(matches(isDisplayed()));
    }

    @Test
    public void test_OnRegisterSuccess() throws Exception
    {
        // Precondition: the user is registered and the cache is initialized.
        signUpAndUpdateTk(COMU_ESCORIAL_PEPE);
        activity.viewer.onRegisterSuccess();
        waitAtMost(4, SECONDS).until(isViewDisplayed(withId(seeUserComuByUserFrRsId)));
        cleanOptions(CLEAN_PEPE);
    }

    @Test
    public void test_RegComuUserButtonListener_1() throws Exception
    {
        typeUserDataFull(USER_PEPE.getUserName(), USER_PEPE.getAlias(), USER_PEPE.getPassword(), USER_PEPE.getPassword());
        typeUserComuData("port2", "escale_b", "planta-N", "puerta5", PRE, INQ);
        int buttonId = R.id.reg_com_usuario_usuariocomu_button;
        focusOnButton(activity, buttonId);
        typeComunidadData();

        onView(withId(buttonId)).perform(scrollTo(), click());
        waitAtMost(6, SECONDS).until(isResourceIdDisplayed(seeUserComuByUserFrRsId));

        cleanOptions(CLEAN_PEPE);
    }

    @Test
    public void test_RegComuUserButtonListener_2() throws Exception
    {
        typeUserComuData("port2", "escale_b", "planta-N", "puerta5", PRE, INQ);
        int buttonId = R.id.reg_com_usuario_usuariocomu_button;
        focusOnButton(activity, buttonId);
        typeComunidadData();

        onView(withId(buttonId)).perform(scrollTo(), click());
        waitAtMost(5, SECONDS).until(isToastInView(R.string.error_validation_msg, activity,
                R.string.email_hint,
                R.string.alias,
                R.string.password));
    }

    @Test
    public void test_RegComuUserButtonListener_3() throws Exception
    {
        typeUserDataFull(USER_PEPE.getUserName(), USER_PEPE.getAlias(), USER_PEPE.getPassword(), USER_PEPE.getPassword());
        typeUserComuData("port2", "escale_b", "planta-N", "puerta5", PRE, INQ);
        int buttonId = R.id.reg_com_usuario_usuariocomu_button;
        focusOnButton(activity, buttonId);
        onView(withId(buttonId)).perform(scrollTo(), click());
        waitAtMost(5, SECONDS).until(isToastInView(R.string.error_validation_msg, activity,
                R.string.tipo_via,
                R.string.nombre_via,
                R.string.municipio));
    }

    //  =========================  TESTS FOR ACTIVITY/FRAGMENT LIFECYCLE  ===========================

    @Test
    public void test_OnCreate()
    {
        // Check for initialization of fragments viewers.
        ParentViewerInjectedIf viewerParent = activity.viewer;
        assertThat(viewerParent.getChildViewer(ViewerRegComuFr.class), notNullValue());
        assertThat(viewerParent.getChildViewer(ViewerRegUserFr.class), notNullValue());
        assertThat(viewerParent.getChildViewer(ViewerRegUserComuFr.class), notNullValue());
    }

    @Test
    public void test_OnStop()
    {
        checkSubscriptionsOnStop(activity, activity.viewer.getController());
    }
}