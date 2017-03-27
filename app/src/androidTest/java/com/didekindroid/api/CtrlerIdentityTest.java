package com.didekindroid.api;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.didekindroid.security.IdentityCacher;
import com.didekindroid.security.IdentityCacherMock;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekindroid.security.IdentityCacherMock.flagIdentityMockMethodExec;
import static com.didekindroid.testutil.ConstantExecution.IDENTITY_AFTER_IS_REGISTERED;
import static com.didekindroid.testutil.ConstantExecution.IDENTITY_AFTER_UPDATE_REGISTERED;
import static com.didekindroid.testutil.ConstantExecution.IDENTITY_FLAG_INITIAL;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 24/02/17
 * Time: 12:31
 * <p>
 * Tests for the methods implemented in Controller and CtrlerIdentity.
 */
@RunWith(AndroidJUnit4.class)
public class CtrlerIdentityTest {

    @Rule
    public ActivityTestRule<? extends Activity> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    CtrlerIdentityIf controller;
    ViewerMock<View, CtrlerIdentityIf> myViewer;
    IdentityCacher identityCacher;

    @Before
    public void setUp()
    {
        myViewer = new ViewerMock<>(null, activityRule.getActivity(), null);
        identityCacher = new IdentityCacherMock();
        controller = new CtrlerIdentity<>(myViewer);
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