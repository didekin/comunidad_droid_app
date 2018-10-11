package com.didekindroid.usuariocomunidad.register;

import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.InjectorOfParentViewerIf;
import com.didekindroid.lib_one.api.ParentViewerIf;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.model.usuariocomunidad.UsuarioComunidad;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.testutil.ComuTestData.COMU_EL_ESCORIAL;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_PEPE;
import static com.didekindroid.lib_one.usuario.UserTestData.authTokenExample;
import static com.didekindroid.testutil.ActivityTestUtil.checkSubscriptionsOnStop;
import static com.didekindroid.usuariocomunidad.RolUi.INQ;
import static com.didekindroid.usuariocomunidad.RolUi.PRE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.checkUserComuData;
import static com.didekindroid.usuariocomunidad.testutil.UserComuEspressoTestUtil.typeUserComuData;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.COMU_TRAV_PLAZUELA_PEPE;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 24/05/17
 * Time: 12:06
 */
@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public class ViewerRegUserComuFrTest {

    @Rule
    public ActivityTestRule<RegComuAndUserAndUserComuAc> activityRule =
            new ActivityTestRule<>(RegComuAndUserAndUserComuAc.class, true, true);

    private RegUserComuFr fragment;
    private RegComuAndUserAndUserComuAc activity;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        fragment = (RegUserComuFr) activity.getSupportFragmentManager().findFragmentById(R.id.reg_usercomu_frg);

        AtomicReference<ViewerRegUserComuFr> viewerAtomic = new AtomicReference<>(null);
        viewerAtomic.compareAndSet(null, fragment.viewer);
        waitAtMost(4, SECONDS).untilAtomic(viewerAtomic, notNullValue());
    }

    @Test
    public void test_OnActivityCreated()
    {
        assertThat(fragment.viewer.getController(), notNullValue());
        assertThat(InjectorOfParentViewerIf.class.isInstance(activity), is(true));
        ParentViewerIf parentViewer = (ParentViewerIf) fragment.viewer.getParentViewer();
        assertThat(parentViewer.getChildViewer(ViewerRegUserComuFr.class), is(fragment.viewer));

        // test_GetUserComuFromViewer_Wrong
        typeUserComuData("po + =", "escale_b", "planta-N", "puerta5", PRE, INQ);
        assertThat(fragment.viewer.getUserComuFromViewer(new StringBuilder(), COMU_EL_ESCORIAL, USER_PEPE), nullValue());

        // test_GetUserComuFromViewer_OK
        onView(withId(R.id.reg_usercomu_portal_ed)).perform(replaceText("port2"));
        assertThat(fragment.viewer.getUserComuFromViewer(new StringBuilder(), COMU_EL_ESCORIAL, USER_PEPE), allOf(
                notNullValue(),
                is(new UsuarioComunidad.UserComuBuilder(COMU_EL_ESCORIAL, USER_PEPE)
                        .portal("port2")
                        .escalera("escale_b")
                        .planta("planta_N")
                        .puerta("puerta5")
                        .build())
        ));

        // test_OnStop
        checkSubscriptionsOnStop(activity, fragment.viewer.getController());
    }

    @Test
    public void test_PaintUserComuView()
    {
        final UsuarioComunidad userComu = COMU_TRAV_PLAZUELA_PEPE;
        activity.runOnUiThread(() -> fragment.viewer.paintUserComuView(userComu));
        checkUserComuData(userComu);
    }

    @Test
    public void test_DoViewInViewer_NotNull() throws UiException
    {
        // Precondition
        fragment.viewer.getController().getTkCacher().updateAuthToken(authTokenExample);

        activity.runOnUiThread(() -> fragment.viewer.doViewInViewer(new Bundle(0), COMU_TRAV_PLAZUELA_PEPE));
        checkUserComuData(COMU_TRAV_PLAZUELA_PEPE);
    }
}