package com.didekindroid;

import com.didekindroid.ManagerIf.ControllerIf;
import com.didekindroid.security.IdentityCacher;

import org.junit.Test;

import static com.didekindroid.testutil.ActivityTestUtils.addSubscription;
import static com.didekindroid.testutil.ActivityTestUtils.testClearCtrlSubscriptions;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 24/02/17
 * Time: 12:31
 */
public abstract class ControllerAbsTest<T extends ControllerIf> {

    protected T controller;

    protected abstract void testProcessReactorError();

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
        IdentityCacher cacher = ((ControllerAbs) controller).getIdentityCacher();
        cacher.updateIsRegistered(true);
        assertThat(controller.isRegisteredUser(), is(true));
        cacher.updateIsRegistered(false);
        assertThat(controller.isRegisteredUser(), is(false));
    }
}