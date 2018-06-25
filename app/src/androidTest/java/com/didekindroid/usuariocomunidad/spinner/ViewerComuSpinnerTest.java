package com.didekindroid.usuariocomunidad.spinner;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Spinner;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.lib_one.api.CtrlerSelectListIf;
import com.didekindroid.lib_one.api.SpinnerEventItemSelectIf;
import com.didekindroid.lib_one.api.SpinnerEventListener;
import com.didekindroid.lib_one.api.SpinnerTextMockFr;
import com.didekindroid.lib_one.api.ViewerMock;
import com.didekinlib.model.comunidad.Comunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.observers.DisposableSingleObserver;
import timber.log.Timber;

import static com.didekindroid.comunidad.util.ComuBundleKey.COMUNIDAD_ID;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.AFTER_METHOD_EXEC_A;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.AFTER_METHOD_EXEC_B;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.BEFORE_METHOD_EXEC;
import static com.didekindroid.lib_one.testutil.UiTestUtil.checkSavedStateWithItemSelected;
import static com.didekindroid.lib_one.usuario.UserTestData.USER_JUAN;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOneUser;
import static com.didekindroid.usuariocomunidad.spinner.ViewerComuSpinner.newViewerComuSpinner;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.makeListTwoUserComu;
import static com.didekindroid.usuariocomunidad.testutil.UserComuTestData.regTwoUserComuSameUser;
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
 * Date: 16/03/17
 * Time: 19:13
 */
@RunWith(AndroidJUnit4.class)
public class ViewerComuSpinnerTest {

    private final AtomicReference<String> flagLocalExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<ActivityMock>(ActivityMock.class) {
        @Override
        protected void beforeActivityLaunched()
        {
            regTwoUserComuSameUser(makeListTwoUserComu());
        }
    };

    private ViewerComuSpinner viewer = null;
    private ActivityMock activity;
    private Spinner spinner;

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        activity.runOnUiThread(() -> {
            activity.getSupportFragmentManager().beginTransaction()
                    .add(R.id.mock_ac_layout, new SpinnerTextMockFr(), null)
                    .commitNow();
            spinner = activity.findViewById(R.id.comunidad_spinner);
            viewer = newViewerComuSpinner(spinner, new ViewerForTest(activity));
        });
        waitAtMost(2, SECONDS).until(() -> viewer != null);
    }

    @After
    public void cleanUp()
    {
        cleanOneUser(USER_JUAN.getUserName());
    }

    // ==================================== ViewerSelectListIf ====================================

    @Test
    public void testNewViewerComuSpinner()
    {
        assertThat(CtrlerComuSpinner.class.cast(viewer.getController()), notNullValue());
    }

    @Test
    public void testInitSelectedItemId()
    {
        // else...
        viewer.spinnerEvent = null;
        viewer.initSelectedItemId(null);
        assertThat(viewer.getSelectedItemId(), is(0L)); // Default initialization.
    }

    @Test
    public void testGetSelectedPositionFromItemId()
    {
        List<Comunidad> comunidades = Arrays.asList(
                new Comunidad.ComunidadBuilder().c_id(11L).build(),
                new Comunidad.ComunidadBuilder().c_id(33L).build(),
                new Comunidad.ComunidadBuilder().c_id(22L).build()
        );

        viewer.setSelectedItemId(22L);
        activity.runOnUiThread(() -> {
            viewer.onSuccessLoadItemList(comunidades);
            // Exec and check.
            assertThat(viewer.getSelectedPositionFromItemId(viewer.getBeanIdFunction()), is(2));   // id 22
        });
    }

    @Test
    public void testComuSelectedListener()
    {
        // Initial state:
        assertThat(viewer.spinnerEvent, nullValue());
        assertThat(viewer.getSelectedItemId(), is(0L));

        // Action.
        viewer.doViewInViewer(new Bundle(0), null);
        /* doViewInViewer() --> loadItemsByEntitiyId() --> onSuccessLoadItemList() --> view.setSelection() --> ComuSelectedListener.onItemSelected() */
        // Check
        waitAtMost(8, SECONDS).until(() -> viewer.getViewInViewer().getAdapter() != null);
        assertThat(viewer.getViewInViewer().getCount(), is(2));
        // Initialize itemId.
        waitAtMost(8, SECONDS).until(() -> viewer.getSelectedItemId() > 1);
        assertThat(viewer.getSelectedPositionFromItemId(viewer.getBeanIdFunction()), is(0));
        // Initialize comunidadId in spinnerEvent.
        assertThat(viewer.spinnerEvent.getSpinnerItemIdSelect(), is(viewer.getSelectedItemId()));
        // Call to SpinnerEventListener.doOnClickItemId()
        assertThat(flagLocalExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_B));
    }

    // ==================================== ViewerIf ====================================

    /* Case: no viewBean to initialize spinnerEvent. */
    @Test
    public void testDoViewInViewer_1()
    {
        final String keyBundle = COMUNIDAD_ID.key;
        Bundle bundleTest = new Bundle(1);
        bundleTest.putLong(keyBundle, 122L);

        viewer.setController(new CtrlerComuSpinner() {
            @Override
            public boolean loadItemsByEntitiyId(DisposableSingleObserver<List<Comunidad>> observer, Long... entityId)
            {
                assertThat(flagLocalExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
                return false;
            }
        });
        viewer.doViewInViewer(bundleTest, null);

        // Check initSelectedItemId(): if (savedState != null && savedState.containsKey(COMUNIDAD_ID.key) && savedState.getLong(COMUNIDAD_ID.key) > 0)
        assertThat(viewer.getSelectedItemId(), allOf(
                is(bundleTest.getLong(keyBundle)),
                is(122L)
        ));
        // Check call to controller.loadDataInSpinner();
        assertThat(flagLocalExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
        // Check call to view.setOnItemSelectedListener().
        assertThat(viewer.getViewInViewer().getOnItemSelectedListener(), instanceOf(ViewerComuSpinner.ComuSelectedListener.class));
    }


    /* Case: spinnerEvent is initialized from a Comunidad instance. */
    @Test
    public void testDoViewInViewer_2()
    {
        viewer.doViewInViewer(null, new Comunidad.ComunidadBuilder().c_id(111L).build());
        assertThat(viewer.spinnerEvent.getSpinnerItemIdSelect(), is(111L));

        // Check initSelectedItemId(): else if (spinnerEvent != null && spinnerEvent.getSpinnerItemIdSelect() > 0)
        assertThat(viewer.getSelectedItemId(), is(111L));
    }

    @Test
    public void testSavedState()
    {
        checkSavedStateWithItemSelected(viewer, COMUNIDAD_ID);
    }

    // ======================================= HELPERS ===============================================

    class ViewerForTest extends ViewerMock<View, CtrlerSelectListIf> implements
            SpinnerEventListener {

        ViewerForTest(AppCompatActivity activity)
        {
            super(activity);
        }

        @Override
        public void doOnClickItemId(@NonNull SpinnerEventItemSelectIf spinnerEventsItemSelect)
        {
            Timber.d("==================== doOnClickItemId =====================");
            assertThat(flagLocalExec.getAndSet(AFTER_METHOD_EXEC_B), is(BEFORE_METHOD_EXEC));
        }
    }
}