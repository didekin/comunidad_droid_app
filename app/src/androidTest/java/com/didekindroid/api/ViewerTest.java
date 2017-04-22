package com.didekindroid.api;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.view.View;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf;
import com.didekindroid.router.ComponentReplacerIf;
import com.didekinlib.http.ErrorBean;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasFlag;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_B;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static com.didekinlib.http.GenericExceptionMsg.BAD_REQUEST;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 16/03/17
 * Time: 17:51
 */
public class ViewerTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public IntentsTestRule<ActivityMock> activityRule = new IntentsTestRule<>(ActivityMock.class, true, true);

    Activity activity;
    Viewer<View, ControllerIf> viewer;
    ViewerMock<View, ControllerIf> parentViewer;
    private View viewInViewer;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        parentViewer = new ViewerMock<>(new View(activity), activity, null);
        viewInViewer = new View(activity);
        viewer = new ViewerForTest(viewInViewer, activity, parentViewer);
    }

    @Test
    public void testGetActivity() throws Exception
    {
        assertThat(viewer.getActivity(), is(activity));
    }

    @Test
    public void testReplaceComponent() throws Exception
    {
        assertThat(viewer, instanceOf(ComponentReplacerIf.class));
        ComponentReplacerIf rootViewReplacer = (ComponentReplacerIf) viewer;
        rootViewReplacer.replaceComponent(new Bundle(0));
        onView(withId(R.id.next_mock_ac_layout)).check(matches(isDisplayed()));
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_B));
    }

    @Test
    public void testProcessControllerError() throws Exception
    {
        UiException uiException = new UiException(
                new ErrorBean(BAD_REQUEST),
                new UiExceptionIf.UiExceptionRouterIf() {
                    @Override
                    public UiExceptionIf.ActionForUiExceptionIf getActionForException(UiException uiException)
                    {
                        return new UiExceptionIf.ActionForUiExceptionIf() {
                            @Override
                            public Class<? extends Activity> getActivityToGoClass()
                            {
                                return ActivityNextMock.class;
                            }

                            @Override // NOT USED in test.
                            public int getToastResourceId()
                            {
                                return 0;
                            }
                        };
                    }
                }
        );

        viewer.processControllerError(uiException);
        onView(withId(R.id.next_mock_ac_layout)).check(matches(isDisplayed()));
        intended(hasFlag(FLAG_ACTIVITY_NEW_TASK));
    }

    @Test
    public void clearSubscriptions() throws Exception
    {
        ControllerIf controller = new Controller(viewer) {
            @Override
            public int clearSubscriptions()
            {
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
                return 0;
            }
        };
        viewer.setController(controller);
        viewer.clearSubscriptions();
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }

    @Test
    public void getViewInViewer() throws Exception
    {
        assertThat(viewer.getViewInViewer(), is(viewInViewer));
    }

    @Test
    public void testGetController() throws Exception
    {
        final ControllerIf controllerLocal = new Controller(viewer);
        viewer.setController(controllerLocal);
        assertThat(viewer.getController(), is(controllerLocal));
    }

    @Test
    public void setController() throws Exception
    {
        testGetController();
    }

    //    ============================  HELPERS  ===================================

    static class ViewerForTest extends Viewer<View, ControllerIf> implements ComponentReplacerIf {

        protected ViewerForTest(View view, Activity activity, ViewerIf parentViewer)
        {
            super(view, activity, parentViewer);
        }

        @Override
        public void replaceComponent(@NonNull Bundle bundle)
        {
            activity.startActivity(new Intent(activity, ActivityNextMock.class));
            assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_B), is(BEFORE_METHOD_EXEC));
        }
    }
}