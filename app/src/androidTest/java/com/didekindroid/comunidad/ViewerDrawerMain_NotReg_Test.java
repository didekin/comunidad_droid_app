package com.didekindroid.comunidad;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Gravity;

import com.didekindroid.security.CtrlerAuthToken;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static com.didekindroid.comunidad.ViewerDrawerMain.DynamicMenuItem.confidencialidad;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchAcLayout;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.confidencialidadLayout;
import static com.didekindroid.testutil.ActivityTestUtils.clickNavigateUp;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 16/09/17
 * Time: 13:21
 */
@RunWith(AndroidJUnit4.class)
public class ViewerDrawerMain_NotReg_Test extends ViewerDrawerMainTest {

    @Rule
    public ActivityTestRule<ComuSearchAc> activityRule = new ActivityTestRule<>(ComuSearchAc.class, true, true);

    @Before
    public void setUp()
    {
        activity = activityRule.getActivity();
        viewerDrawer = activity.viewerDrawer;
        assertThat(viewerDrawer.getController().isRegisteredUser(), is(false));
        navView = viewerDrawer.getViewInViewer().findViewById(drawer_nav_view);
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
        onView(withId(drawer_main_layout)).check(matches(isClosed(Gravity.START)));
        // Exec.
        clickNavigateUp();
        // Check if drawer is open
        onView(withId(drawer_main_layout)).check(matches(isOpen(Gravity.LEFT)));
    }

    /**
     * It tests implicitly ViewerDrawerMain.doViewInViewer() and  explicitly ViewerDrawerMain.buildMenu().
     */
    @Test
    public void test_BuildMenu() throws Exception
    {
        checkMenuItems(false);
    }

    /**
     * It tests implicitly ViewerDrawerMain.doViewInViewer() and explicitly DrawerMainMnItemSelListener.onNavigationItemSelected().
     */
    @Test
    public void test_OnNavigationItemSelected_1() throws InterruptedException
    {
        // Exec and check.
        checkMainDrawerMenu(confidencialidad.resourceId, confidencialidadLayout, comuSearchAcLayout);
    }
}