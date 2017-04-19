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

import static com.didekindroid.incidencia.core.ViewerImportanciaSpinner.newViewerImportanciaSpinner;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_NUMBER;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 29/03/17
 * Time: 15:48
 */
@RunWith(AndroidJUnit4.class)
public class ViewerImportanciaSpinnerTest {

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    ViewerImportanciaSpinner viewer;
    ActivityMock activity;
    Spinner spinner;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();

        final AtomicReference<ViewerImportanciaSpinner> atomicViewer = new AtomicReference<>(null);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                activity.getSupportFragmentManager().beginTransaction()
                        .add(R.id.mock_ac_layout, new SpinnerMockFr(), null)
                        .commitNow();
                spinner = (Spinner) activity.findViewById(R.id.importancia_spinner);
                atomicViewer.compareAndSet(null, newViewerImportanciaSpinner(spinner, activity, null));
            }
        });
        waitAtMost(2, SECONDS).untilAtomic(atomicViewer, notNullValue());
        viewer = atomicViewer.get();
    }

    @Test
    public void tesNewViewerImportanciaSpinner() throws Exception
    {
        assertThat(newViewerImportanciaSpinner(spinner, activity, null).getController(), notNullValue());
    }

    @Test
    public void testInitSelectedItemId() throws Exception
    {
        viewer.bean = new IncidImportanciaBean();
        Bundle bundle = new Bundle();

        viewer.initSelectedItemId(bundle);
        assertThat(viewer.getSelectedItemId(), is(0L));

        bundle = null;
        viewer.bean.setImportancia((short) 1);
        viewer.initSelectedItemId(bundle);
        assertThat(viewer.getSelectedItemId(), is(1L));

        bundle = new Bundle(1);
        bundle.putLong(INCID_IMPORTANCIA_NUMBER.key, (short) 4);
        viewer.initSelectedItemId(bundle);
        assertThat(viewer.getSelectedItemId(), is(4L));
    }

    @Test
    public void testSaveState() throws Exception
    {
        /*assertThat(checkSavedStateInSpinner(null, (short) 121, INCID_IMPORTANCIA_NUMBER, viewer), is(LONG_DEFAULT_EXTRA_VALUE));
        assertThat(checkSavedStateInSpinner(new Bundle(1), (short) 101, INCID_IMPORTANCIA_NUMBER, viewer), is(101L));*/     // TODO
    }

    @Test
    public void testGetSelectedItemId() throws Exception
    {
        /*viewer.itemSelectedId = (short) 27;
        assertThat(viewer.getSelectedItemId(), is(27L));*/     // TODO
    }

    @Test
    public void testDoViewInViewer() throws Exception
    {
        /*final String keyBundle = INCID_IMPORTANCIA_NUMBER.key;
        IncidImportanciaBean incidImportanciaBean = new IncidImportanciaBean();
        Bundle bundle = new Bundle();
        bundle.putLong(keyBundle, (short) 93);

        AtomicReference<String> flagExec = doCtrlerInSpinnerViewer(viewer);
        viewer.doViewInViewer(bundle, incidImportanciaBean);

        // Check call to initSelectedItemId().
        assertThat(viewer.getSelectedItemId(), allOf(
                is(bundle.getLong(keyBundle)),
                is(93L)
        ));
        // Check call to controller.loadDataInSpinner();
        assertThat(flagExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
        // Check call to view.setOnItemSelectedListener().
        ViewerImportanciaSpinner.ImportanciaSelectedListener listener =
                (ViewerImportanciaSpinner.ImportanciaSelectedListener) viewer.getViewInViewer().getOnItemSelectedListener();*/    // TODO
    }
}