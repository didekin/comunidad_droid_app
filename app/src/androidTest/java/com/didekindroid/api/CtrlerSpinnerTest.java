package com.didekindroid.api;

import android.app.Activity;
import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 22/03/17
 * Time: 18:23
 */
public class CtrlerSpinnerTest {

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    AtomicReference<CtrlerSpinner<Long>> controller = new AtomicReference<>(null);
    Activity activity;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                controller.compareAndSet(null, new CtrlerSpinner<Long>(new ViewerSelectableForTest(new Spinner(activity), activity)) {
                    @Override
                    public boolean loadDataInSpinner()
                    {
                        return false;
                    }

                    @Override
                    public int getSelectedFromItemId(long itemId)
                    {
                        return 2;   // Value used in test.
                    }
                });
            }
        });
    }

    @Test
    public void testOnSuccessLoadDataInSpinner() throws Exception
    {
        List<Long> idsList = Arrays.asList(12L, 13L, 21L, 31L);
        waitAtMost(2, TimeUnit.SECONDS).untilAtomic(controller, notNullValue());
        assertThat(controller.get().getSpinnerAdapter().getCount(), is(0));
        controller.get().onSuccessLoadDataInSpinner(idsList);
        assertThat(controller.get().getSpinnerView().getAdapter(), CoreMatchers.<SpinnerAdapter>is(controller.get().getSpinnerAdapter()));
        assertThat(controller.get().getSpinnerAdapter().getCount(), is(4));
        assertThat(controller.get().getSpinnerView().getSelectedItemPosition(), is(2));
    }

    //    .................................... HELPERS .................................


    static class ViewerSelectableForTest extends Viewer<Spinner, CtrlerSpinnerIf> implements
            ViewerSelectableIf<Spinner, CtrlerSpinnerIf> {

        protected ViewerSelectableForTest(Spinner view, Activity activity)
        {
            super(view, activity, null);
        }

        @Override
        public void initSelectedItemId(Bundle savedState)
        {
        }

        @Override
        public long getSelectedItemId()
        {
            return 0;
        }
    }
}