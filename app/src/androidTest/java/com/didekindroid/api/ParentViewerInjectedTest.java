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
public class ParentViewerInjectedTest {

    @Rule
    public IntentsTestRule<ActivityMock> activityRule = new IntentsTestRule<>(ActivityMock.class, true, true);

    ActivityMock activity;
    ParentViewerInjected<View, Controller> parentViewerInjected;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        parentViewerInjected = new ParentViewerInjected<>(null, activity);
    }

    @Test
    public void test_SetChildViewer() throws Exception
    {
        final ViewerMock childViewer = new ViewerMock(activity);
        parentViewerInjected.setChildViewer(childViewer);
        assertThat(parentViewerInjected.getChildViewer(ViewerMock.class), is(childViewer));
    }

    @Test
    public void test_GetChildViewersFromSuperClass() throws Exception
    {
        final ViewerMock childViewer = new ViewerMock(activity);
        parentViewerInjected.setChildViewer(childViewer);
        final Viewer<View, Controller> childViewer2 = new Viewer<>(null, activity, null);
        parentViewerInjected.setChildViewer(childViewer2);
        assertThat(parentViewerInjected.getChildViewersFromSuperClass(ViewerIf.class).size(), is(2));
    }
}