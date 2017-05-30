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

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.didekindroid.incidencia.core.ViewerImportanciaSpinner.newViewerImportanciaSpinner;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_NUMBER;
import static com.didekindroid.testutil.ActivityTestUtils.checkSavedStateWithItemSelected;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
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

    final AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

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
    public void testOnSuccessLoadItems()
    {
        final List<String> importancias = Arrays.asList("baja", "alta", "muy alta", "urgente");
        viewer.setItemSelectedId(2);

        final AtomicBoolean isExec = new AtomicBoolean(false);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer.onSuccessLoadItemList(importancias);
                isExec.compareAndSet(false, true);
            }
        });
        waitAtMost(2, SECONDS).untilTrue(isExec);
        assertThat(viewer.getViewInViewer().getAdapter().getCount(), is(4));
        assertThat(ViewerImportanciaSpinner.class.cast(viewer).getViewInViewer().getSelectedItemId(), is(2L));
        assertThat(ViewerImportanciaSpinner.class.cast(viewer).getViewInViewer().getSelectedItemPosition(), is(2));
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
        checkSavedStateWithItemSelected(viewer, INCID_IMPORTANCIA_NUMBER);
    }

    @Test
    public void testDoViewInViewer() throws Exception
    {
        final String keyBundle = INCID_IMPORTANCIA_NUMBER.key;
        IncidImportanciaBean incidImportanciaBean = new IncidImportanciaBean();
        Bundle bundle = new Bundle();
        bundle.putLong(keyBundle, (short) 93);
        viewer.doViewInViewer(bundle, incidImportanciaBean);

        // Check call to initSelectedItemId().
        assertThat(viewer.getSelectedItemId(), allOf(
                is(bundle.getLong(keyBundle)),
                is(93L)
        ));
        // Check call to view.setOnItemSelectedListener().
        ViewerImportanciaSpinner.ImportanciaSelectedListener listener =
                (ViewerImportanciaSpinner.ImportanciaSelectedListener) viewer.getViewInViewer().getOnItemSelectedListener();
        // Check importanciaSpinner data are shown.
        // TODO: chech with onView and onDat of the spinner.
    }
}