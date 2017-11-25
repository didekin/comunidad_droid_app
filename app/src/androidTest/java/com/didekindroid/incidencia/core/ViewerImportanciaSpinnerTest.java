package com.didekindroid.incidencia.core;

import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Spinner;

import com.didekindroid.R;
import com.didekindroid.api.ActivityMock;
import com.didekindroid.api.SpinnerTextMockFr;
import com.didekindroid.api.ViewerMock;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.incidencia.core.ViewerImportanciaSpinner.newViewerImportanciaSpinner;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_NUMBER;
import static com.didekindroid.testutil.ActivityTestUtils.checkSavedStateWithItemSelected;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
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
        activity.runOnUiThread(() -> {
            activity.getSupportFragmentManager().beginTransaction()
                    .add(R.id.mock_ac_layout, new SpinnerTextMockFr(), null)
                    .commitNow();
            spinner = activity.findViewById(R.id.importancia_spinner);
            atomicViewer.compareAndSet(null, newViewerImportanciaSpinner(spinner, new ViewerMock(activity)));
        });
        waitAtMost(2, SECONDS).untilAtomic(atomicViewer, notNullValue());
        viewer = atomicViewer.get();
    }

    @Test
    public void tesNewViewerImportanciaSpinner() throws Exception
    {
        assertThat(newViewerImportanciaSpinner(spinner, new ViewerMock(activity)).getController(), notNullValue());
    }

    @Test
    public void testOnSuccessLoadItems()
    {
        final List<String> importancias = Arrays.asList("baja", "alta", "muy alta", "urgente");
        viewer.setItemSelectedId(2);

        final AtomicBoolean isExec = new AtomicBoolean(false);
        activity.runOnUiThread(() -> {
            viewer.onSuccessLoadItemList(importancias);
            isExec.compareAndSet(false, true);
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
        short importanciaArrItem = (short) (activity.getResources().getStringArray(R.array.IncidImportanciaArray).length - 2);

        final String keyBundle = INCID_IMPORTANCIA_NUMBER.key;
        final IncidImportanciaBean incidImportanciaBean = new IncidImportanciaBean();
        final Bundle bundle = new Bundle();
        bundle.putLong(keyBundle, importanciaArrItem);

        final AtomicBoolean isRun = new AtomicBoolean(false);
        activity.runOnUiThread(() -> {
            viewer.doViewInViewer(bundle, incidImportanciaBean);
            isRun.compareAndSet(false, true);
        });
        waitAtMost(4, SECONDS).untilTrue(isRun);

        // Check call to initSelectedItemId().
        assertThat(viewer.getSelectedItemId(), allOf(
                is(bundle.getLong(keyBundle)),
                is((long) importanciaArrItem)
        ));
        // Check call to view.setOnItemSelectedListener().
        assertThat(viewer.getViewInViewer().getOnItemSelectedListener(), instanceOf(ViewerImportanciaSpinner.ImportanciaSelectedListener.class));
        // Check importanciaSpinner data are shown.
        onView(allOf(
                withId(R.id.app_spinner_1_dropdown_item),
                withParent(withId(R.id.importancia_spinner)),
                withText(activity.getResources().getStringArray(R.array.IncidImportanciaArray)[importanciaArrItem])
        )).check(matches(isDisplayed()));
    }
}