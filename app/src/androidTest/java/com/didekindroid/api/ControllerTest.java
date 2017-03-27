package com.didekindroid.api;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.didekindroid.exception.UiException;
import com.didekindroid.exception.UiExceptionIf;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.disposables.Disposable;

import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 16/03/17
 * Time: 17:42
 */
@RunWith(AndroidJUnit4.class)
public class ControllerTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    Controller controller;
    ViewerMock<View,ControllerIf> viewer;

    @Before
    public void setUp(){
        viewer = new ViewerMock<View, ControllerIf>(null, activityRule.getActivity(), null){
            @Override
            public UiExceptionIf.ActionForUiExceptionIf processControllerError(UiException ui)
            {
                assertThat(flagMethodExec.getAndSet(AFTER_METHOD_EXEC_A), is(BEFORE_METHOD_EXEC));
                return null;
            }
        };
        controller = new Controller<>(viewer);
    }

    @Test
    public void getSubscriptions() throws Exception
    {
        assertThat(controller.getSubscriptions(), allOf(
                is(controller.subscriptions),
                notNullValue()
        ));
        assertThat(controller.getSubscriptions().size(), is(0));
    }

    @Test
    public void clearSubscriptions() throws Exception
    {
        assertThat(controller.getSubscriptions().size(), is(0));
        controller.subscriptions.add(new Disposable() {
            @Override
            public void dispose()
            {
            }
            @Override
            public boolean isDisposed()
            {
                return false;
            }
        });
        assertThat(controller.getSubscriptions().size(), is(1));
        controller.clearSubscriptions();
        assertThat(controller.getSubscriptions().size(), is(0));
    }

    @Test
    public void getViewer() throws Exception
    {
        ViewerIf viewerOut = controller.getViewer();
        assertThat(controller.getViewer(), is(viewerOut));
    }

    @Test
    public void onErrorCtrl() throws Exception
    {
        controller.onErrorCtrl(new Throwable());
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }
}