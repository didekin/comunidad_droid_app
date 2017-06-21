package com.didekindroid.router;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.didekindroid.R;
import com.didekindroid.api.ActivityMock;
import com.didekindroid.api.ActivityNextMock;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasFlag;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.not;

/**
 * User: pedro@didekin
 * Date: 22/03/17
 * Time: 18:22
 */
@RunWith(AndroidJUnit4.class)
public class ActivityInitiatorTest {

    @Rule
    public IntentsTestRule<ActivityMock> activityRule = new IntentsTestRule<ActivityMock>(ActivityMock.class) {
        @Override
        protected Intent getActivityIntent()
        {
            Intent intent = new Intent();
            intent.putExtra("keyTest_1", "Value_keyTest_1");
            return intent;
        }
    };

    ActivityInitiator activityInitiator;
    Activity activity;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        activityInitiator = new ActivityInitiator(activity, new ActivityRouterIf() {

            @Override
            public Class<? extends Activity> nextActivityFromMn(int resourceId)
            {
                return ActivityMock.class;
            }

            @Override
            public Class<? extends Activity> nextActivity(Class<? extends Activity> previousActivity)
            {
                return ActivityNextMock.class;
            }

            @Override
            public Class<? extends Activity> nextActivityFromClick(Class<? extends View.OnClickListener> clickListener)
            {
                return null;
            }
        });
    }

    @Test
    public void testInitAcFromMenuKeepIntent() throws Exception
    {
        activityInitiator.initAcFromMnKeepIntent(1);
        onView(withId(R.id.mock_ac_layout)).check(matches(isDisplayed()));
        intended(hasExtra("keyTest_1", "Value_keyTest_1"));
    }

    @Test
    public void test_InitAcFromMnNewIntent() throws Exception
    {
        activityInitiator.initAcFromMnNewIntent(1);
        onView(withId(R.id.mock_ac_layout)).check(matches(isDisplayed()));
        // Check for NO extra.
        intended(not(hasExtra("keyTest_1", "Value_keyTest_1")));
    }

    @Test
    public void test_InitActivityWithBundle1() throws Exception
    {
        Bundle bundle = new Bundle(1);
        bundle.putInt("mock_key", 121);
        activityInitiator.initAcWithBundle(bundle);
        onView(withId(R.id.next_mock_ac_layout)).check(matches(isDisplayed()));
        intended(hasExtra("mock_key", 121));
    }

    @Test
    public void test_InitActivityWithBundle2() throws Exception
    {
        activityInitiator.initAcWithBundle(null, ActivityNextMock.class);
        onView(withId(R.id.next_mock_ac_layout)).check(matches(isDisplayed()));
        intended(hasComponent("com.didekindroid.api.ActivityNextMock"));
    }

    @Test
    public void testInitActivityWithFlag() throws Exception
    {
        Bundle bundle = new Bundle(1);
        bundle.putInt("mock_key", 12);
        activityInitiator.initAcWithFlag(bundle, FLAG_ACTIVITY_NEW_TASK);
        onView(withId(R.id.next_mock_ac_layout)).check(matches(isDisplayed()));
        intended(hasExtra("mock_key", 12));
        intended(hasFlag(FLAG_ACTIVITY_NEW_TASK));
    }

}