package com.didekindroid.api;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 29/05/17
 * Time: 15:34
 */
@RunWith(AndroidJUnit4.class)
public class ViewerParentTest {

    @Rule
    public IntentsTestRule<ActivityMock> activityRule = new IntentsTestRule<>(ActivityMock.class, true, true);

    ActivityMock activity;
    ViewerParent<View, Controller> viewerParent;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        viewerParent = new ViewerParent<>(null, activity);
    }

    @Test
    public void test_SetChildViewer() throws Exception
    {
        final ViewerMock childViewer = new ViewerMock(activity);
        viewerParent.setChildViewer(childViewer);
        assertThat(viewerParent.getChildViewer(ViewerMock.class), is(childViewer));
    }
}