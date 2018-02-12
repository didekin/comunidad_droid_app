package com.didekindroid.router;

import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.Gravity;

import com.didekindroid.R;
import com.didekindroid.comunidad.ComuSearchAc;
import com.didekindroid.usuario.dao.CtrlerUsuario;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.comuSearchAcLayout;
import static com.didekindroid.comunidad.testutil.ComunidadNavConstant.confidencialidadLayout;
import static com.didekindroid.router.MnRouterAction.confidencialidad_mn;
import static com.didekindroid.testutil.ActivityTestUtils.clickNavigateUp;
import static com.didekindroid.usuario.testutil.UsuarioDataTestUtils.cleanWithTkhandler;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.isA;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * User: pedro@didekin
 * Date: 16/09/17
 * Time: 13:21
 */
@RunWith(AndroidJUnit4.class)
public class ViewerDrawerMain_NotReg_Test extends ViewerDrawerMain_abs_Test {

    @Rule
    public ActivityTestRule<ComuSearchAc> activityRule = new ActivityTestRule<>(ComuSearchAc.class, true, true);

    @Before
    public void setUp()
    {
        // Preconditions.
        cleanWithTkhandler();
        waitAtMost(4, SECONDS).until(() -> activityRule.getActivity().getViewerDrawer(), notNullValue());
        viewerDrawer = activityRule.getActivity().getViewerDrawer();

    }

    //    ============================ TESTS ==============================

    @Test
    public void test_NewViewerDrawerMain() throws Exception
    {
        assertThat(viewerDrawer.getController(), isA(CtrlerUsuario.class));
    }

    @Test
    public void testDoViewInViewer()
    {
        clickNavigateUp();
        onView(allOf(
                withText(R.string.app_name),
                withId(drawer_header_view)
        )).check(ViewAssertions.matches(isDisplayed()));
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
        checkMainDrawerMenu(confidencialidad_mn.getMnItemRsId(), confidencialidadLayout, comuSearchAcLayout);
    }
}