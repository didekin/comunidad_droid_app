package com.didekindroid.api;

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.R;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekindroid.exception.UiExceptionRouter.SEARCH_COMU_ACC;
import static com.didekindroid.testutil.ActivityTestUtils.checkManagerReplaceView;
import static com.didekindroid.testutil.ActivityTestUtils.checkProcessViewerCtrError;
import static com.didekinlib.model.comunidad.ComunidadExceptionMsg.COMUNIDAD_NOT_FOUND;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 09/03/17
 * Time: 16:18
 */
@RunWith(AndroidJUnit4.class)
public class ManagerTest {

    @Rule
    public ActivityTestRule<ActivityMock> activityRule = new ActivityTestRule<>(ActivityMock.class, true, true);

    Activity activity;
    ManagerIf<Object> manager;
    int nextViewResourceId;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        manager = new Manager(activity);
        nextViewResourceId = R.id.next_mock_ac_layout;
    }

    @Test
    public void getActivity() throws Exception
    {
        assertThat(manager.getActivity(), is(activity));
    }

    @Test
    public void processViewerError() throws Exception
    {
        assertThat(checkProcessViewerCtrError(manager, COMUNIDAD_NOT_FOUND, SEARCH_COMU_ACC), is(true));
    }

    @Test
    public void replaceRootView() throws Exception
    {
        manager.replaceRootView(null);
        checkManagerReplaceView(manager, nextViewResourceId);
    }
}