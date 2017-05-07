package com.didekindroid.comunidad.spinner;

import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Spinner;

import com.didekindroid.R;
import com.didekindroid.api.ActivityMock;
import com.didekindroid.api.SpinnerMockFr;
import com.didekinlib.model.comunidad.Provincia;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import static com.didekindroid.comunidad.ComuBundleKey.PROVINCIA_ID;
import static com.didekindroid.comunidad.spinner.ViewerProvinciaSpinner.newViewerProvinciaSpinner;
import static com.didekindroid.comunidad.spinner.ViewerTipoViaSpinner.newViewerTipoViaSpinner;
import static com.didekindroid.testutil.ActivityTestUtils.checkSavedStateWithItemSelected;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 06/05/17
 * Time: 13:26
 */
@RunWith(AndroidJUnit4.class)
public class ViewerProvinciaSpinnerTest {

    final AtomicReference<String> flagLocalExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    ViewerProvinciaSpinner viewer;
    ActivityMock activity;
    Spinner spinner;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();

        final AtomicReference<ViewerProvinciaSpinner> atomicViewer = new AtomicReference<>(null);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                activity.getSupportFragmentManager().beginTransaction()
                        .add(R.id.mock_ac_layout, new SpinnerMockFr(), null)
                        .commitNow();
                spinner = (Spinner) activity.findViewById(R.id.provincia_spinner);
                atomicViewer.compareAndSet(null, newViewerProvinciaSpinner(spinner, activity, null));
            }
        });
        waitAtMost(2, SECONDS).untilAtomic(atomicViewer, notNullValue());
        viewer = atomicViewer.get();
    }

    @Test
    public void test_NewViewerProvinciaSpinner() throws Exception
    {
        assertThat(newViewerTipoViaSpinner(spinner, activity, null).getController(), notNullValue());
    }

    @Test
    public void test_InitSelectedItemId() throws Exception
    {
        Bundle bundle = new Bundle();

        // Case 0: no previous initialization
        viewer.initSelectedItemId(bundle);
        assertThat(viewer.getSelectedItemId(), is(0L));
        // Case 1: initialization in savedState
        bundle.putLong(PROVINCIA_ID.key, 23);
        viewer.initSelectedItemId(bundle);
        assertThat(viewer.getSelectedItemId(), is(23L));
        // Case 2: initialization in both savedState and comunidadBean
        viewer.provinciaIn = new Provincia((short) 57);
        viewer.initSelectedItemId(bundle);
        assertThat(viewer.getSelectedItemId(), is(23L));
        // Case 3: initialization in comunidadBean
        bundle = null;
        viewer.initSelectedItemId(bundle);
        assertThat(viewer.getSelectedItemId(), is(57L));
    }

    @Test
    public void test_GetSelectedPositionFromItemId() throws Exception
    {
        // TODO:
    }

    @Test
    public void test_DoViewInViewer() throws Exception
    {
        // State
        final String keyBundle = PROVINCIA_ID.key;
        Bundle bundle = new Bundle(1);
        bundle.putLong(keyBundle, 11L);

        viewer.setController(new CtrlerProvinciaSpinner(viewer) {
            @Override
            public boolean loadItemsByEntitiyId(Long... entityId)
            {
                assertThat(flagLocalExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
                return false;
            }
        });
        viewer.doViewInViewer(bundle, null);

        assertThat(viewer.provinciaIn, nullValue());
        assertThat(viewer.getSelectedItemId(), allOf(
                is(bundle.getLong(keyBundle)),
                is(11L)
        ));
        // Check NO call to controller.loadItemsByEntitiyId();
        assertThat(flagLocalExec.get(), is(BEFORE_METHOD_EXEC));
        // Check call to view.setOnItemSelectedListener().
        assertThat(viewer.getViewInViewer().getOnItemSelectedListener(), instanceOf(ViewerProvinciaSpinner.ProvinciaSelectedListener.class));
    }

    @Test
    public void test_SaveState() throws Exception
    {
        checkSavedStateWithItemSelected(viewer, PROVINCIA_ID);
    }
}