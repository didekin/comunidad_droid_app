package com.didekindroid.incidencia.spinner;

import android.app.Activity;
import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.didekindroid.ManagerDumbImp;
import com.didekindroid.incidencia.spinner.ManagerComuSpinnerIf.ControllerComuSpinnerIf;
import com.didekindroid.incidencia.spinner.ManagerComuSpinnerIf.ViewerComuSpinnerIf;
import com.didekindroid.testutil.MockActivity;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.exception.UiExceptionRouter.LOGIN_ACC;
import static com.didekindroid.incidencia.spinner.ViewerComuSpinner.newComuSpinnerViewer;
import static com.didekindroid.testutil.ActivityTestUtils.testProcessCtrlError;
import static com.didekinlib.http.GenericExceptionMsg.TOKEN_NULL;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.fieldIn;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 18/02/17
 * Time: 12:36
 */
@RunWith(AndroidJUnit4.class)
public class ViewerComuSpinnerTest {

    @Rule
    public ActivityTestRule<MockActivity> activityRule = new ActivityTestRule<>(MockActivity.class, true, true);

    ViewerComuSpinner viewer;
    boolean flagIntent;
    AtomicLong comunidadId = new AtomicLong(0);
    static AtomicInteger flagForExecution = new AtomicInteger(0);
    Activity activity;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer = newComuSpinnerViewer(new ManagerComuSpinnerForTest(activity));
            }
        });
        waitAtMost(1L, SECONDS).until(fieldIn(this).ofType(ViewerComuSpinner.class), notNullValue());
    }

    // ............................ TESTS ..................................

    @Test
    public void testSetDataInView()
    {
        ControllerComuSpinnerIf controller = new ControllerComuSpinnerForTest(viewer);
        // Inject mock controller.
        viewer.injectController(controller);
        assertThat(viewer.setDataInView(new Bundle()), CoreMatchers.<ViewerComuSpinnerIf>is(viewer));
        assertThat(flagForExecution.getAndSet(0), is(19));
    }

    @Test
    public void testInitComuSelectedIndex_NoIntent() throws Exception
    {
        Bundle savedState = new Bundle();
        savedState.putLong(COMUNIDAD_ID.key, 8L);
        viewer.initSelectedIndex(savedState);
        assertThat(viewer.getComunidadSelectedId(), is(8L));
    }

    @Test
    public void testInitComuSelectedIndex_WithIntent() throws Exception
    {
        comunidadId.set(321L);
        viewer.initSelectedIndex(null);
        // Value in mock manager.
        assertThat(viewer.getComunidadSelectedId(), is(321L));
    }

    @Test
    public void testProcessControllerError()
    {
        testProcessCtrlError(viewer, TOKEN_NULL, LOGIN_ACC);
    }

    @Test
    public void testSaveComuSelectedIndex()
    {
        viewer.comunidadSelectedId = 111L;
        Bundle newBundle = new Bundle();
        viewer.saveSelectedIndex(newBundle);
        assertThat(newBundle.getLong(COMUNIDAD_ID.key), is(111L));
    }

    @Test
    public void testClearControllerSubscriptions()
    {
        ControllerComuSpinnerIf controller = new ControllerComuSpinnerForTest(viewer);
        // Inject mock controller.
        viewer.injectController(controller);
        viewer.clearControllerSubscriptions();
        assertThat(flagForExecution.getAndSet(0), is(29));
    }

    // ............................ HELPERS ..................................

    class ManagerComuSpinnerForTest extends ManagerDumbImp implements ManagerComuSpinnerIf {

        ManagerComuSpinnerForTest(Activity activity)
        {
            super(activity);
        }

        @Override // Used in testInitComuSelectedIndex_WithIntent().
        public long getComunidadIdInIntent()
        {
            return comunidadId.get();
        }

        @Override
        public Spinner initSpinnerView()
        {
            Spinner spinner = getSpinnerViewInManager();
            getSpinnerViewInManager().setOnItemSelectedListener(getSpinnerListener());
            return spinner;
        }

        @Override
        public Spinner getSpinnerViewInManager()
        {
            return new Spinner(activity);
        }

        @Override
        public AdapterView.OnItemSelectedListener getSpinnerListener()
        {
            return null;
        }
    }

    class ControllerComuSpinnerForTest extends ControllerComuSpinner implements
            ControllerComuSpinnerIf {

        ControllerComuSpinnerForTest(ViewerComuSpinnerIf viewerIn)
        {
            super(viewerIn);
        }

        @Override // Used in testSetDataInView().
        public void loadDataInSpinner()
        {
            assertThat(flagForExecution.getAndSet(19), is(0));
        }

        @Override  // Used in testClearControllerSubscriptions().
        public int clearSubscriptions()
        {
            assertThat(flagForExecution.getAndSet(29), is(0));
            return 99;
        }
    }
}