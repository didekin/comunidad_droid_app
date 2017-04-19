package com.didekindroid.incidencia.core;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.Spinner;

import com.didekindroid.api.ActivityMock;
import com.didekindroid.api.ViewerMock;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.notNullValue;

/**
 * User: pedro@didekin
 * Date: 29/03/17
 * Time: 15:06
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerImportanciaSpinnerTest {

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    CtrlerImportanciaSpinner controller;

    @Before
    public void setUp()
    {
        final Activity activity = activityRule.getActivity();
        final AtomicReference<CtrlerImportanciaSpinner> atomicController = new AtomicReference<>(null);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                atomicController.compareAndSet(
                        null,
                        new CtrlerImportanciaSpinner(
                                new ViewerImportanciaSpinner(
                                        new Spinner(activity), activity, new ViewerMock<>(new View(activity), activity, null)
                                )
                        )
                );
            }
        });
        waitAtMost(2, SECONDS).untilAtomic(atomicController, notNullValue());
        controller = atomicController.get();
    }

    @Test
    public void testLoadDataInSpinner() throws Exception
    {
//        assertThat(controller.loadDataInSpinner(), is(true));  // TODO
    }

    @Test
    public void testGetSelectedFromItemId() throws Exception
    {
//       assertThat(controller.getSelectedFromItemId(12L), is(12));    // TODO
    }

}