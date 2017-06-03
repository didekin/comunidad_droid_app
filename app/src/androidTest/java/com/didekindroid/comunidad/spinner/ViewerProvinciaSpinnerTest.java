package com.didekindroid.comunidad.spinner;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.Spinner;

import com.didekindroid.R;
import com.didekindroid.api.ActivityMock;
import com.didekindroid.api.Controller;
import com.didekindroid.api.ObserverSingleSelectList;
import com.didekindroid.api.SpinnerEventItemSelectIf;
import com.didekindroid.api.SpinnerEventListener;
import com.didekindroid.api.SpinnerMockFr;
import com.didekindroid.api.ViewerMock;
import com.didekinlib.model.comunidad.Provincia;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.comunidad.spinner.ViewerProvinciaSpinner.default_spinnerEvent;
import static com.didekindroid.comunidad.spinner.ViewerProvinciaSpinner.newViewerProvinciaSpinner;
import static com.didekindroid.comunidad.spinner.ViewerTipoViaSpinner.newViewerTipoViaSpinner;
import static com.didekindroid.comunidad.utils.ComuBundleKey.PROVINCIA_ID;
import static com.didekindroid.testutil.ActivityTestUtils.checkSavedStateWithItemSelected;
import static com.didekindroid.testutil.ActivityTestUtils.getAdapter;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_B;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 06/05/17
 * Time: 13:26
 */
@RunWith(AndroidJUnit4.class)
public class ViewerProvinciaSpinnerTest {

    static final AtomicReference<String> flagLocalExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

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
                spinner = (Spinner) activity.findViewById(R.id.provincia_mock_spinner);
                atomicViewer.compareAndSet(null, newViewerProvinciaSpinner(spinner, activity, new ParentViewerForTest(activity)));
            }
        });
        waitAtMost(2, SECONDS).untilAtomic(atomicViewer, notNullValue());
        viewer = atomicViewer.get();
    }

    @Test
    public void test_NewViewerProvinciaSpinner() throws Exception
    {
        assertThat(newViewerTipoViaSpinner(spinner, activity, null).getController(), notNullValue());
        assertThat(viewer.eventListener, isA(SpinnerEventListener.class));
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
        viewer.spinnerEvent = new ProvinciaSpinnerEventItemSelect(new Provincia((short) 57));
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
        final List<Provincia> provincias = Arrays.asList(new Provincia((short) 22, "provincia_0"), new Provincia((short) 11, "provincia_1"), new Provincia((short) 33, "provincia_2"));
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer.onSuccessLoadItemList(provincias);
                assertThat(viewer.getSelectedPositionFromItemId(11L), is(1));
                assertThat(viewer.getSelectedPositionFromItemId(22L), is(0));
                assertThat(viewer.getSelectedPositionFromItemId(33L), is(2));
                assertThat(viewer.getSelectedPositionFromItemId(122L), is(0));
            }
        });
    }

    @Test
    public void test_DoViewInViewer() throws Exception
    {
        // State
        final String keyBundle = PROVINCIA_ID.key;
        Bundle bundle = new Bundle(1);
        bundle.putLong(keyBundle, 11L);

        viewer.setController(new CtrlerProvinciaSpinner() {
            @Override
            public boolean loadItemsByEntitiyId(DisposableSingleObserver<List<Provincia>> observer, Long... entityId)
            {
                assertThat(flagLocalExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
                return false;
            }
        });
        viewer.doViewInViewer(bundle, null);

        assertThat(viewer.spinnerEvent, is(default_spinnerEvent));
        assertThat(viewer.getSelectedItemId(), allOf(
                is(bundle.getLong(keyBundle)),
                is(11L)
        ));
        // Check NO call to controller.loadItemsByEntitiyId();
        assertThat(flagLocalExec.get(), is(BEFORE_METHOD_EXEC));
        // Check call to view.setOnItemSelectedListener().
        assertThat(viewer.getViewInViewer().getOnItemSelectedListener(), instanceOf(ViewerProvinciaSpinner.ProvinciaSelectedListener.class));
    }

    /* Comunidad Autónoma: Canarias, ca_id: 5; nº de provincias: 2 */
    @Test
    public void test_ProvinciaSelectedListener()
    {
        // Initial state.
        assertThat(viewer.spinnerEvent, is(default_spinnerEvent));
        assertThat(viewer.getSelectedItemId(), is(0L));
        // Preconditions
        final Provincia provincia = new Provincia((short) 35);
        viewer.doViewInViewer(new Bundle(0), new ProvinciaSpinnerEventItemSelect(provincia));
        assertThat(viewer.spinnerEvent.getSpinnerItemIdSelect(), is((long) provincia.getProvinciaId()));
        // ItemSelectedId is initialized.
        assertThat(viewer.getSelectedItemId(), is(35L));

        // Check controller.loadItemsByEntitiyId() --> onSuccessLoadItemList() --> view.setSelection() ... {--> ProvinciaSelectedListener.onItemSelected() }
        // We initialize to 0 the itemSelectedId to chedk the call to ProvinciaSelectedListener.onItemSelected().
        viewer.setItemSelectedId(38L); // Santa Cruz de Tenerife
        viewer.getController().loadItemsByEntitiyId(new ObserverSingleSelectList<>(viewer), 5L);
        waitAtMost(3, SECONDS).until(getAdapter(viewer.getViewInViewer()), notNullValue());
        assertThat(viewer.getViewInViewer().getCount(), is(2));
        // ProvinciaSelectedListener.onItemSelected() modify spinnerEvent.
        assertThat(viewer.spinnerEvent.getSpinnerItemIdSelect(), is(38L));
        // Call to SpinnerEventListener.doOnClickItemId()
        waitAtMost(3, SECONDS).untilAtomic(flagLocalExec, is(AFTER_METHOD_EXEC_B));
        flagLocalExec.compareAndSet(AFTER_METHOD_EXEC_B, BEFORE_METHOD_EXEC);
    }

    @Test
    public void test_SaveState() throws Exception
    {
        checkSavedStateWithItemSelected(viewer, PROVINCIA_ID);
    }

    // ======================================= HELPERS ===============================================

    static class ParentViewerForTest extends ViewerMock<View, Controller> implements
            SpinnerEventListener {

        public ParentViewerForTest(Activity activity)
        {
            super(activity);
        }

        @Override
        public void doOnClickItemId(@Nullable SpinnerEventItemSelectIf spinnerEventItemSelect)
        {
            Timber.d("==================== doOnClickItemId =====================");
            assertThat(flagLocalExec.getAndSet(AFTER_METHOD_EXEC_B), is(BEFORE_METHOD_EXEC));
        }
    }
}