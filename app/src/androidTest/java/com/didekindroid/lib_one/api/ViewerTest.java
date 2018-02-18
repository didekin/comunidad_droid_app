package com.didekindroid.lib_one.api;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.api.router.RouterActionIf;
import com.didekindroid.lib_one.api.router.UiExceptionRouterIf;
import com.didekinlib.http.exception.ErrorBean;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.AFTER_METHOD_EXEC_A;
import static com.didekindroid.lib_one.testutil.ConstantForMethodCtrlExec.BEFORE_METHOD_EXEC;
import static com.didekindroid.lib_one.testutil.MockTestNavigation.nextMockAcLayout;
import static com.didekinlib.http.usuario.UsuarioExceptionMsg.BAD_REQUEST;
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

    AppCompatActivity activity;
    Viewer<View, ControllerIf> viewer;
    ViewerMock<View, ControllerIf> parentViewer;
    private View viewInViewer;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        parentViewer = new ViewerMock<>(new View(activity), activity);
        viewInViewer = new View(activity);
        viewer = new Viewer<View, ControllerIf>(viewInViewer, activity, parentViewer) {
            @Override
            public UiExceptionRouterIf getExceptionRouter()
            {
                return httpMsg -> (RouterActionIf) () -> ActivityNextMock.class;
            }
        };
    }

    @Test
    public void testGetActivity() throws Exception
    {
        assertThat(viewer.getActivity(), is(activity));
    }

    @Test
    public void test_OnErrorInObserver() throws Exception
    {
        UiException uiException = new UiException(new ErrorBean(BAD_REQUEST));
        activity.runOnUiThread(() -> viewer.onErrorInObserver(uiException));
        onView(withId(nextMockAcLayout)).check(matches(isDisplayed()));
    }

    @Test
    public void clearSubscriptions() throws Exception
    {
        ControllerIf controller = new Controller() {
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
    public void test_GetController() throws Exception
    {
        final ControllerIf controllerLocal = new Controller();
        viewer.setController(controllerLocal);
        assertThat(viewer.getController(), is(controllerLocal));
    }

    @Test
    public void test_GetParentViewer() throws Exception
    {
        assertThat(viewer.getParentViewer(), is(parentViewer));
    }
}