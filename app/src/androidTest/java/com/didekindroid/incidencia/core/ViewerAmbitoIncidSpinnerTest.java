package com.didekindroid.incidencia.core;

import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Spinner;

import com.didekindroid.R;
import com.didekindroid.api.ActivityMock;
import com.didekindroid.api.SpinnerMockFr;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.didekindroid.incidencia.core.ViewerAmbitoIncidSpinner.newViewerAmbitoIncidSpinner;
import static com.didekindroid.incidencia.utils.IncidBundleKey.AMBITO_INCIDENCIA_POSITION;
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
 * Date: 30/03/17
 * Time: 17:01
 */
@RunWith(AndroidJUnit4.class)
public class ViewerAmbitoIncidSpinnerTest {

    final AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

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
    public void testOnSuccessLoadItems()
    {
        final List<AmbitoIncidValueObj> ambitos = new ArrayList<>(3);
        ambitos.add(new AmbitoIncidValueObj((short) 0, "ambito0"));
        ambitos.add(new AmbitoIncidValueObj((short) 1, "ambito1"));
        ambitos.add(new AmbitoIncidValueObj((short) 2, "ambito2"));
        viewer.setItemSelectedId(1);

        final AtomicBoolean isExec = new AtomicBoolean(false);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer.onSuccessLoadItems(ambitos);
                isExec.compareAndSet(false, true);
            }
        });
        waitAtMost(2, SECONDS).untilTrue(isExec);
        assertThat(viewer.getViewInViewer().getAdapter().getCount(), is(ambitos.size()));
        assertThat(ViewerAmbitoIncidSpinner.class.cast(viewer).getViewInViewer().getSelectedItemId(), is(1L));
        assertThat(ViewerAmbitoIncidSpinner.class.cast(viewer).getViewInViewer().getSelectedItemPosition(), is(1));
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
        checkSavedStateWithItemSelected(viewer, AMBITO_INCIDENCIA_POSITION);
    }

    @Test
    public void testDoViewInViewer_1() throws Exception
    {
        final String keyBundle = AMBITO_INCIDENCIA_POSITION.key;
        IncidenciaBean incidenciaBean = new IncidenciaBean();
        Bundle bundle = new Bundle();
        bundle.putLong(keyBundle, 111);

        viewer.setController(new CtrlerAmbitoIncidSpinner(viewer) {
            @Override
            public boolean loadItemsByEntitiyId(Long... entityId)
            {
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), CoreMatchers.is(BEFORE_METHOD_EXEC));
                return false;
            }
        });

        viewer.doViewInViewer(bundle, incidenciaBean);

        // Check call to initSelectedItemId().
        assertThat(viewer.getSelectedItemId(), allOf(
                is(111L),
                is(bundle.getLong(keyBundle))
        ));
        // Check call to controller.loadDataInSpinner();
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
        // Check call to view.setOnItemSelectedListener().
        ViewerAmbitoIncidSpinner.AmbitoIncidSelectedListener listener =
                (ViewerAmbitoIncidSpinner.AmbitoIncidSelectedListener) viewer.getViewInViewer().getOnItemSelectedListener();
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