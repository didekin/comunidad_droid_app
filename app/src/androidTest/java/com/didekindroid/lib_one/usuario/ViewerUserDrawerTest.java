package com.didekindroid.lib_one.usuario;

import android.content.Intent;
import android.os.Bundle;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.PopupMenu;
import android.view.Menu;

import com.didekindroid.R;
import com.didekindroid.lib_one.api.ActivityDrawerMock;
import com.didekindroid.lib_one.api.ActivityNextMock;
import com.didekindroid.lib_one.api.exception.UiException;
import com.didekindroid.lib_one.api.router.MnRouterIf;
import com.didekindroid.lib_one.api.router.RouterInitializerMock;
import com.didekindroid.lib_one.testutil.EspressoTestUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.contrib.NavigationViewActions.navigateTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static android.view.Gravity.LEFT;
import static android.view.Gravity.START;
import static com.didekindroid.lib_one.RouterInitializer.routerInitializer;
import static com.didekindroid.lib_one.testutil.EspressoTestUtil.checkUp;
import static com.didekindroid.lib_one.testutil.EspressoTestUtil.clickNavigateUp;
import static com.didekindroid.lib_one.testutil.EspressoTestUtil.isViewDisplayed;
import static com.didekindroid.lib_one.testutil.MockTestConstant.nextMockAcLayout;
import static com.didekindroid.lib_one.usuario.UserTestData.CleanUserEnum.CLEAN_RODRIGO;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanOptions;
import static com.didekindroid.lib_one.usuario.UserTestData.cleanWithTkhandler;
import static com.didekindroid.lib_one.usuario.UserTestData.comu_real_rodrigo;
import static com.didekindroid.lib_one.usuario.UserTestData.regUserComuWithTkCache;
import static com.didekindroid.lib_one.usuario.UserTestData.user_crodrigo;
import static com.didekindroid.lib_one.usuario.UsuarioBundleKey.user_alias;
import static com.didekindroid.lib_one.usuario.UsuarioMockDao.usuarioMockDao;
import static com.didekindroid.lib_one.usuario.ViewerUserDrawer.newViewerDrawerMain;
import static com.didekindroid.lib_one.util.DrawerConstant.default_header_no_reg_user;
import static com.didekindroid.lib_one.util.DrawerConstant.header_textview_rsId;
import static com.didekindroid.lib_one.util.DrawerConstant.nav_view_rsId;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: pedro@didekin
 * Date: 21/02/2018
 * Time: 11:49
 */
@SuppressWarnings("ConstantConditions")
@RunWith(AndroidJUnit4.class)
public class ViewerUserDrawerTest {

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
    public void test_NewViewerDrawerMain() throws Exception
    {
        // Check navigationView
        assertThat(viewer.navView.getId(), is(nav_view_rsId));
        // Check header.
        assertThat(viewer.drawerHeaderRot.getId(), is(header_textview_rsId));

        cleanOptions(CLEAN_RODRIGO);
    }

    @Test
    public void test_DoViewInViewer_1() throws Exception
    {
        // Precondition: user registered.
        assertThat(viewer.getController().isRegisteredUser(), is(true));
        // Exec.
        activity.runOnUiThread(() -> viewer.doViewInViewer(null, null));
        // Viewer in activity is still null.
        activity.setViewerDrawer(viewer);
        clickNavigateUp();
        // Check.
        waitAtMost(8, SECONDS).until(isViewDisplayed(
                allOf(
                        withText(user_crodrigo.getAlias()),
                        withId(header_textview_rsId)
                )
        ));

        cleanOptions(CLEAN_RODRIGO);
    }

    @Test
    public void test_DoViewInViewer_2() throws Exception
    {
        // Precondition: user not registered.
        cleanWithTkhandler();
        assertThat(viewer.getController().isRegisteredUser(), is(false));
        // Exec.
        activity.runOnUiThread(() -> viewer.doViewInViewer(null, null));
        // Viewer in activity is still null.
        activity.setViewerDrawer(viewer);
        clickNavigateUp();
        // Check.
        waitAtMost(6, SECONDS).until(() -> {
                    onView(
                            allOf(withText(default_header_no_reg_user), withId(header_textview_rsId))).check(matches(isDisplayed())
                    );
                    return true;
                }
        );

        usuarioMockDao.deleteUser(user_crodrigo.getUserName());
    }

    @Test
    public void test_DoViewForRegUser_1() throws Exception
    {
        // Precondition: saveState == null.
        viewer.doViewForRegUser(null);
        waitAtMost(6, SECONDS).until(() -> viewer.drawerHeaderRot.getText().equals(user_crodrigo.getAlias()));

        cleanOptions(CLEAN_RODRIGO);
    }

