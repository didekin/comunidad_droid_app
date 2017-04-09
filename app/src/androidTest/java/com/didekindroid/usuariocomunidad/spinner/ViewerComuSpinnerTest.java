package com.didekindroid.usuariocomunidad.spinner;

import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Spinner;

import com.didekindroid.R;
import com.didekindroid.api.ActivityMock;
import com.didekindroid.api.SpinnerMockFr;
import com.didekindroid.exception.UiException;
import com.didekindroid.incidencia.core.IncidenciaBean;
import com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static com.didekindroid.comunidad.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.testutil.ActivityTestUtils.doCtrlerInSpinnerViewer;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_JUAN;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOneUser;
import static com.didekindroid.usuariocomunidad.spinner.ViewerComuSpinner.newViewerComuSpinner;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.regTwoUserComuSameUser;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 16/03/17
 * Time: 19:13
 */
@RunWith(AndroidJUnit4.class)
public class ViewerComuSpinnerTest {

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<ActivityMock>(ActivityMock.class) {
        @Override
        protected void beforeActivityLaunched()
        {
            try {
                regTwoUserComuSameUser(UserComuDataTestUtil.makeListTwoUserComu());
            } catch (IOException | UiException e) {
                fail();
            }
        }
    };

    ViewerComuSpinner viewer;
    ActivityMock activity;
    Spinner spinner;

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();

        final AtomicReference<ViewerComuSpinner> atomicViewer = new AtomicReference<>(null);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                activity.getSupportFragmentManager().beginTransaction()
                        .add(R.id.mock_ac_layout, new SpinnerMockFr(), null)
                        .commitNow();
                spinner = (Spinner) activity.findViewById(R.id.comunidad_spinner);
                atomicViewer.compareAndSet(null, newViewerComuSpinner(spinner, activity, null));
            }
        });
        waitAtMost(2, SECONDS).untilAtomic(atomicViewer, notNullValue());
        viewer = atomicViewer.get();
    }

    @After
    public void cleanUp() throws UiException
    {
        cleanOneUser(USER_JUAN);
    }

    @Test
    public void testNewViewerComuSpinner() throws Exception
    {
        ViewerComuSpinner viewer = newViewerComuSpinner(spinner, activity, null);
        assertThat(viewer, notNullValue());
        assertThat(viewer.getController(), notNullValue());
    }

    @Test
    public void testInitSelectedItemId() throws Exception
    {
        viewer.spinnerBean = new IncidenciaBean();
        Bundle savedState = new Bundle();

        viewer.initSelectedItemId(savedState);
        assertThat(viewer.getSelectedItemId(), is(0L));

        savedState = null;
        viewer.spinnerBean.setComunidadId((short) 13);
        viewer.initSelectedItemId(savedState);
        assertThat(viewer.getSelectedItemId(), is(13L));

        savedState = new Bundle();
        savedState.putLong(COMUNIDAD_ID.key, 8L);
        assertThat(viewer.spinnerBean.getComunidadId(), is(13L));
        viewer.initSelectedItemId(savedState);
        assertThat(viewer.getSelectedItemId(), is(8L));
    }

    @Test
    public void testSavedState() throws Exception
    {
        viewer.itemSelectedId = 111L;
        Bundle newBundle = new Bundle();
        viewer.saveState(newBundle);
        assertThat(newBundle.getLong(COMUNIDAD_ID.key), is(111L));
    }

    @Test
    public void testGetSelectedItem() throws Exception
    {
        viewer.itemSelectedId = 111L;
        assertThat(viewer.getSelectedItemId(), is(111L));
    }

    @Test
    public void testDoViewInViewer() throws Exception
    {
        final String keyBundle = COMUNIDAD_ID.key;
        IncidenciaBean incidenciaBean = new IncidenciaBean();
        Bundle bundleTest = new Bundle(1);
        bundleTest.putLong(keyBundle, 122L);

        AtomicReference<String> flagExec = doCtrlerInSpinnerViewer(viewer);
        viewer.doViewInViewer(bundleTest, incidenciaBean);

        // Check call to initSelectedItemId().
        assertThat(viewer.getSelectedItemId(), allOf(
                is(bundleTest.getLong(keyBundle)),
                is(122L)
        ));
        // Check call to controller.loadDataInSpinner();
        assertThat(flagExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
        // Check call to view.setOnItemSelectedListener().
        ViewerComuSpinner.ComuSelectedListener listener =
                (ViewerComuSpinner.ComuSelectedListener) viewer.getViewInViewer().getOnItemSelectedListener();
    }
}