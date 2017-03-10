package com.didekindroid.api;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.view.View;

import com.didekindroid.api.ActivityMock;
import com.didekindroid.api.ControllerIdentityAbs;
import com.didekindroid.api.ManagerIf;
import com.didekindroid.api.ManagerMock;
import com.didekindroid.api.ViewerMock;
import com.didekindroid.incidencia.list.ManagerIncidSeeIf;
import com.didekindroid.security.IdentityCacher;
import com.didekindroid.security.IdentityCacherMock;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.didekindroid.api.ViewerMock.flagViewerMockMethodExec;
import static com.didekindroid.security.IdentityCacherMock.flagIdentityMockMethodExec;
import static com.didekindroid.testutil.ActivityTestUtils.addSubscription;
import static com.didekindroid.testutil.ConstantExecution.IDENTITY_AFTER_IS_REGISTERED;
import static com.didekindroid.testutil.ConstantExecution.IDENTITY_AFTER_UPDATE_REGISTERED;
import static com.didekindroid.testutil.ConstantExecution.IDENTITY_FLAG_INITIAL;
import static com.didekindroid.testutil.ConstantExecution.VIEWER_AFTER_ERROR_CONTROL;
import static com.didekindroid.testutil.ConstantExecution.VIEWER_FLAG_INITIAL;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 24/02/17
 * Time: 12:31
 * <p>
 * Tests for the methods implemented in ControllerAbs and ControllerIdentityAbs.
 */
public class ControllerIdentityAbsTest {

    @Rule
    public ActivityTestRule<? extends Activity> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    protected ManagerIf.ControllerIdentityIf controller;
    ViewerMock<View, Object> myViewer;
    IdentityCacher identityCacher;

    @Before
    public void setUp()
    {
        final ManagerIf<Object> manager = new ManagerMock<>(activityRule.getActivity());
        myViewer = new ViewerMock<>(manager);
        identityCacher = new IdentityCacherMock();

        controller = new ControllerIdentityAbs() {
            @Override
            public ManagerIncidSeeIf.ViewerIf<View, Object> getViewer()
            {
                return myViewer;
            }
        };
    }

    @Test
    public void testGetViewer()
    {
        assertThat(controller.getViewer(), CoreMatchers.<ManagerIf.ViewerIf>is(myViewer));
    }

    @Test
    public void testProcessReactorError()
    {
        controller.processReactorError(new Throwable());
        assertThat(flagViewerMockMethodExec.getAndSet(VIEWER_FLAG_INITIAL), is(VIEWER_AFTER_ERROR_CONTROL));
    }

    @Test
    public void testGetSubscriptions() throws Exception
    {
        addSubscription(controller);
        assertThat(controller.getSubscriptions().size(), is(1));
    }

    @Test
    public void testClearSubscriptions() throws Exception
    {
        addSubscription(controller);
        assertThat(controller.clearSubscriptions(), is(0));
    }

    @Test
    public void testIsRegisteredUser() throws Exception
    {
        controller.updateIsRegistered(true);
        assertThat(controller.isRegisteredUser(), is(true));
        assertThat(flagIdentityMockMethodExec.getAndSet(IDENTITY_FLAG_INITIAL), is(IDENTITY_AFTER_IS_REGISTERED));
    }

    @Test
    public void testUpdateIsRegistered()
    {
        controller.updateIsRegistered(false);
        assertThat(controller.isRegisteredUser(), is(false));
        assertThat(flagIdentityMockMethodExec.getAndSet(IDENTITY_FLAG_INITIAL), is(IDENTITY_AFTER_UPDATE_REGISTERED));
    }

    @Test
    public void testGetIdentityCacher()
    {
        assertThat(controller.getIdentityCacher(), allOf(
                notNullValue(),
                is(identityCacher)
        ));
        assertThat(flagIdentityMockMethodExec.getAndSet(IDENTITY_FLAG_INITIAL), is(IDENTITY_AFTER_IS_REGISTERED));
    }
}