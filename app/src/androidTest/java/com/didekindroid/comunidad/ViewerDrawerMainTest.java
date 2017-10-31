package com.didekindroid.comunidad;

import android.support.design.widget.NavigationView;
import android.support.test.espresso.assertion.ViewAssertions;
import android.view.Menu;
import android.view.MenuItem;

import com.didekindroid.R;
import com.didekindroid.comunidad.ViewerDrawerMain.DynamicMenuItem;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.view.Gravity.LEFT;
import static com.didekindroid.comunidad.ViewerDrawerMain.DynamicMenuItem.confidencialidad;
import static com.didekindroid.comunidad.ViewerDrawerMain.DynamicMenuItem.default_menu;
import static com.didekindroid.comunidad.ViewerDrawerMain.DynamicMenuItem.menuItemsToDraw;
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
abstract class ViewerDrawerMainTest {

    final int drawer_main_layout = R.id.drawer_main_layout;
    final int drawer_nav_view = R.id.drawer_main_nav_view;
    NavigationView navView;
    ComuSearchAc activity;
    ViewerDrawerMain viewerDrawer;

    void checkMenuItems(boolean isVisibleEnabled)
    {
        Menu drawerMn = navView.getMenu();
        MenuItem item;
        for (DynamicMenuItem menuItem : menuItemsToDraw) {
            item = drawerMn.findItem(menuItem.resourceId);
            assertThat(item.isEnabled() && item.isVisible(), is(isVisibleEnabled));
        }
        item = drawerMn.findItem(confidencialidad.resourceId);
        assertThat(item.isEnabled() && item.isVisible(), is(true));
        onView(withId(default_menu.resourceId)).check(ViewAssertions.doesNotExist());
    }

    void checkMainDrawerMenu(int menuItemId, int nexAcLayoutId, int upLayout)
    {
        checkDrawerMenu(drawer_main_layout, drawer_nav_view, menuItemId, nexAcLayoutId);
        checkUp(upLayout);
        onView(withId(drawer_main_layout)).check(matches(isClosed(LEFT)));
    }
}
