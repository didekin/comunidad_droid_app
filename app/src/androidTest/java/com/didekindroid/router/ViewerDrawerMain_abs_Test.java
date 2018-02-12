package com.didekindroid.router;

import android.view.Menu;
import android.view.MenuItem;

import com.didekindroid.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.view.Gravity.LEFT;
import static com.didekindroid.router.MnRouterAction.confidencialidad_mn;
import static com.didekindroid.testutil.ActivityTestUtils.checkDrawerMenu;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 30/10/2017
 * Time: 16:35
 */
@SuppressWarnings("AbstractClassWithoutAbstractMethods")
abstract class ViewerDrawerMain_abs_Test {

    final int drawer_main_layout = R.id.drawer_main_layout;
    final int drawer_nav_view = R.id.drawer_main_nav_view;
    final int drawer_header_view = R.id.drawer_main_header_text;
    ViewerDrawerMain viewerDrawer;

    void checkMenuItems(boolean isVisibleEnabled)
    {
        Menu drawerMn = viewerDrawer.navView.getMenu();
        MenuItem item;
        for (MnRouterAction menuItem : viewerDrawer.getMenuItemsToDraw()) {
            item = drawerMn.findItem(menuItem.getMnItemRsId());
            assertThat(item.isEnabled() && item.isVisible(), is(isVisibleEnabled));
        }
        item = drawerMn.findItem(confidencialidad_mn.getMnItemRsId());
        assertThat(item.isEnabled() && item.isVisible(), is(true));
    }

    void checkMainDrawerMenu(int menuItemId, int nexAcLayoutId, int upLayout)
    {
        checkDrawerMenu(drawer_main_layout, drawer_nav_view, menuItemId, nexAcLayoutId);
        checkUp(upLayout);
        onView(withId(drawer_main_layout)).check(matches(isClosed(LEFT)));
    }
}
