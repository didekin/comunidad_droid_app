package com.didekindroid.api;

import android.app.Activity;
import android.os.Bundle;
import android.support.test.rule.ActivityTestRule;
import android.widget.Spinner;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import static com.didekindroid.testutil.ConstantExecution.AFTER_METHOD_EXEC_A;
import static com.didekindroid.testutil.ConstantExecution.BEFORE_METHOD_EXEC;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 19/04/17
 * Time: 12:13
 */
public class CtrlerSelectionListTest {

    final static AtomicReference<String> flagMethodExec = new AtomicReference<>(BEFORE_METHOD_EXEC);

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    CtrlerSelectionList<String> controller;

    @Before
    public void setUp() throws Exception
    {
        Activity activity = activityRule.getActivity();

        ViewerSelectionList<Spinner, CtrlerSelectionList<String>, String> viewer =
                new ViewerSelectionList<Spinner, CtrlerSelectionList<String>, String>(new Spinner(activity), activity, null) {
                    @Override
                    public void initSelectedItemId(Bundle savedState)
                    {
                    }
                };

        controller = new CtrlerSelectionList<String>(viewer) {
            @Override
            public boolean loadItemsByEntitiyId(Long... entityId)
            {
                return false;
            }
        };
    }

    @Test
    public void testOnSuccessLoadItemsInList() throws Exception
    {
        controller.onSuccessLoadItemsInList(new ArrayList<String>());
        assertThat(flagMethodExec.getAndSet(BEFORE_METHOD_EXEC), is(AFTER_METHOD_EXEC_A));
    }
}