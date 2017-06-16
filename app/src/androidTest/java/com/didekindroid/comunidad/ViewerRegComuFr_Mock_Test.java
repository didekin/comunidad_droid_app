package com.didekindroid.comunidad;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.api.ActivityMock;
import com.didekindroid.api.SpinnerMockFr;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.didekindroid.comunidad.ViewerRegComuFr.newViewerRegComuFr;
import static com.didekindroid.comunidad.testutil.ComuDataTestUtil.COMU_TRAV_PLAZUELA_11;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.checkSpinnersOff;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.checkSpinnersOffNull;
import static com.didekindroid.comunidad.testutil.ComuEspresoTestUtil.checkSubsetSpinnersOff;
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
    ViewerRegComuFr viewer;
    ActivityMock activity;
    SpinnerMockFr spinnerMockFr;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        spinnerMockFr = new SpinnerMockFr();
        final AtomicBoolean isRun = new AtomicBoolean(false);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                activity.getSupportFragmentManager().beginTransaction()
                        .add(R.id.mock_ac_layout, spinnerMockFr, "spinnerMockFr")
                        .commitNow();
                spinnerMockFr = (SpinnerMockFr) activity.getSupportFragmentManager().findFragmentByTag("spinnerMockFr");
                isRun.compareAndSet(false, true);
            }
        });
        waitAtMost(4, SECONDS).untilTrue(isRun);
    }

    //    =============================================================================================

    @Test
    public void test_InitializeSpinnersFromComunidad() throws Exception
    {
        // Case: comunidadId == 0, viewBean != null.
        assertThat(COMU_TRAV_PLAZUELA_11.getC_Id(), is(0L));

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer = newViewerRegComuFr(spinnerMockFr.getView(), activity);
                viewer.initializeSpinnersFromComunidad(COMU_TRAV_PLAZUELA_11, null);
                checkSpinnersOff(viewer, COMU_TRAV_PLAZUELA_11);
            }
        });
    }

    @Test
    public void test_InitializeSpinnersFromComunidad_NULL() throws Exception
    {
        // Case: viewBean == null.

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer = newViewerRegComuFr(spinnerMockFr.getView(), activity);
                viewer.initializeSpinnersFromComunidad(null, null);
                checkSpinnersOffNull(viewer);
            }
        });
    }

    @Test
    public void test_DoInViewer()
    {
        // Case: comunidadId == 0, viewBean != null.
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer = newViewerRegComuFr(spinnerMockFr.getView(), activity);
                viewer.doViewInViewer(null, COMU_TRAV_PLAZUELA_11);
                checkSpinnersOff(viewer, COMU_TRAV_PLAZUELA_11);
            }
        });
    }

    @Test
    public void test_DoInViewer_NULL()
    {
        // Case: viewBean == null.
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer = newViewerRegComuFr(spinnerMockFr.getView(), activity);
                viewer.doViewInViewer(null, null);
                checkSubsetSpinnersOff(viewer);
            }
        });
    }
}