package com.didekindroid.api;

import android.app.Activity;
import android.os.Bundle;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 19/04/17
 * Time: 12:21
 */
public class ViewerSelectionListTest {

    @Rule
    public IntentsTestRule<ActivityMock> activityRule = new IntentsTestRule<>(ActivityMock.class, true, true);

    Activity activity;
    ViewerSelectionList<AdapterView, CtrlerSelectionList<String>, String> viewer;

    @Before
    public void setUp() throws Exception
    {
        activity = activityRule.getActivity();
        viewer = new ViewerSelectionList<AdapterView, CtrlerSelectionList<String>, String>(new Spinner(activity), activity, null) {
            @Override
            public void onSuccessLoadItems(List<String> incidCloseList)
            {
            }

            @Override
            public void initSelectedItemId(Bundle savedState)
            {
            }
        };
    }

    @Test
    public void testGetArrayAdapterForSpinner() throws Exception
    {
        ArrayAdapter<Integer> adapter = ViewerSelectionList.getArrayAdapterForSpinner(Integer.class, activity);
        assertThat(adapter, notNullValue());
        assertThat(adapter.getCount(), is(0));
    }

    @Test
    public void getSelectedItemId() throws Exception
    {
        viewer.setItemSelectedId(111L);
        assertThat(viewer.getSelectedItemId(), is(111L));
    }

    @Test
    public void getSelectedViewFromItemId() throws Exception
    {
        viewer.setItemSelectedId(111L);
        assertThat(viewer.getSelectedPositionFromItemId(111L), is(111));
    }
}