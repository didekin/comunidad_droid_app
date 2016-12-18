package com.didekinaar.exception;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import com.didekin.common.exception.ErrorBean;
import com.didekinaar.R;
import com.didekinaar.mock.MockActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static com.didekin.common.exception.DidekinExceptionMsg.ROLES_NOT_FOUND;
import static com.didekin.common.exception.DidekinExceptionMsg.USER_DATA_NOT_MODIFIED;
import static com.didekinaar.security.TokenHandler.TKhandler;
import static com.didekinaar.testutil.AarActivityTestUtils.checkToastInTest;
import static com.didekinaar.utils.UIutils.isRegisteredUser;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 17/11/15
 * Time: 10:07
 */
@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
@RunWith(AndroidJUnit4.class)
public class UiAarExceptionTests {

    MockActivity mActivity;

    @Rule
    public IntentsTestRule<MockActivity> intentRule = new IntentsTestRule<MockActivity>(MockActivity.class) {

        @Override
        protected Intent getActivityIntent()
        {
            return super.getActivityIntent();
        }

        @Override
        protected void beforeActivityLaunched()
        {
           super.beforeActivityLaunched();
        }
    };

    @BeforeClass
    public static void slowSeconds() throws InterruptedException
    {
        Thread.sleep(4000);
    }

    @Before
    public void setUp() throws Exception
    {
        mActivity = intentRule.getActivity();
    }

    @After
    public void tearDown() throws Exception
    {
        Thread.sleep(2000);
    }

    //  ===========================================================================

    @Test
    public void testSetUp()
    {
        assertThat(mActivity, notNullValue());
    }

    @Test
    public void testLogin() throws Exception
    {
        final UiException ue = new UiException(new ErrorBean(ROLES_NOT_FOUND));

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                ue.processMe(mActivity, new Intent());
            }
        });

        checkToastInTest(R.string.user_without_signedUp, mActivity);
        onView(ViewMatchers.withId(R.id.login_ac_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void testUserDataAc() throws Exception
    {
        // Preconditions.
        assertThat(isRegisteredUser(mActivity), is(true));
        assertThat(TKhandler.getTokenInCache(), notNullValue());

        final UiException ue = new UiException(new ErrorBean(USER_DATA_NOT_MODIFIED));
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                ue.processMe(mActivity, new Intent());
            }
        });

        checkToastInTest(R.string.user_data_not_modified_msg, mActivity);
        onView(ViewMatchers.withId(R.id.user_data_ac_layout)).check(matches(isDisplayed()));
    }

    //  ============================== HELPERS  ===================================

}