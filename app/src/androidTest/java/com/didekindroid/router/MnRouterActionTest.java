package com.didekindroid.router;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.lib_one.api.ActivityNextMock;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.support.test.runner.lifecycle.Stage.RESUMED;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.getActivitesInTaskByStage;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * User: pedro@didekin
 * Date: 06/02/2018
 * Time: 14:20
 */
@RunWith(AndroidJUnit4.class)
public class MnRouterActionTest {

    @Rule
    public IntentsTestRule<ActivityMock> activityRule = new IntentsTestRule<ActivityMock>(ActivityMock.class) {
        @Override
        protected Intent getActivityIntent()
        {
            Intent intent = new Intent();
            intent.putExtra("keyTest_2", "Value_keyTest_2");
            return intent;
        }
    };

    @Test
    public void test_InitActivity() throws Exception
    {
    }

    @Test
    public void test_DoUpMenu_1() throws Exception
    {
        ActivityMock activityMock = activityRule.getActivity();
        ActivityManager manager = (ActivityManager) activityMock.getSystemService(ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= LOLLIPOP) {
            manager.getAppTasks().get(0).startActivity(activityMock, new Intent(activityMock, ActivityNextMock.class), new Bundle(0));
            // Calling indirectly the method to test and check new activity layout.
            checkUp(R.id.mock_ac_layout);
            // Check that the up activity is resumed and has the original intent.
            Collection<Activity> activities = getActivitesInTaskByStage(RESUMED);
            assertThat(activities.size(), is(1));
            for (Activity next : activities) {
                assertThat(next.getComponentName().getClassName(), is(ActivityMock.class.getCanonicalName()));
                assertThat(next.getIntent().getStringExtra("keyTest_2"), is("Value_keyTest_2"));
            }
        }
    }

}