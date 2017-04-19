package com.didekindroid.incidencia.core;

import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Spinner;

import com.didekindroid.R;
import com.didekindroid.api.ActivityMock;
import com.didekindroid.api.SpinnerMockFr;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import static com.didekindroid.incidencia.core.ViewerAmbitoIncidSpinner.newViewerAmbitoIncidSpinner;
import static com.didekindroid.incidencia.utils.IncidBundleKey.AMBITO_INCIDENCIA_POSITION;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 30/03/17
 * Time: 17:01
 */
@RunWith(AndroidJUnit4.class)
public class ViewerAmbitoIncidSpinnerTest {

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    ViewerAmbitoIncidSpinner viewer;
    ActivityMock activity;
    Spinner spinner;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();

        final AtomicReference<ViewerAmbitoIncidSpinner> atomicViewer = new AtomicReference<>(null);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                activity.getSupportFragmentManager().beginTransaction()
                        .add(R.id.mock_ac_layout, new SpinnerMockFr(), null)
                        .commitNow();
                spinner = (Spinner) activity.findViewById(R.id.ambito_spinner);
                atomicViewer.compareAndSet(null, newViewerAmbitoIncidSpinner(spinner, activity, null));
            }
        });
        waitAtMost(2, SECONDS).untilAtomic(atomicViewer, notNullValue());
        viewer = atomicViewer.get();
    }

    // ==================================== TESTS ====================================

    @Test
    public void testNewViewerAmbitoIncidSpinner() throws Exception
    {
        assertThat(newViewerAmbitoIncidSpinner(spinner, activity, null).getController(), notNullValue());
    }

    @Test
    public void testInitSelectedItemId() throws Exception
    {

        viewer.incidenciaBean = new IncidenciaBean();
        Bundle bundle = new Bundle();

        viewer.initSelectedItemId(bundle);
        assertThat(viewer.getSelectedItemId(), is(0L));

        bundle = null;
        viewer.incidenciaBean.setCodAmbitoIncid((short) 13);
        viewer.initSelectedItemId(bundle);
        assertThat(viewer.getSelectedItemId(), is(13L));

        bundle = new Bundle();
        bundle.putLong(AMBITO_INCIDENCIA_POSITION.key, 91);
        assertThat(viewer.incidenciaBean.getCodAmbitoIncid(), is((short) 13));
        viewer.initSelectedItemId(bundle);
        assertThat(viewer.getSelectedItemId(), is(91L));
    }

    @Test
    public void testSaveState() throws Exception
    {
        /*assertThat(checkSavedStateInSpinner(null, 121, AMBITO_INCIDENCIA_POSITION, viewer), is(LONG_DEFAULT_EXTRA_VALUE));
        assertThat(checkSavedStateInSpinner(new Bundle(1), 101, AMBITO_INCIDENCIA_POSITION, viewer), is(101L));*/   // TODO
    }

    @Test
    public void testGetSelectedItemId() throws Exception
    {
        /*viewer.itemSelectedId = 93;
        assertThat(viewer.getSelectedItemId(), is(93L));*/          // TODO
    }

    @Test
    public void testDoViewInViewer_1() throws Exception
    {
        /*final String keyBundle = AMBITO_INCIDENCIA_POSITION.key;
        IncidenciaBean incidenciaBean = new IncidenciaBean();
        Bundle bundle = new Bundle();
        bundle.putLong(keyBundle, 111);

        AtomicReference<String> flagExec = doCtrlerInSpinnerViewer(viewer);
        viewer.doViewInViewer(bundle, incidenciaBean);

        // Check call to initSelectedItemId().
        assertThat(viewer.getSelectedItemId(), allOf(
                is(111L),
                is(bundle.getLong(keyBundle))
        ));
        // Check call to controller.loadDataInSpinner();
        assertThat(flagExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
        // Check call to view.setOnItemSelectedListener().
        ViewerAmbitoIncidSpinner.AmbitoIncidSelectedListener listener =
                (ViewerAmbitoIncidSpinner.AmbitoIncidSelectedListener) viewer.getViewInViewer().getOnItemSelectedListener();*/    // TODO
    }

    @Test
    public void testDoViewInViewer_2() throws Exception
    {
        IncidenciaBean incidenciaBean = new IncidenciaBean();
        incidenciaBean.setCodAmbitoIncid((short) 13);
        viewer.doViewInViewer(null, incidenciaBean);
        assertThat(viewer.getSelectedItemId(), is(13L));
    }
}