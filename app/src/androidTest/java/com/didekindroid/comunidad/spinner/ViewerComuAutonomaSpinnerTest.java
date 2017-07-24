package com.didekindroid.comunidad.spinner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Spinner;

import com.didekindroid.R;
import com.didekindroid.api.ActivityMock;
import com.didekindroid.api.Controller;
import com.didekindroid.api.SpinnerEventItemSelectIf;
import com.didekindroid.api.SpinnerEventListener;
import com.didekindroid.api.SpinnerTextMockFr;
import com.didekindroid.api.ViewerMock;
import com.didekinlib.model.comunidad.ComunidadAutonoma;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.comunidad.repository.ComunidadDataDb.ComunidadAutonoma.NUMBER_RECORDS;
import static com.didekindroid.comunidad.spinner.ViewerComuAutonomaSpinner.newViewerComuAutonomaSpinner;
import static com.didekindroid.comunidad.spinner.ViewerComuAutonomaSpinner.spinnerEvent_default;
import static com.didekindroid.comunidad.spinner.ViewerTipoViaSpinner.newViewerTipoViaSpinner;
import static com.didekindroid.comunidad.utils.ComuBundleKey.COMUNIDAD_AUTONOMA_ID;
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
 * Date: 05/05/17
 * Time: 10:07
 */
@RunWith(AndroidJUnit4.class)
public class ViewerComuAutonomaSpinnerTest {

    final static AtomicReference<String> flagLocalExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    ViewerComuAutonomaSpinner viewer;
    ActivityMock activity;
    Spinner spinner;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();

        final AtomicReference<ViewerComuAutonomaSpinner> atomicViewer = new AtomicReference<>(null);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                activity.getSupportFragmentManager().beginTransaction()
                        .add(R.id.mock_ac_layout, new SpinnerTextMockFr(), null)
                        .commitNow();
                spinner = (Spinner) activity.findViewById(R.id.autonoma_comunidad_spinner);
                atomicViewer.compareAndSet(null,
                        newViewerComuAutonomaSpinner(spinner, activity, new ParentViewerForTest(activity))
                );
            }
        });
        waitAtMost(2, SECONDS).untilAtomic(atomicViewer, notNullValue());
        viewer = atomicViewer.get();
    }

    @Test
    public void test_NewViewerComuAutonomaSpinner() throws Exception
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
        bundle.putLong(COMUNIDAD_AUTONOMA_ID.key, 23);
        viewer.initSelectedItemId(bundle);
        assertThat(viewer.getSelectedItemId(), is(23L));
        // Case 2: initialization in both savedState and comunidadBean
        viewer.spinnerEvent = new ComuAutonomaSpinnerEventItemSelect(new ComunidadAutonoma((short) 17));
        viewer.initSelectedItemId(bundle);
        assertThat(viewer.getSelectedItemId(), is(23L));
        // Case 3: initialization in comunidadBean
        bundle = null;
        viewer.initSelectedItemId(bundle);
        assertThat(viewer.getSelectedItemId(), is(17L));
    }

    @Test
    public void test_DoViewInViewer() throws Exception
    {
        // State
        final String keyBundle = COMUNIDAD_AUTONOMA_ID.key;
        Bundle bundle = new Bundle(1);
        bundle.putLong(keyBundle, 11L);

        viewer.setController(new CtrlerComAutonomaSpinner() {
            @Override
            public boolean loadItemsByEntitiyId(DisposableSingleObserver<List<ComunidadAutonoma>> observer, Long... entityId)
            {
                assertThat(flagLocalExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
                return false;
            }
        });
        viewer.doViewInViewer(bundle, null);

        // Check comunidadIntent is default.
        assertThat(viewer.spinnerEvent, is(spinnerEvent_default));
        // Check call to initSelectedItemId().
        assertThat(viewer.getSelectedItemId(), allOf(
                is(bundle.getLong(keyBundle)),
                is(11L)
        ));
        // Check call to controller.ItemsByEntitiyId();
        assertThat(flagLocalExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
        // Check call to view.setOnItemSelectedListener().
        assertThat(viewer.getViewInViewer().getOnItemSelectedListener(), instanceOf(ViewerComuAutonomaSpinner.ComuAutonomaSelectedListener.class));
    }

    @Test
    public void testComuAutonomaSelectedListener()
    {
        // Initial state.
        assertThat(viewer.spinnerEvent, is(spinnerEvent_default));
        assertThat(viewer.getSelectedItemId(), is(0L));

        // Action.
        viewer.doViewInViewer(new Bundle(0), new ComuAutonomaSpinnerEventItemSelect(new ComunidadAutonoma((short) 9)));
         /* doViewInViewer() --> loadItemsByEntitiyId() --> onSuccessLoadItemList() --> view.setSelection() --> ComuAutonomaSelectedListener.onItemSelected() */
        // Check
        waitAtMost(2, SECONDS).until(getAdapter(viewer.getViewInViewer()), notNullValue());
        assertThat(viewer.getViewInViewer().getCount(), is(NUMBER_RECORDS));
        // Initialize itemId.
        assertThat(viewer.getSelectedItemId(), is(9L));
        assertThat(viewer.getSelectedPositionFromItemId(viewer.getSelectedItemId()), is(9));
        // Call to SpinnerEventListener.doOnClickItemId()
        waitAtMost(3, SECONDS).untilAtomic(flagLocalExec, is(AFTER_METHOD_EXEC_B));
        flagLocalExec.compareAndSet(AFTER_METHOD_EXEC_B, BEFORE_METHOD_EXEC);
    }

    @Test
    public void test_SaveState() throws Exception
    {
        checkSavedStateWithItemSelected(viewer, COMUNIDAD_AUTONOMA_ID);
    }

    // ======================================= HELPERS ===============================================

    static class ParentViewerForTest extends ViewerMock<View, Controller> implements
            SpinnerEventListener {

        public ParentViewerForTest(AppCompatActivity activity)
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