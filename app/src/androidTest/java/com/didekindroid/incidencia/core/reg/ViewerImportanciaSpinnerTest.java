package com.didekindroid.incidencia.core.reg;

import android.app.Activity;
import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Spinner;

import com.didekindroid.R;
import com.didekindroid.api.ActivityMock;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import static com.didekindroid.incidencia.core.reg.ViewerImportanciaSpinner.newViewerImportanciaSpinner;
import static com.didekindroid.incidencia.utils.IncidBundleKey.INCID_IMPORTANCIA_NUMBER;
import static com.didekindroid.testutil.ActivityTestUtils.LONG_DEFAULT_EXTRA_VALUE;
import static com.didekindroid.testutil.ActivityTestUtils.checkDoSpinnerViewer;
import static com.didekindroid.testutil.ActivityTestUtils.checkGetSelectedItemId;
import static com.didekindroid.testutil.ActivityTestUtils.checkInitSelectedItemId;
import static com.didekindroid.testutil.ActivityTestUtils.checkSavedStateInSpinner;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

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
    Activity activity;
    Spinner spinner;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        spinner = (Spinner) activity.findViewById(R.id.importancia_spinner);
        final AtomicReference<ViewerImportanciaSpinner> atomicViewer = new AtomicReference<>(null);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                atomicViewer.compareAndSet(null, newViewerImportanciaSpinner(spinner, activity, null));
            }
        });
        waitAtMost(2, SECONDS).untilAtomic(atomicViewer, notNullValue());
        viewer = atomicViewer.get();
    }

    @Test
    public void tesNewViewerImportanciaSpinner() throws Exception
    {
        assertThat(newViewerImportanciaSpinner(spinner, activity, null).getController(), notNullValue());
    }

    @Test
    public void testInitSelectedItemId() throws Exception
    {
        Bundle bundle = new Bundle();
        assertThat(checkInitSelectedItemId(bundle, INCID_IMPORTANCIA_NUMBER, viewer), is(0L));

        bundle.putLong(INCID_IMPORTANCIA_NUMBER.key, (short) 91);
        assertThat(checkInitSelectedItemId(bundle, INCID_IMPORTANCIA_NUMBER, viewer), is(91L));
    }

    @Test
    public void testSaveState() throws Exception
    {
        assertThat(checkSavedStateInSpinner(null, (short) 121, INCID_IMPORTANCIA_NUMBER, viewer), is(LONG_DEFAULT_EXTRA_VALUE));
        assertThat(checkSavedStateInSpinner(new Bundle(1), (short) 101, INCID_IMPORTANCIA_NUMBER, viewer), is(101L));
    }

    @Test
    public void testGetSelectedItemId() throws Exception
    {
        assertThat(checkGetSelectedItemId((short) 93, INCID_IMPORTANCIA_NUMBER, viewer), is(93L));
    }

    @Test
    public void testGetItemIdInIntent() throws Exception
    {
        try {
            viewer.getItemIdInIntent();
            fail();
        } catch (UnsupportedOperationException uo) {
            assertThat(uo instanceof UnsupportedOperationException, is(true));
        }
    }

    @Test
    public void testDoViewInViewer() throws Exception
    {
        Bundle bundle = new Bundle();
        bundle.putLong(INCID_IMPORTANCIA_NUMBER.key, (short) 93);
        checkDoSpinnerViewer(bundle, INCID_IMPORTANCIA_NUMBER, ViewerImportanciaSpinner.ImportanciaSelectedListener.class, viewer);
    }
}