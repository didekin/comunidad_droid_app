package com.didekindroid.router;

import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.ActivityMock;
import com.didekindroid.lib_one.api.router.ContextualRouterIf;
import com.didekindroid.lib_one.api.router.RouterActionIf;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.usuario.router.UserContextName.default_no_reg_user;
import static com.didekindroid.router.DidekinContextAction.searchForComu;
import static com.didekindroid.router.testutil.UserRouterMapUtil.checkUserContextActionMap;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 19/02/2018
 * Time: 15:08
 */
@RunWith(AndroidJUnit4.class)
public class DidekinContextActionTest {

    @Rule
    public IntentsTestRule<ActivityMock> intentRule = new IntentsTestRule<>(ActivityMock.class, true, true);
    private ActivityMock activity;
    private ContextualRouterIf router = routerInitializer.get().getContextRouter();

    @Before
    public void setUp() throws Exception
    {
        activity = intentRule.getActivity();
    }

    //  ===========================================================================

    @Test
    public void test_InitStaticMap()
    {
        checkUserContextActionMap();
    }

    @Test
    public void test_default_no_reg_user()
    {
        RouterActionIf action = router.getActionFromContextNm(default_no_reg_user);
        assertThat(action, is(searchForComu));

        activity.runOnUiThread(() -> action.initActivity(activity, null, 0));
        waitAtMost(2, SECONDS).until(() -> activity.isFinishing());
    }
}