    @Test
    public void test_DoViewForRegUser_2() throws Exception
    {
        // Precondition: saveState != null.
        Bundle bundle = new Bundle(1);
        bundle.putString(user_alias.key, "alias_mock");
        activity.runOnUiThread(() -> viewer.doViewForRegUser(bundle));
        waitAtMost(6, SECONDS).until(() -> viewer.drawerHeaderRot.getText().equals("alias_mock"));

        cleanOptions(CLEAN_RODRIGO);
    }

    @Test
    public void test_SaveState_1() throws Exception
    {
        // Preconditions: user is registered.
        viewer.doViewInViewer(null, null);
        waitAtMost(6, SECONDS).until(() -> viewer.drawerHeaderRot.getText().equals(user_crodrigo.getAlias()));
        Bundle bundleState = new Bundle(1);
        // Exec.
        viewer.saveState(bundleState);
        // Check.
        assertThat(bundleState.getString(user_alias.key), is(user_crodrigo.getAlias()));

        cleanOptions(CLEAN_RODRIGO);
    }

    @Test
    public void test_SaveState_2() throws Exception
    {
        // Precondition: user is NOT registered.
        viewer.doViewInViewer(null, null);
        Bundle bundleState = new Bundle(1);
        viewer.saveState(bundleState);
        assertThat(bundleState.getString(user_alias.key), notNullValue());

        cleanOptions(CLEAN_RODRIGO);
    }

    @Test
    public void test_OpenDrawer() throws Exception
    {
        viewer.doViewInViewer(null, null);
        // Viewer in activity is still null.
        activity.setViewerDrawer(viewer);
        // Precondition: drawer is closed.
        onView(withId(activity.getDrawerDecoratedView().getId())).check(matches(isClosed(START)));
        // Exec.
        clickNavigateUp();
        // Check if drawer is open
        onView(withId(activity.getDrawerDecoratedView().getId())).check(matches(isOpen(LEFT)));

        cleanOptions(CLEAN_RODRIGO);
    }

    @Test
    public void test_BuildMenu_1() throws Exception
    {
        activity.runOnUiThread(() -> viewer.buildMenu(viewer.navView, true));
        TimeUnit.SECONDS.sleep(2);
        checkVisibleMenuItems(true, viewer.navView.getMenu());

        cleanOptions(CLEAN_RODRIGO);
    }

    @Test
    public void test_BuildMenu_2() throws Exception
    {
        activity.runOnUiThread(() -> viewer.buildMenu(viewer.navView, false));
        TimeUnit.SECONDS.sleep(2);
        checkVisibleMenuItems(false, viewer.navView.getMenu());

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
        onView(withId(nav_view_rsId)).perform(navigateTo(activity.getViewerDrawer().navView.getMenu().getItem(0).getItemId()));
        // Check.
        waitAtMost(4, SECONDS).until(EspressoTestUtil.isResourceIdDisplayed(nextMockAcLayout));
        checkUp();
        onView(withId(activity.getDrawerDecoratedView().getId())).check(matches(isClosed(LEFT)));

        cleanOptions(CLEAN_RODRIGO);
    }

    private void checkVisibleMenuItems(boolean isVisibleEnabled, Menu toBeTestedMenu)
    {
        Set<Integer> toTestItemSet = new TreeSet<>();
        for (int i = 0; i < toBeTestedMenu.size(); ++i) {
            toTestItemSet.add(toBeTestedMenu.getItem(i).getItemId());
        }

        Menu expectedMenu = new PopupMenu(activity, null).getMenu();
        // XML original.
        activity.getMenuInflater().inflate(activity.getDrawerMnRsId(), expectedMenu);
        // Confidencialidad option.
        activity.getMenuInflater().inflate(R.menu.confidec_item_mn, expectedMenu);

        Set<Integer> expectedItemSet = new TreeSet<>();
        for (int i = 0; i < expectedMenu.size(); ++i) {
            expectedItemSet.add(expectedMenu.getItem(i).getItemId());
        }

        assertThat(toTestItemSet.size(), is(expectedItemSet.size()));
        assertThat(toTestItemSet.containsAll(expectedItemSet), is(true));

        if (isVisibleEnabled) {
            for (Integer itemId : toTestItemSet) {
                assertThat(toBeTestedMenu.findItem(itemId).isEnabled()
                        && toBeTestedMenu.findItem(itemId).isVisible(), is(true));
            }
        } else {
            for (Integer itemId : toTestItemSet) {
                assertThat(toBeTestedMenu.findItem(itemId).isEnabled(), is(expectedMenu.findItem(itemId).isEnabled()));
                assertThat(toBeTestedMenu.findItem(itemId).isVisible(), is(expectedMenu.findItem(itemId).isVisible()));
            }
        }
    }
}