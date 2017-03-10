package com.didekindroid.incidencia.spinner;

import android.app.Activity;
import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.didekindroid.api.ActivityMock;
import com.didekindroid.api.ManagerMock;
import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.spinner.ManagerComuSpinnerIf.ControllerComuSpinnerIf;
import com.didekindroid.incidencia.spinner.ManagerComuSpinnerIf.ViewerComuSpinnerIf;
import com.didekinlib.http.ErrorBean;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static com.didekindroid.api.ManagerMock.flagManageMockExecMethod;
import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.incidencia.spinner.ViewerComuSpinner.newComuSpinnerViewer;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.testutil.ConstantExecution.MANAGER_AFTER_ERROR_CONTROL;
import static com.didekindroid.testutil.ConstantExecution.MANAGER_FLAG_INITIAL;
import static com.didekinlib.http.GenericExceptionMsg.GENERIC_INTERNAL_ERROR;
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

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    ViewerComuSpinner viewer;
    boolean flagIntent;
    AtomicLong comunidadId = new AtomicLong(0);
    //    static AtomicInteger flagForExecution = new AtomicInteger(0);
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
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC));
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
        viewer.processControllerError(new UiException(new ErrorBean(GENERIC_INTERNAL_ERROR)));
        assertThat(flagManageMockExecMethod.getAndSet(MANAGER_FLAG_INITIAL), is(MANAGER_AFTER_ERROR_CONTROL));
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
        assertThat(viewer.clearControllerSubscriptions(), is(999));
    }

    // ............................ HELPERS ..................................

    class ManagerComuSpinnerForTest extends ManagerMock implements ManagerComuSpinnerIf {

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
            return null;
        }

        @Override
        public Spinner getSpinnerViewInManager()
        {
            return null;
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
            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC), is(BEFORE_METHOD_EXEC));
        }

        @Override  // Used in testClearControllerSubscriptions().
        public int clearSubscriptions()
        {
            return 999;
        }
    }
}