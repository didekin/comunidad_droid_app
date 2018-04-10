package com.didekindroid.usuariocomunidad.spinner;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.lib_one.api.CtrlerSelectListIf;
import com.didekindroid.lib_one.api.SpinnerEventItemSelectIf;
import com.didekindroid.lib_one.api.SpinnerEventListener;
import com.didekindroid.lib_one.api.SpinnerTextMockFr;
import com.didekindroid.lib_one.api.ViewerMock;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekinlib.model.comunidad.Comunidad;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
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
import static org.junit.Assert.fail;

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
            try {
                regTwoUserComuSameUser(makeListTwoUserComu());
            } catch (IOException | UiException e) {
                fail();
            }
        }
    };

    private ViewerComuSpinner viewer;
    private AtomicReference<ViewerComuSpinner> atomicViewer;
    private ActivityMock activity;
    private Spinner spinner;

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();

        atomicViewer = new AtomicReference<>(null);
        activity.runOnUiThread(() -> {
            activity.getSupportFragmentManager().beginTransaction()
                    .add(R.id.mock_ac_layout, new SpinnerTextMockFr(), null)
                    .commitNow();
            spinner = activity.findViewById(R.id.comunidad_spinner);
            atomicViewer.compareAndSet(null, newViewerComuSpinner(spinner, new ViewerForTest(activity)));
        });
        waitAtMost(2, SECONDS).untilAtomic(atomicViewer, notNullValue());
        viewer = atomicViewer.get();
    }

    @After
    public void cleanUp() throws UiException
    {
        cleanOneUser(USER_JUAN);
    }

    // ======================================= TESTS ===============================================

    @Test
    public void testNewViewerComuSpinner()
    {
        assertThat(CtrlerComuSpinner.class.cast(viewer.getController()), notNullValue());
    }

    @Test
    public void testOnSuccessLoadItems()
    {
        viewer.setSelectedItemId(33L);
        execLoadItems();

        assertThat(viewer.getViewInViewer().getAdapter().getCount(), is(3));
        assertThat(ViewerComuSpinner.class.cast(viewer).getViewInViewer().getSelectedItemId(), is((long) viewer.getSelectedPositionFromItemId(33L)));
        // ListView.getSelectedItemId() returns the same as ListView.getSelectedItemPosition(), not the c_Id.
        assertThat(ViewerComuSpinner.class.cast(viewer).getViewInViewer().getSelectedItemId(), is(1L));
        assertThat(ViewerComuSpinner.class.cast(viewer).getViewInViewer().getSelectedItemPosition(), is(1));
        // To get the itemId:
        assertThat(((Comunidad) viewer.getViewInViewer().getItemAtPosition(0)).getC_Id(), is(11L));
    }

    @Test
    public void testInitSelectedItemId()
    {
        // Preconditions: savedState != null  && COMUNIDAD_ID.key == 0 && spinnerEvent != null && spinnerEvent.getSpinnerItemIdSelect() == 0.
        Bundle savedState = new Bundle();
        savedState.putLong(COMUNIDAD_ID.key, 0L);
        viewer.spinnerEvent = new ComuSpinnerEventItemSelect();
        assertThat(viewer.spinnerEvent.getSpinnerItemIdSelect() == 0L, is(true));
        viewer.initSelectedItemId(savedState);
        assertThat(viewer.getSelectedItemId(), is(0L)); // Default initialization.
        // Preconditions: savedState != null  && COMUNIDAD_ID.key == 0 && spinnerEvent != null && spinnerEvent.getSpinnerItemIdSelect() > 0.
        viewer.spinnerEvent = new ComuSpinnerEventItemSelect(new Comunidad.ComunidadBuilder().c_id(11L).build());
        assertThat(viewer.spinnerEvent.getSpinnerItemIdSelect() > 0L, is(true));
        viewer.initSelectedItemId(savedState);
        assertThat(viewer.getSelectedItemId(), is(11L)); // spinnerEvent initialization.
        // Preconditions: savedState != null  && COMUNIDAD_ID.key > 0 && spinnerEvent != null && spinnerEvent.getSpinnerItemIdSelect() > 0.
        savedState.putLong(COMUNIDAD_ID.key, 22L);
        assertThat(viewer.spinnerEvent.getSpinnerItemIdSelect() > 0L, is(true));
        viewer.initSelectedItemId(savedState);
        assertThat(viewer.getSelectedItemId(), is(22L)); // savedState initialization.
    }

    @Test
    public void testSavedState()
    {
        checkSavedStateWithItemSelected(viewer, COMUNIDAD_ID);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetSelectedPositionFromItemId()
    {
        activity.runOnUiThread(() -> {
            viewer.onSuccessLoadItemList(makeListComu());
            assertThat(viewer.getSelectedPositionFromItemId(33L), is(1));
            assertThat(viewer.getSelectedPositionFromItemId(22L), is(2));
            assertThat(viewer.getSelectedPositionFromItemId(122L), is(0));
        });
    }

    @Test
    public void testDoViewInViewer()
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

        // Check call to initSelectedItemId().
        assertThat(viewer.getSelectedItemId(), allOf(
                is(bundleTest.getLong(keyBundle)),
                is(122L)
        ));
        // Check call to controller.loadDataInSpinner();
        assertThat(flagLocalExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
        // Check call to view.setOnItemSelectedListener().
        assertThat(viewer.getViewInViewer().getOnItemSelectedListener(), instanceOf(ViewerComuSpinner.ComuSelectedListener.class));
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
        waitAtMost(10, SECONDS).until((Callable<Adapter>) ((AdapterView<? extends Adapter>) viewer.getViewInViewer())::getAdapter, notNullValue());
        assertThat(viewer.getViewInViewer().getCount(), is(2));
        // Initialize itemId.
        AtomicBoolean isSelectedOne = new AtomicBoolean(false);
        isSelectedOne.compareAndSet(false, viewer.getSelectedItemId() > 1);
        waitAtMost(10, SECONDS).untilTrue(isSelectedOne);
        assertThat(viewer.getSelectedPositionFromItemId(viewer.getSelectedItemId()), is(0));
        // Initialize comunidadId in spinnerEvent.
        assertThat(viewer.spinnerEvent.getSpinnerItemIdSelect(), is(viewer.getSelectedItemId()));
        // Call to SpinnerEventListener.doOnClickItemId()
        assertThat(flagLocalExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_B));
    }

    // ======================================= HELPERS ===============================================

    @NonNull
    private List<Comunidad> makeListComu()
    {
        final List<Comunidad> comunidades = new ArrayList<>(3);
        comunidades.add(new Comunidad.ComunidadBuilder().c_id(11L).build());
        comunidades.add(new Comunidad.ComunidadBuilder().c_id(33L).build());
        comunidades.add(new Comunidad.ComunidadBuilder().c_id(22L).build());
        return comunidades;
    }

    private void execLoadItems()
    {
        final AtomicBoolean isExec = new AtomicBoolean(false);
        activity.runOnUiThread(() -> {
            viewer.onSuccessLoadItemList(makeListComu());
            isExec.compareAndSet(false, true);
        });
        waitAtMost(2, SECONDS).untilTrue(isExec);
    }

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