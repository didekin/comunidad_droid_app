package com.didekindroid.comunidad;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.lib_one.api.SpinnerTextMockFr;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekindroid.comunidad.ViewerRegComuFr.newViewerRegComuFr;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.checkComunidadTextsOffView;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.checkSpinnersOffView;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.checkSpinnersOffViewNull;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.checkSubsetSpinnersOff;
import static com.didekindroid.comunidad.testutil.ComuTestData.COMU_TRAV_PLAZUELA_11;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 09/05/17
 * Time: 13:12
 */
@RunWith(AndroidJUnit4.class)
public class ViewerRegComuFr_Mock_Test {

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, false, true);

    private ViewerRegComuFr viewer;
    private ActivityMock activity;
    private SpinnerTextMockFr spinnerTextMockFr;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        activity.runOnUiThread(() -> {
            spinnerTextMockFr = new SpinnerTextMockFr();
            activity.getSupportFragmentManager().beginTransaction()
                    .add(R.id.mock_ac_layout, spinnerTextMockFr, "spinnerTextMockFr")
                    .commitNow();
            spinnerTextMockFr = (SpinnerTextMockFr) activity.getSupportFragmentManager().findFragmentByTag("spinnerTextMockFr");
        });
        waitAtMost(4, SECONDS).until(() -> spinnerTextMockFr != null);
    }

    //    =============================================================================================

    @Test
    public void test_InitializeSpinnersFromComunidad()
    {
        // Case: comunidadId == 0, viewBean != null.
        assertThat(COMU_TRAV_PLAZUELA_11.getC_Id(), is(0L));

        activity.runOnUiThread(() -> {
            viewer = newViewerRegComuFr(requireNonNull(spinnerTextMockFr.getView()), activity);
            viewer.initializeSpinnersFromComunidad(COMU_TRAV_PLAZUELA_11, null);
            checkSpinnersOffView(viewer, COMU_TRAV_PLAZUELA_11);
        });
    }

    @Test
    public void test_InitializeSpinnersFromComunidad_NULL()
    {
        // Case: viewBean == null.

        activity.runOnUiThread(() -> {
            viewer = newViewerRegComuFr(requireNonNull(spinnerTextMockFr.getView()), activity);
            viewer.initializeSpinnersFromComunidad(null, null);
            checkSpinnersOffViewNull(viewer);
        });
    }

    @Test
    public void test_DoInViewer()
    {
        // Case: comunidadId == 0, viewBean != null.
        activity.runOnUiThread(() -> {
            viewer = newViewerRegComuFr(requireNonNull(spinnerTextMockFr.getView()), activity);
            viewer.doViewInViewer(null, COMU_TRAV_PLAZUELA_11);
            checkComunidadTextsOffView(viewer, COMU_TRAV_PLAZUELA_11);
            checkSpinnersOffView(viewer, COMU_TRAV_PLAZUELA_11);
        });
    }

    @Test
    public void test_DoInViewer_NULL()
    {
        // Case: viewBean == null.
        activity.runOnUiThread(() -> {
            viewer = newViewerRegComuFr(requireNonNull(spinnerTextMockFr.getView()), activity);
            viewer.doViewInViewer(null, null);
            checkSubsetSpinnersOff(viewer);
        });
    }
}