package com.didekindroid.comunidad.spinner;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableSingleObserver;

import static com.didekindroid.comunidad.spinner.ViewerTipoViaSpinner.newViewerTipoViaSpinner;
import static com.didekindroid.comunidad.utils.ComuBundleKey.TIPO_VIA_ID;
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
 * Date: 03/05/17
 * Time: 15:49
 */
@RunWith(AndroidJUnit4.class)
public class ViewerTipoViaSpinnerTest {

    final AtomicReference<String> flagLocalExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    ViewerTipoViaSpinner viewer;
    ActivityMock activity;
    Spinner spinner;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();

        final AtomicReference<ViewerTipoViaSpinner> atomicViewer = new AtomicReference<>(null);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                activity.getSupportFragmentManager().beginTransaction()
                        .add(R.id.mock_ac_layout, new SpinnerMockFr(), null)
                        .commitNow();
                spinner = (Spinner) activity.findViewById(R.id.tipovia_spinner);
                atomicViewer.compareAndSet(null, newViewerTipoViaSpinner(spinner, activity, null));
            }
        });
        waitAtMost(2, SECONDS).untilAtomic(atomicViewer, notNullValue());
        viewer = atomicViewer.get();
    }

    // ==================================== TESTS ====================================

    @Test
    public void test_NewViewerTipoViaSpinner() throws Exception
    {
        assertThat(newViewerTipoViaSpinner(spinner, activity, null).getController(), notNullValue());
    }

    @Test
    public void test_GetSelectedPositionFromDesc() throws Exception
    {
        final List<TipoViaValueObj> tiposVia = Arrays.asList(new TipoViaValueObj("tipo_4"), new TipoViaValueObj("tipo_1"), new TipoViaValueObj("tipo_99"));
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                // Necesito valor inicial para evitar NullPointer en onSuccessLoadItemList(tiposVia).
                viewer.tipoViaValueObj = new TipoViaValueObj("initialDesc");
                viewer.onSuccessLoadItemList(tiposVia);
                assertThat(viewer.getSelectedPositionFromDesc("tipo_99"), is(2));
                assertThat(viewer.getSelectedPositionFromDesc("tipo_4"), is(0));
                assertThat(viewer.getSelectedPositionFromDesc("tipo_1"), is(1));
            }
        });
    }

    @Test
    public void test_OnSuccessLoadItems() throws Exception
    {
        final List<TipoViaValueObj> tiposVia = new ArrayList<>(3);
        tiposVia.add(new TipoViaValueObj(11, "tipo_11"));
        tiposVia.add(new TipoViaValueObj(22, "tipo_22"));
        tiposVia.add(new TipoViaValueObj(33, "tipo_33"));

        viewer.tipoViaValueObj = new TipoViaValueObj("tipo_22");

        final AtomicBoolean isExec = new AtomicBoolean(false);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer.onSuccessLoadItemList(tiposVia);
                isExec.compareAndSet(false, true);
            }
        });
        waitAtMost(4, SECONDS).untilTrue(isExec);
        assertThat(viewer.getViewInViewer().getAdapter().getCount(), is(tiposVia.size()));
        assertThat(viewer.getViewInViewer().getSelectedItemId(), is(1L));
        assertThat(viewer.getViewInViewer().getSelectedItemPosition(), is(1));
    }

    @Test
    public void test_InitSelectedItemId() throws Exception
    {
        Bundle bundle = new Bundle();

        // Case 0: no previous initialization
        viewer.initSelectedItemId(bundle);
        assertThat(viewer.getSelectedItemId(), is(0L));
        // Case 1: initialization in savedState
        bundle.putLong(TIPO_VIA_ID.key, 23);
        viewer.initSelectedItemId(bundle);
        assertThat(viewer.getSelectedItemId(), is(23L));
    }

    @Test
    public void test_DoViewInViewer() throws Exception
    {
        // State
        final String keyBundle = TIPO_VIA_ID.key;
        Bundle bundle = new Bundle(1);
        bundle.putLong(keyBundle, 112L);

        viewer.setController(new CtrlerTipoViaSpinner() {
            @Override
            public boolean loadItemsByEntitiyId(DisposableSingleObserver<List<TipoViaValueObj>> observer, Long... entityId)
            {
                assertThat(flagLocalExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
                return false;
            }
        });
        viewer.doViewInViewer(bundle, null);

        // Check tipoViaValueObj null.
        assertThat(viewer.tipoViaValueObj, nullValue());
        // Check call to initSelectedItemId().
        assertThat(viewer.getSelectedItemId(), allOf(
                is(bundle.getLong(keyBundle)),
                is(112L)
        ));
        // Check call to controller.loadDataInSpinner();
        assertThat(flagLocalExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
        // Check call to view.setOnItemSelectedListener().
        assertThat(viewer.getViewInViewer().getOnItemSelectedListener(), instanceOf(ViewerTipoViaSpinner.TipoViaSelectedListener.class));
    }

    @Test
    public void test_SaveState() throws Exception
    {
        checkSavedStateWithItemSelected(viewer, TIPO_VIA_ID);
    }
}