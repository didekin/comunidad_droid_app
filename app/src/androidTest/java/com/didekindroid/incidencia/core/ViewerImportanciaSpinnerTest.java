package com.didekindroid.incidencia.core;

import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.lib_one.api.SpinnerTextMockFr;
import com.didekindroid.lib_one.api.ViewerMock;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.incidencia.IncidBundleKey.INCID_IMPORTANCIA_NUMBER;
import static com.didekindroid.incidencia.core.ViewerImportanciaSpinner.newViewerImportanciaSpinner;
import static com.didekindroid.lib_one.testutil.UiTestUtil.checkSavedStateWithItemSelected;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
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

    private ViewerImportanciaSpinner viewer;
    private ActivityMock activity;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();

        activity.runOnUiThread(() -> {
            activity.getSupportFragmentManager().beginTransaction()
                    .add(R.id.mock_ac_layout, new SpinnerTextMockFr(), null)
                    .commitNow();
            viewer = newViewerImportanciaSpinner(activity.findViewById(R.id.importancia_spinner), new ViewerMock(activity));
        });
        waitAtMost(2, SECONDS).until(() -> viewer != null);
    }

    @Test
    public void tesNewViewerImportanciaSpinner()
    {
        assertThat(viewer.getController(), nullValue());
    }

    @Test
    public void testOnSuccessLoadItems()
    {
        final List<String> importancias = Arrays.asList("imp1", "imp2", "imp3", "imp4");
        viewer.setSelectedItemId(2);

        activity.runOnUiThread(() -> viewer.onSuccessLoadItemList(importancias));
        waitAtMost(4, SECONDS).until(() -> viewer.getViewInViewer().getCount() == 4);
        assertThat(ViewerImportanciaSpinner.class.cast(viewer).getViewInViewer().getSelectedItemPosition(), is(2));
    }

    @Test
    public void testInitSelectedItemId()
    {
        viewer.bean = new IncidImportanciaBean();
        Bundle bundle = new Bundle();

        viewer.initSelectedItemId(bundle);
        assertThat(viewer.getSelectedItemId(), is(0L));

        viewer.bean.setImportancia((short) 1);
        viewer.initSelectedItemId(null);
        assertThat(viewer.getSelectedItemId(), is(1L));

        bundle = new Bundle(1);
        bundle.putLong(INCID_IMPORTANCIA_NUMBER.key, (short) 4);
        viewer.initSelectedItemId(bundle);
        assertThat(viewer.getSelectedItemId(), is(4L));
    }

    @Test
    public void testSaveState()
    {
        checkSavedStateWithItemSelected(viewer, INCID_IMPORTANCIA_NUMBER);
    }

    @Test
    public void testDoViewInViewer()
    {
        short importanciaArrItem = (short) (activity.getResources().getStringArray(R.array.IncidImportanciaArray).length - 2);

        final String keyBundle = INCID_IMPORTANCIA_NUMBER.key;
        final IncidImportanciaBean incidImportanciaBean = new IncidImportanciaBean();
        final Bundle bundle = new Bundle();
        bundle.putLong(keyBundle, importanciaArrItem);

        activity.runOnUiThread(() -> viewer.doViewInViewer(bundle, incidImportanciaBean));
        waitAtMost(4, SECONDS).until(() -> viewer.bean.equals(incidImportanciaBean));

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