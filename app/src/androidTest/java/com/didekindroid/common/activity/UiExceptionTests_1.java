package com.didekindroid.common.activity;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekin.common.exception.DidekinExceptionMsg.COMUNIDAD_NOT_FOUND;
import static com.didekin.common.exception.DidekinExceptionMsg.GENERIC_INTERNAL_ERROR;
import static com.didekin.common.exception.DidekinExceptionMsg.INCIDENCIA_USER_WRONG_INIT;
import static com.didekin.common.exception.DidekinExceptionMsg.ROLES_NOT_FOUND;
import static com.didekindroid.common.testutils.ActivityTestUtils.checkToastInTest;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 17/11/15
 * Time: 10:07
 */
@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
@RunWith(AndroidJUnit4.class)
public class UiExceptionTests_1 extends UiExceptionAbstractTest {

    private MockActivity mActivity;

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
        Thread.sleep(4000);
    }

    //  ===========================================================================

    @Test
    public void testSetUp()
    {
        assertThat(mActivity, notNullValue());
    }

    @Test
    public void testGeneric() throws Exception
    {
        final UiException ue = getUiException(GENERIC_INTERNAL_ERROR);

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                ue.processMe(mActivity, new Intent());
            }
        });

        checkToastInTest(R.string.exception_generic_message, mActivity);
        onView(withId(R.id.comu_search_ac_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void testLogin() throws Exception
    {
        final UiException ue = getUiException(ROLES_NOT_FOUND);

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                ue.processMe(mActivity, new Intent());
            }
        });

        checkToastInTest(R.string.user_without_signedUp, mActivity);
        onView(withId(R.id.login_ac_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoginIncid() throws Exception
    {
        final UiException ue = getUiException(INCIDENCIA_USER_WRONG_INIT);

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                ue.processMe(mActivity, new Intent());
            }
        });

        checkToastInTest(R.string.user_without_powers, mActivity);
        onView(withId(R.id.login_ac_layout)).check(matches(isDisplayed()));
    }

    @Test
    public void testSearchComu() throws Exception     // FAil
    {
        final UiException ue = getUiException(COMUNIDAD_NOT_FOUND);

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                ue.processMe(mActivity, new Intent());
            }
        });

        checkToastInTest(R.string.comunidad_not_found_message, mActivity);
        onView(withId(R.id.comu_search_ac_layout)).check(matches(isDisplayed()));
    }

    //  ============================== HELPERS  ===================================

}