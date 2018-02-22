package com.didekindroid.lib_one.api.router;

import android.os.Bundle;
import android.support.test.espresso.intent.rule.IntentsTestRule;

import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.lib_one.api.ActivityNextMock;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.BundleMatchers.hasEntry;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtras;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasFlag;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.lib_one.testutil.MockTestConstant.nextMockAcLayout;

/**
 * User: pedro@didekin
 * Date: 13/02/2018
 * Time: 18:16
 */
public class RouterActionIfTest {

    @Rule
    public IntentsTestRule<ActivityMock> activityRule = new IntentsTestRule<>(ActivityMock.class);
    private ActivityMock activity;
    private RouterActionIf router;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        router = () -> ActivityNextMock.class;
    }

    @Test
    public void test_InitActivity() throws Exception
    {
        router.initActivity(activity);
        onView(withId(nextMockAcLayout)).check(matches(isDisplayed()));
    }

    @Test
    public void test_InitActivity1() throws Exception
    {
        Bundle bundle = new Bundle(1);
        bundle.putString("keyTest", "Value_keyTest");
        router.initActivity(activity, bundle);
        onView(withId(nextMockAcLayout)).check(matches(isDisplayed()));
        intended(hasExtras(hasEntry("keyTest", "Value_keyTest")));
    }

    @Test
    public void test_InitActivity2() throws Exception
    {
        router.initActivity(activity, null, FLAG_ACTIVITY_CLEAR_TOP);
        onView(withId(nextMockAcLayout)).check(matches(isDisplayed()));
        intended(hasFlag(FLAG_ACTIVITY_CLEAR_TOP));
    }
}