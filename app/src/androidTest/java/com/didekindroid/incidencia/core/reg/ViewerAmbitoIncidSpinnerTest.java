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

import static com.didekindroid.incidencia.core.reg.ViewerAmbitoIncidSpinner.newViewerAmbitoIncidSpinner;
import static com.didekindroid.incidencia.utils.IncidBundleKey.AMBITO_INCIDENCIA_POSITION;
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
 * Date: 30/03/17
 * Time: 17:01
 */
@RunWith(AndroidJUnit4.class)
public class ViewerAmbitoIncidSpinnerTest {

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    ViewerAmbitoIncidSpinner viewer;
    Activity activity;
    Spinner spinner;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        spinner = (Spinner) activity.findViewById(R.id.ambito_spinner);
        final AtomicReference<ViewerAmbitoIncidSpinner> atomicViewer = new AtomicReference<>(null);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                atomicViewer.compareAndSet(null, newViewerAmbitoIncidSpinner(spinner, activity, null));
            }
        });
        waitAtMost(2, SECONDS).untilAtomic(atomicViewer, notNullValue());
        viewer = atomicViewer.get();
    }

    @Test
    public void testNewViewerAmbitoIncidSpinner() throws Exception
    {
        assertThat(newViewerAmbitoIncidSpinner(spinner, activity, null).getController(), notNullValue());
    }

    @Test
    public void testInitSelectedItemId() throws Exception
    {
        Bundle bundle = new Bundle();
        assertThat(checkInitSelectedItemId(bundle, AMBITO_INCIDENCIA_POSITION, viewer), is(LONG_DEFAULT_EXTRA_VALUE));

        bundle.putLong(AMBITO_INCIDENCIA_POSITION.key, 91);
        assertThat(checkInitSelectedItemId(bundle, AMBITO_INCIDENCIA_POSITION, viewer), is(91L));
    }

    @Test
    public void testSaveState() throws Exception
    {
        assertThat(checkSavedStateInSpinner(null, 121, AMBITO_INCIDENCIA_POSITION, viewer), is(LONG_DEFAULT_EXTRA_VALUE));
        assertThat(checkSavedStateInSpinner(new Bundle(1), 101, AMBITO_INCIDENCIA_POSITION, viewer), is(101L));
    }

    @Test
    public void testGetSelectedItemId() throws Exception
    {
        assertThat(checkGetSelectedItemId(93, AMBITO_INCIDENCIA_POSITION, viewer), is(93L));
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
        bundle.putLong(AMBITO_INCIDENCIA_POSITION.key, 111);
        checkDoSpinnerViewer(bundle, AMBITO_INCIDENCIA_POSITION, ViewerAmbitoIncidSpinner.AmbitoIncidSelectedListener.class, viewer);
    }

}