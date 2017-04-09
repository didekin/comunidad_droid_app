package com.didekindroid.incidencia.core;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.Spinner;

import com.didekindroid.api.ActivityMock;
import com.didekindroid.api.ViewerMock;
import com.didekindroid.incidencia.core.AmbitoIncidValueObj;
import com.didekindroid.incidencia.core.CtrlerAmbitoIncidSpinner;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.functions.Consumer;
import io.reactivex.observers.TestObserver;

import static com.didekindroid.incidencia.core.CtrlerAmbitoIncidSpinner.ambitoIncidList;
import static com.didekindroid.incidencia.core.CtrlerAmbitoIncidSpinner.newCtrlerAmbitoIncidSpinner;
import static com.didekindroid.incidencia.core.IncidenciaDataDb.AmbitoIncidencia.AMBITO_INCID_COUNT;
import static com.didekindroid.incidencia.core.ViewerAmbitoIncidSpinner.newViewerAmbitoIncidSpinner;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceAndroidMain;
import static com.didekindroid.testutil.RxSchedulersUtils.trampolineReplaceIoScheduler;
import static io.reactivex.plugins.RxJavaPlugins.reset;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 30/03/17
 * Time: 16:31
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerAmbitoIncidSpinnerTest {

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    CtrlerAmbitoIncidSpinner controller;

    @Before
    public void setUp()
    {
        final Activity activity = activityRule.getActivity();
        final AtomicReference<CtrlerAmbitoIncidSpinner> atomicController = new AtomicReference<>(null);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                atomicController.compareAndSet(
                        null,
                        newCtrlerAmbitoIncidSpinner(
                                newViewerAmbitoIncidSpinner(
                                        new Spinner(activity), activity, new ViewerMock<>(new View(activity), activity, null)
                                )
                        )
                );
            }
        });
        waitAtMost(2, SECONDS).untilAtomic(atomicController, notNullValue());
        controller = atomicController.get();
    }

    @After
    public void clear()
    {
        controller.clearSubscriptions();
    }

    @Test
    public void testAmbitoIncidList()
    {
        ambitoIncidList(controller.getViewer()).test().assertOf(new Consumer<TestObserver<List<AmbitoIncidValueObj>>>() {
            @Override
            public void accept(TestObserver<List<AmbitoIncidValueObj>> listTestObserver) throws Exception
            {
                assertThat(listTestObserver.values().get(0).size(), is(AMBITO_INCID_COUNT));
                assertThat(listTestObserver.values().size(), is(1));
            }
        });
    }

    @Test
    public void loadDataInSpinner() throws Exception
    {
        try {
            trampolineReplaceIoScheduler();
            trampolineReplaceAndroidMain();
            assertThat(controller.loadDataInSpinner(), is(true));
        } finally {
            reset();
        }
        assertThat(controller.getSubscriptions().size(), is(1));
    }

    @Test
    public void getSelectedFromItemId() throws Exception
    {
        assertThat(controller.getSelectedFromItemId(12L), is(12));
    }

}