package com.didekindroid.usuario.delete;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.didekindroid.ManagerIf.ViewerIf;
import com.didekindroid.ViewerDumbImp;
import com.didekindroid.testutil.MockActivity;
import com.didekindroid.usuario.delete.ControllerDeleteMeIf.ReactorDeleteMeIf;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 23/02/17
 * Time: 12:47
 */
@RunWith(AndroidJUnit4.class)
public class ControllerDeleteMeTest {

    final static AtomicInteger flagForExecution = new AtomicInteger(0);

    @Rule
    public ActivityTestRule<MockActivity> activityRule = new ActivityTestRule<>(MockActivity.class, true, true);

    ControllerDeleteMeIf controller;

    @Before
    public void setUp()
    {
        controller = new ControllerDeleteMe(new ViewerForTest(activityRule.getActivity()), doReactor());
    }

    @Test
    public void testUnregisterUser() throws Exception
    {
        assertThat(controller.unregisterUser(), is(false));
        assertThat(flagForExecution.getAndSet(0), is(77));

    }

    @Test
    public void testProcessBackDeleteMeRemote() throws Exception
    {
        controller.processBackDeleteMeRemote(true);
        assertThat(flagForExecution.getAndSet(0), is(12));
    }

    //  ============================================================================================
    //    .................................... HELPERS .................................
    //  ============================================================================================

    ReactorDeleteMeIf doReactor()
    {
        return new ReactorDeleteMeIf() {
            @Override
            public boolean deleteMeInRemote(ControllerDeleteMeIf controller)
            {
                assertThat(flagForExecution.getAndSet(77), is(0));
                return false;
            }
        };
    }

    class ViewerForTest extends ViewerDumbImp<View, Object> implements ViewerIf<View, Object> {

        public ViewerForTest(Activity activity)
        {
            super(activity);
            viewInViewer = new View(activity);
        }

        @Override
        public View doViewInViewer(Activity activity)
        {
            return new View(activity);
        }

        @Override
        public void replaceView(Object initParams)
        {
            assertThat(flagForExecution.getAndSet(12), is(0));
        }
    }
}