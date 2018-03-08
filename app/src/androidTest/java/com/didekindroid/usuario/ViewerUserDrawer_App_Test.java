package com.didekindroid.usuario;

import android.content.Intent;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.didekindroid.lib_one.api.ActivityDrawerMock;
import com.didekindroid.lib_one.api.ActivityNextMock;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.api.router.MnRouterIf;
import com.didekindroid.lib_one.api.router.RouterInitializerMock;
import com.didekindroid.lib_one.testutil.EspressoTestUtil;
import com.didekindroid.lib_one.usuario.ViewerUserDrawer;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.contrib.NavigationViewActions.navigateTo;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.view.Gravity.LEFT;
import static android.view.Gravity.START;
import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.testutil.EspressoTestUtil.checkUp;
import static com.didekindroid.lib_one.testutil.EspressoTestUtil.clickNavigateUp;
import static com.didekindroid.lib_one.testutil.MockTestConstant.nextMockAcLayout;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_RODRIGO;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.usuario.UserTestData.comu_real_rodrigo;
import static com.didekindroid.lib_one.usuario.UserTestData.regUserComuWithTkCache;
import static com.didekindroid.lib_one.usuario.ViewerUserDrawer.newViewerDrawerMain;
import static com.didekindroid.lib_one.util.DrawerConstant.nav_view_rsId;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 21/02/2018
 * Time: 11:49
 */
@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public class ViewerUserDrawer_App_Test {

    @Rule
    public IntentsTestRule<ActivityDrawerMock> intentRule = new IntentsTestRule<ActivityDrawerMock>(ActivityDrawerMock.class) {
        @Override
        protected Intent getActivityIntent()
        {
            try {
                regUserComuWithTkCache(comu_real_rodrigo);
            } catch (IOException | UiException e) {
                fail();
            }
            return new Intent();
        }
    };

    private ActivityDrawerMock activity;
    private ViewerUserDrawer viewer;

    @Before
    public void setUp()
    {
        activity = intentRule.getActivity();
        // Check method newViewerDrawerMain()
        activity.runOnUiThread(() -> viewer = newViewerDrawerMain(activity));
        // Check controller.
        waitAtMost(4, SECONDS).until(() -> viewer.getController().isRegisteredUser());
    }

    @Test
    public void test_OpenDrawer() throws Exception
    {
        viewer.doViewInViewer(null, null);
        // Viewer in activity is still null.
        activity.setViewerDrawer(viewer);
        // Precondition: drawer is closed.
        waitAtMost(2, SECONDS).until(() -> {
            onView(withId(activity.getDrawerDecoratedView().getId())).check(matches(isClosed(START)));
            return true;
        });
        // Exec.
        clickNavigateUp();
        SECONDS.sleep(1);
        // Check if drawer is open
        onView(withId(activity.getDrawerDecoratedView().getId())).check(matches(isOpen(LEFT)));

        cleanOptions(CLEAN_RODRIGO);
    }

    /**
     * It tests implicitly ViewerUserDrawer.doViewInViewer() and explicitly DrawerMainMnItemSelListener.onNavigationItemSelected().
     */
    @Test
    public void test_OnNavigationItemSelected_1() throws IOException, UiException, InterruptedException
    {
        // RouterInitializer for test.
        routerInitializer.set(new RouterInitializerMock() {
            @Override
            public MnRouterIf getMnRouter()
            {
                return menuItemRsId -> () -> ActivityNextMock.class;
            }
        });
        // Precondition.
        viewer.doViewInViewer(null, null);
        /* Viewer in activity is still null.*/
        activity.setViewerDrawer(viewer);
        // Precondition: drawer is closed.
        onView(withId(activity.getDrawerDecoratedView().getId())).check(matches(isClosed(START)));
        // Exec. The menu is built at this point.
        clickNavigateUp();
        SECONDS.sleep(1);
        onView(withId(nav_view_rsId)).perform(navigateTo(activity.getViewerDrawer().getNavView().getMenu().getItem(0).getItemId()));
        // Check.
        waitAtMost(4, SECONDS).until(EspressoTestUtil.isResourceIdDisplayed(nextMockAcLayout));
        checkUp();
        waitAtMost(4, SECONDS).until(() -> {
            onView(withId(activity.getDrawerDecoratedView().getId())).check(matches(isClosed(LEFT)));
            return true;
        });

        cleanOptions(CLEAN_RODRIGO);
    }
}