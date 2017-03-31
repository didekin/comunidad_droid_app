package com.didekindroid.api;

import android.app.Activity;
import android.os.Bundle;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.ActivityRouter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasFlag;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * User: pedro@didekin
 * Date: 22/03/17
 * Time: 18:22
 */
@RunWith(AndroidJUnit4.class)
public class RootViewReplacerTest {

    @Rule
    public IntentsTestRule<ActivityMock> activityRule = new IntentsTestRule<>(ActivityMock.class, true, true);

    RootViewReplacer replacer;

    @Before
    public void setUp()
    {
        replacer = new RootViewReplacer(activityRule.getActivity(), new ActivityRouter() {
            @Override
            public Class<? extends Activity> getNextActivity(Class<? extends Activity> previousActivity)
            {
                return ActivityNextMock.class;
            }
        });
    }

    @Test
    public void tesReplaceRootView_A() throws Exception
    {
        Bundle bundle = new Bundle(1);
        bundle.putInt("mock_key", 121);
        replacer.replaceRootView(bundle);
        onView(withId(R.id.next_mock_ac_layout)).check(matches(isDisplayed()));
        intended(hasExtra("mock_key", 121));
    }

    @Test
    public void testReplaceRootView_B() throws Exception
    {
        Bundle bundle = new Bundle(1);
        bundle.putInt("mock_key", 12);
        replacer.replaceRootView(bundle, FLAG_ACTIVITY_NEW_TASK);
        onView(withId(R.id.next_mock_ac_layout)).check(matches(isDisplayed()));
        intended(hasExtra("mock_key", 12));
        intended(hasFlag(FLAG_ACTIVITY_NEW_TASK));
    }

}