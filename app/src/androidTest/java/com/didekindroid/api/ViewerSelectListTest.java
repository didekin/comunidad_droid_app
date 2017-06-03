package com.didekindroid.api;

import android.os.Bundle;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 19/04/17
 * Time: 12:21
 */
public class ViewerSelectListTest {

    @Rule
    public IntentsTestRule<ActivityMock> activityRule = new IntentsTestRule<>(ActivityMock.class, true, true);

    ActivityMock activity;
    ViewerSelectList<Spinner, CtrlerSelectList<String>, String> viewer;

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        final AtomicBoolean execFlag = new AtomicBoolean(false);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer = new ViewerSelectList<Spinner, CtrlerSelectList<String>, String>(new Spinner(activity), activity, null) {
                    @Override
                    public void initSelectedItemId(Bundle savedState)
                    {
                    }
                };
                execFlag.compareAndSet(false, true);
            }
        });
        waitAtMost(2, SECONDS).untilTrue(execFlag);
    }

    @Test
    public void testGetArrayAdapterForSpinner() throws Exception
    {
        ArrayAdapter<String> adapter = viewer.getArrayAdapterForSpinner(activity);
        assertThat(adapter, notNullValue());
        assertThat(adapter.getCount(), is(0));
    }

    @Test
    public void testGetSelectedItemId() throws Exception
    {
        viewer.setItemSelectedId(111L);
        assertThat(viewer.getSelectedItemId(), is(111L));
    }

    @Test
    public void testGetSelectedViewFromItemId() throws Exception
    {
        viewer.setItemSelectedId(111L);
        assertThat(viewer.getSelectedPositionFromItemId(111L), is(111));
    }

    @Test
    public void test_OnSuccessLoadItemList() throws Exception
    {
        final List<String> stringList = Arrays.asList("string22", "string11", "string44", "string33");
        long itemSelected = 1;
        viewer.setItemSelectedId(itemSelected);

        final AtomicBoolean isExec = new AtomicBoolean(false);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewer.onSuccessLoadItemList(stringList);
                isExec.compareAndSet(false, true);
            }
        });
        waitAtMost(4, SECONDS).untilTrue(isExec);
        assertThat(viewer.getViewInViewer().getAdapter().getCount(), is(stringList.size()));
        assertThat(viewer.getViewInViewer().getSelectedItemId(), is(itemSelected));
        assertThat(viewer.getViewInViewer().getSelectedItemPosition(), is((int) itemSelected));
    }
}