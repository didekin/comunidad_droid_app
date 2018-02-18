package com.didekindroid.lib_one.api.router;

import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.ListMockFr;
import com.didekindroid.lib_one.api.ActivityMock;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 24/11/2017
 * Time: 14:19
 */
@RunWith(AndroidJUnit4.class)
public class FragmentInitiatorTest {

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, false, true);

    AppCompatActivity activity;
    FragmentInitiator<Fragment> initiator;
    AtomicBoolean isRun;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        initiator = new FragmentInitiator<>(activity, R.id.mock_ac_layout);
        isRun = new AtomicBoolean(false);
    }

    @Test
    public void test_InitReplaceFragment() throws Exception
    {
        // Preconditions.
        Bundle bundle = new Bundle(3);
        bundle.putBoolean("testKey", true);
        Fragment mockFr = new ListMockFr();
        // Exec.
        activity.runOnUiThread(() -> {
            initiator.initReplaceFragmentTx(bundle, mockFr);
            isRun.compareAndSet(false, true);
        });
        // Check.
        waitAtMost(4, SECONDS).untilTrue(isRun);
        assertThat(mockFr.getArguments().getBoolean("testKey"), is(true));
        assertThat(activity.getSupportFragmentManager().findFragmentByTag(mockFr.getClass().getName()), notNullValue());
    }

    @Test
    public void test_InitFragment() throws Exception
    {
        // Preconditions.
        Fragment mockFr = new ListMockFr();
        // Exec.
        activity.runOnUiThread(() -> {
            initiator.initFragmentTx(mockFr);
            isRun.compareAndSet(false, true);
        });
        // Check.
        waitAtMost(4, SECONDS).untilTrue(isRun);
        assertThat(activity.getSupportFragmentManager().findFragmentByTag(mockFr.getClass().getName()), notNullValue());
    }

    @Test
    public void test_InitFragmentById() throws Exception
    {
        // Preconditions.
        Bundle bundle = new Bundle(3);
        bundle.putBoolean("mock3_key", true);
        // Exec.
        activity.runOnUiThread(() -> {
            ListMockFr fragment = new FragmentInitiator<ListMockFr>(activity).initFragmentById(bundle, R.id.list_mock_frg);
            // Check.
            assertThat(fragment.getArguments().getBoolean("mock3_key"), is(true));
        });
    }
}