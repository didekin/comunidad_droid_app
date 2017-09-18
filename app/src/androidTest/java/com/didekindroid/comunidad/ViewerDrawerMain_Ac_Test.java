package com.didekindroid.comunidad;

import android.support.design.widget.NavigationView;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import com.didekindroid.R;
import com.didekindroid.exception.UiException;
import com.didekindroid.security.CtrlerAuthToken;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchAcLayout;
import static com.didekindroid.incidencia.testutils.IncidNavigationTestConstant.incidSeeOpenAcLayout;
import static com.didekindroid.testutil.ActivityTestUtils.checkDrawerMenu;
import static com.didekindroid.testutil.ActivityTestUtils.checkUp;
import static com.didekindroid.testutil.ActivityTestUtils.clickNavigateUp;
import static com.didekindroid.usuario.testutil.UserNavigationTestConstant.userDataAcRsId;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.USER_PEPE;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanOneUser;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.COMU_PLAZUELA5_PEPE;
import static com.didekindroid.usuariocomunidad.testutil.UserComuDataTestUtil.signUpAndUpdateTk;
import static com.didekindroid.usuariocomunidad.testutil.UserComuNavigationTestConstant.seeUserComuByUserFrRsId;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 16/09/17
 * Time: 13:21
 */
@RunWith(AndroidJUnit4.class)
public class ViewerDrawerMain_Ac_Test {

    final int drawer_main_layout = R.id.drawer_main_layout;
    final int drawer_nav_view = R.id.drawer_main_nav_view;
    @Rule
    public ActivityTestRule<ComuSearchAc> activityRule = new ActivityTestRule<>(ComuSearchAc.class, false, true);
    ComuSearchAc activity;
    ViewerDrawerMain viewerDrawer;
    NavigationView navView;

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        viewerDrawer = activity.viewerDrawer;
        navView = viewerDrawer.getViewInViewer().findViewById(R.id.drawer_main_nav_view);
        viewerDrawer.getController().updateIsRegistered(false);
    }

    //    ============================ TESTS ==============================

    @Test
    public void test_NewViewerDrawerMain() throws Exception
    {
        assertThat(viewerDrawer.getController(), isA(CtrlerAuthToken.class));
    }

    @Test
    public void test_OpenDrawer() throws Exception
    {
        // Precondition: drawer is closed.
        onView(withId(R.id.drawer_main_layout)).check(matches(isClosed(Gravity.START)));
        // Exec.
        clickNavigateUp();
        // Check if drawer is open
        onView(withId(R.id.drawer_main_layout)).check(matches(isOpen(Gravity.LEFT)));
    }

    /**
     * It tests implicitly ViewerDrawerMain.doViewInViewer() and  explicitly ViewerDrawerMain.buildMenu().
     */
    @Test
    public void test_BuildMenu_1() throws Exception
    {
        // Precondition: user not registered.
        assertThat(viewerDrawer.getController().isRegisteredUser(), is(false));
        checkMenuItems(false);
    }

    /**
     * It tests implicitly ViewerDrawerMain.doViewInViewer() and  explicitly ViewerDrawerMain.buildMenu().
     */
    @Test
    public void test_BuildMenu_2() throws Exception
    {
        // Precondition: user registered.
        setDrawerRegisteredUser();
        checkMenuItems(true);
    }

    /**
     * It tests implicitly ViewerDrawerMain.doViewInViewer() and explicitly DrawerMainMnItemSelListener.onNavigationItemSelected().
     */
    @Test
    public void test_OnNavigationItemSelected_1()
    {
        // Precondition: user not registered.
        assertThat(viewerDrawer.getController().isRegisteredUser(), is(false));
        // Exec and check.
        checkMainDrawerMenu(R.id.confidencialidad_ac_mn, R.id.confidencialidad_ac_layout);
    }

    /**
     * It tests implicitly ViewerDrawerMain.doViewInViewer() and explicitly DrawerMainMnItemSelListener.onNavigationItemSelected().
     */
    @Test
    public void test_OnNavigationItemSelected_2() throws IOException, UiException
    {
        // Precondition: user registered.
        signUpAndUpdateTk(COMU_PLAZUELA5_PEPE);
        setDrawerRegisteredUser();
        // Exec and check.
        checkMainDrawerMenu(R.id.confidencialidad_ac_mn, R.id.confidencialidad_ac_layout);
        checkMainDrawerMenu(R.id.see_usercomu_by_user_ac_mn, seeUserComuByUserFrRsId);
        checkMainDrawerMenu(R.id.user_data_ac_mn, userDataAcRsId);
        checkMainDrawerMenu(R.id.incid_see_open_by_comu_ac_mn, incidSeeOpenAcLayout);

        cleanOneUser(USER_PEPE);
    }

    //    ============================ HELPERS ==============================

    private void checkMenuItem(MenuItem item, boolean isVisibleEnabled)
    {
        assertThat(item.isEnabled(), is(isVisibleEnabled));
        assertThat(item.isVisible(), is(isVisibleEnabled));
    }

    private void checkMenuItems(boolean isVisibleEnabled)
    {
        Menu drawerMn = navView.getMenu();
        checkMenuItem(drawerMn.findItem(R.id.see_usercomu_by_user_ac_mn), isVisibleEnabled);
        checkMenuItem(drawerMn.findItem(R.id.user_data_ac_mn), isVisibleEnabled);
        checkMenuItem(drawerMn.findItem(R.id.incid_see_open_by_comu_ac_mn), isVisibleEnabled);
        checkMenuItem(drawerMn.findItem(R.id.confidencialidad_ac_mn), true);
    }

    private void setDrawerRegisteredUser()
    {
        viewerDrawer.getController().updateIsRegistered(true);
        final AtomicBoolean isRun = new AtomicBoolean(false);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                viewerDrawer.buildMenu(navView);
                isRun.compareAndSet(false, true);
            }
        });
        waitAtMost(4, SECONDS).untilTrue(isRun);
    }

    private void checkMainDrawerMenu(int menuItemId, int nexAcLayoutId)
    {
        checkDrawerMenu(drawer_main_layout, drawer_nav_view, menuItemId, nexAcLayoutId);
        checkUp(comuSearchAcLayout);
        onView(withId(drawer_main_layout)).check(matches(isClosed(Gravity.LEFT)));
    }
}